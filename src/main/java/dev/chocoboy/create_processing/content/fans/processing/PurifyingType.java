package dev.chocoboy.create_processing.content.fans.processing;

import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class PurifyingType extends AbstractFanProcessingType {

    private static final int COLOR_DARK = 0x8fcfe8;
    private static final int COLOR_LIGHT = 0xe4f8ff;
    private static final FanEntityTransformHelper.TransformationFeedback ZOMBIE_VILLAGER_PURIFYING_FEEDBACK =
        new FanEntityTransformHelper.TransformationFeedback(
            SoundEvents.BEACON_POWER_SELECT,
            SoundEvents.ZOMBIE_VILLAGER_CURE,
            ParticleTypes.HAPPY_VILLAGER
        );

    public PurifyingType() {
        super(CreateProcRecipeTypes.PURIFYING);
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        return isValidAtTags(level, pos,
            CreateProcTags.PURIFYING_CATALYST_FLUIDS,
            CreateProcTags.PURIFYING_CATALYST_BLOCKS);
    }

    @Override
    public int getPriority() {
        return 691290;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) return;
        Vector3f color = new Color(COLOR_LIGHT).asVectorF();
        level.addParticle(new DustParticleOptions(color, 1),
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
        level.addParticle(ParticleTypes.END_ROD,
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(COLOR_DARK, COLOR_LIGHT, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextFloat() < 1 / 24f)
            particleAccess.spawnExtraParticle(ParticleTypes.END_ROD, 0.125f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) return;

        if (entity.getType() == EntityType.ZOMBIE_VILLAGER
                && FanEntityTransformHelper.purifyZombieVillager(level, entity, ZOMBIE_VILLAGER_PURIFYING_FEEDBACK)) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 0, false, false));
        living.removeEffect(MobEffects.WITHER);
        living.removeEffect(MobEffects.POISON);
        living.removeEffect(MobEffects.WEAKNESS);
        living.removeEffect(MobEffects.BLINDNESS);
        living.removeEffect(MobEffects.HUNGER);


        if (living.tickCount % 20 == 0) {
            FanProcessingSounds.playPurifying(level, entity.blockPosition());
        }
    }
}
