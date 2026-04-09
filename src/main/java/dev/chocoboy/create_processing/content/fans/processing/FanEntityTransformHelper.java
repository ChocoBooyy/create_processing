package dev.chocoboy.create_processing.content.fans.processing;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Level;

public final class FanEntityTransformHelper {

    private static final int TRANSFORMATION_TICKS = 50;
    private static final int PROGRESS_SOUND_INTERVAL_TICKS = 10;
    private static final float PROGRESS_MIN_PITCH = 0.55f;
    private static final float PROGRESS_MAX_PITCH = 1.35f;
    private static final String TRANSFORMATION_KEY_PREFIX = "create_processing_transform_";
    private static final String PURIFY_ZOMBIE_VILLAGER_KEY = "create_processing_purify_zombie_villager";

    private FanEntityTransformHelper() {
    }

    public static boolean purifyZombieVillager(Level level, Entity entity, TransformationFeedback feedback) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof ZombieVillager source)) {
            return false;
        }

        if (advanceTransformation(serverLevel, source, PURIFY_ZOMBIE_VILLAGER_KEY, feedback)) {
            Villager villager = createVillager(serverLevel);
            if (villager == null) {
                return false;
            }

            copyMobState(source, villager);

            configurePurifiedVillager(serverLevel, source, villager);

            if (!serverLevel.addFreshEntity(villager)) {
                return false;
            }

            clearTransformationProgress(source, PURIFY_ZOMBIE_VILLAGER_KEY);
            source.discard();
            return true;
        }

        return false;
    }

    public static boolean transformMob(Level level, Entity entity, EntityType<? extends Mob> targetType, TransformationFeedback feedback) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof Mob sourceMob)) {
            return false;
        }

        String transformationKey = transformationKey(targetType);
        if (advanceTransformation(serverLevel, sourceMob, transformationKey, feedback)) {
            Mob targetMob = createMob(serverLevel, targetType);
            if (targetMob == null) {
                return false;
            }

            copyMobState(sourceMob, targetMob);

            if (!serverLevel.addFreshEntity(targetMob)) {
                return false;
            }

            clearTransformationProgress(sourceMob, transformationKey);
            sourceMob.discard();
            return true;
        }

        return false;
    }

    private static Villager createVillager(ServerLevel serverLevel) {
        return EntityType.VILLAGER.create(serverLevel);
    }

    private static Mob createMob(ServerLevel serverLevel, EntityType<? extends Mob> targetType) {
        return targetType.create(serverLevel);
    }

    private static void configurePurifiedVillager(ServerLevel serverLevel, ZombieVillager source, Villager villager) {
        VillagerType biomeType = VillagerType.byBiome(serverLevel.getBiome(source.blockPosition()));
        villager.setVillagerData(villager.getVillagerData()
            .setType(biomeType)
            .setProfession(VillagerProfession.NONE));

        villager.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true));

        rewardNearestPlayerForPurification(serverLevel, source, villager);
    }

    private static void rewardNearestPlayerForPurification(ServerLevel serverLevel, ZombieVillager source, Villager villager) {
        Player nearestPlayer = serverLevel.getNearestPlayer(source, 32.0d);
        if (nearestPlayer != null) {
            villager.getGossips().add(
                nearestPlayer.getUUID(),
                net.minecraft.world.entity.ai.gossip.GossipType.MAJOR_POSITIVE,
                20
            );
        }
    }

    private static boolean advanceTransformation(ServerLevel level, Entity entity, String key, TransformationFeedback feedback) {
        CompoundTag data = entity.getPersistentData();
        int progress = data.getInt(key);

        if (progress % PROGRESS_SOUND_INTERVAL_TICKS == 0) {
            playProgressFeedback(level, entity, progress, feedback);
        }

        int nextProgress = progress + 1;
        if (nextProgress < TRANSFORMATION_TICKS) {
            data.putInt(key, nextProgress);
            return false;
        }

        data.putInt(key, TRANSFORMATION_TICKS);
        playCompletionFeedback(level, entity, feedback);
        return true;
    }

    private static void clearTransformationProgress(Entity entity, String key) {
        entity.getPersistentData().remove(key);
    }

    private static String transformationKey(EntityType<?> targetType) {
        return TRANSFORMATION_KEY_PREFIX + BuiltInRegistries.ENTITY_TYPE.getKey(targetType).getPath();
    }

    private static void copyMobState(Mob sourceMob, Mob targetMob) {
        targetMob.moveTo(sourceMob.getX(), sourceMob.getY(), sourceMob.getZ(), sourceMob.getYRot(), sourceMob.getXRot());
        targetMob.setDeltaMovement(sourceMob.getDeltaMovement());
        targetMob.setNoAi(sourceMob.isNoAi());
        targetMob.setSilent(sourceMob.isSilent());

        if (sourceMob.hasCustomName()) {
            targetMob.setCustomName(sourceMob.getCustomName());
            targetMob.setCustomNameVisible(sourceMob.isCustomNameVisible());
        }

        float healthRatio = sourceMob.getHealth() / Math.max(1.0f, sourceMob.getMaxHealth());
        targetMob.setHealth(Math.max(1.0f, targetMob.getMaxHealth() * healthRatio));
    }

    private static void playProgressFeedback(ServerLevel level, Entity entity, int progress, TransformationFeedback feedback) {
        playProgressSound(level, entity, progress, feedback);
        playProgressParticles(level, entity, feedback);
    }

    private static void playProgressSound(ServerLevel level, Entity entity, int progress, TransformationFeedback feedback) {
        float ratio = Mth.clamp(progress / (float) (TRANSFORMATION_TICKS - 1), 0.0f, 1.0f);
        float pitch = Mth.lerp(ratio, PROGRESS_MIN_PITCH, PROGRESS_MAX_PITCH);
        level.playSound(null, entity.blockPosition(), feedback.progressSound(), SoundSource.NEUTRAL, 0.85f, pitch);
    }

    private static void playProgressParticles(ServerLevel level, Entity entity, TransformationFeedback feedback) {
        if (feedback.progressParticle() != null) {
            level.sendParticles(
                feedback.progressParticle(),
                entity.getX(), entity.getY(0.5d), entity.getZ(),
                4,
                entity.getBbWidth() * 0.35d,
                entity.getBbHeight() * 0.25d,
                entity.getBbWidth() * 0.35d,
                0.02d
            );
        }
    }

    private static void playCompletionFeedback(ServerLevel level, Entity entity, TransformationFeedback feedback) {
        level.playSound(null, entity.blockPosition(), feedback.completionSound(), SoundSource.NEUTRAL,
            1.15f, 0.9f + level.random.nextFloat() * 0.2f);

        playCompletionParticles(level, entity, feedback);
    }

    private static void playCompletionParticles(ServerLevel level, Entity entity, TransformationFeedback feedback) {
        if (feedback.progressParticle() != null) {
            level.sendParticles(
                feedback.progressParticle(),
                entity.getX(), entity.getY(0.5d), entity.getZ(),
                16,
                entity.getBbWidth() * 0.45d,
                entity.getBbHeight() * 0.35d,
                entity.getBbWidth() * 0.45d,
                0.06d
            );
        }
    }

    public record TransformationFeedback(SoundEvent progressSound, SoundEvent completionSound, ParticleOptions progressParticle) {
    }
}
