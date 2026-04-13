package dev.chocoboy.create_processing.content.fans.processing;

import dev.chocoboy.create_processing.infrastructure.config.ModConfig;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    protected boolean isFanProcessingEnabled() {
        return ModConfig.COMMON.enablePurifyingFan.get();
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!isFanProcessingEnabled()) return false;
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
        spawnDustWithParticle(level, pos, COLOR_LIGHT, ParticleTypes.END_ROD);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        morphStandardAirFlow(particleAccess, random, COLOR_DARK, COLOR_LIGHT, 1 / 24f, ParticleTypes.END_ROD);
    }

    @Override
    protected void affectLivingEntity(LivingEntity living, Level level) {
        if (living.getType() == EntityType.ZOMBIE_VILLAGER
                && FanEntityTransformHelper.purifyZombieVillager(level, living, ZOMBIE_VILLAGER_PURIFYING_FEEDBACK)) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 0, false, false));
        living.removeEffect(MobEffects.WITHER);
        living.removeEffect(MobEffects.POISON);
        living.removeEffect(MobEffects.WEAKNESS);
        living.removeEffect(MobEffects.BLINDNESS);
        living.removeEffect(MobEffects.HUNGER);

        if (living.tickCount % 20 == 0) {
            FanProcessingSounds.playPurifying(level, living.blockPosition());
        }
    }
}
