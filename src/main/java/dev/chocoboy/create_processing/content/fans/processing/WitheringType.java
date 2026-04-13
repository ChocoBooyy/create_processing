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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class WitheringType extends AbstractFanProcessingType {

    private static final int COLOR_DARK = 0x180c30;
    private static final int COLOR_LIGHT = 0x1e0f3d;

    private static final FanEntityTransformHelper.TransformationFeedback SKELETON_WITHERING_FEEDBACK =
        new FanEntityTransformHelper.TransformationFeedback(
            SoundEvents.WITHER_SKELETON_AMBIENT,
            SoundEvents.WITHER_SKELETON_STEP,
            ParticleTypes.ASH
        );

    public WitheringType() {
        super(CreateProcRecipeTypes.WITHERING);
    }

    @Override
    protected boolean isFanProcessingEnabled() {
        return ModConfig.COMMON.enableWitheringFan.get();
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        if (!isFanProcessingEnabled()) return false;
        return isValidAtTags(level, pos,
            CreateProcTags.WITHERING_CATALYST_FLUIDS,
            CreateProcTags.WITHERING_CATALYST_BLOCKS);
    }

    @Override
    public int getPriority() {
        return 691300;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        spawnDustWithParticle(level, pos, COLOR_LIGHT, ParticleTypes.SOUL);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        morphStandardAirFlow(particleAccess, random, COLOR_DARK, COLOR_LIGHT, 1 / 32f, ParticleTypes.SOUL);
    }

    @Override
    protected void affectLivingEntity(LivingEntity living, Level level) {
        if (living.getType() == EntityType.SKELETON
                && FanEntityTransformHelper.transformMob(level, living, EntityType.WITHER_SKELETON, SKELETON_WITHERING_FEEDBACK)) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.WITHER, 30, 0, false, false));

        if (living instanceof WitherBoss witherBoss) {
            witherBoss.heal(2f);
        } else if (living instanceof WitherSkeleton witherSkeleton) {
            witherSkeleton.heal(1f);
        } else {
            FanProcessingSounds.playWithering(level, living.blockPosition());
        }
    }
}
