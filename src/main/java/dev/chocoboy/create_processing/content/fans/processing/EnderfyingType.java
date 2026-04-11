package dev.chocoboy.create_processing.content.fans.processing;

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

public final class EnderfyingType extends AbstractFanProcessingType {

    private static final int COLOR_LIGHT = 0x7D4CFF;
    private static final int COLOR_DARK = 0x5A2BA8;

    private static final FanEntityTransformHelper.TransformationFeedback SILVERFISH_ENDERFYING_FEEDBACK =
        new FanEntityTransformHelper.TransformationFeedback(
            SoundEvents.ENDERMITE_AMBIENT,
            SoundEvents.ENDERMAN_SCREAM,
            ParticleTypes.PORTAL
        );

    public EnderfyingType() {
        super(CreateProcRecipeTypes.ENDERFYING);
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        return isValidAtBlockTag(level, pos, CreateProcTags.ENDERFYING_CATALYST_BLOCKS);
    }

    @Override
    public int getPriority() {
        return 691350;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) return;
        level.addParticle(ParticleTypes.PORTAL,
                pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
                pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        morphStandardAirFlow(particleAccess, random, COLOR_DARK, COLOR_LIGHT, 1 / 32f, ParticleTypes.PORTAL);
    }

    @Override
    protected void affectLivingEntity(LivingEntity living, Level level) {
        if (living.getType() == EntityType.SILVERFISH
                && FanEntityTransformHelper.transformMob(level, living, EntityType.ENDERMITE, SILVERFISH_ENDERFYING_FEEDBACK)) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
        living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));

        // 0.5 heart per second while the entity stays in the stream
        if (living.tickCount % 20 == 0) {
            living.hurt(level.damageSources().magic(), 1.0f);

            RandomSource rand = level.random;
            for (int i = 0; i < 6; i++) {
                level.addParticle(ParticleTypes.PORTAL,
                        living.getX() + (rand.nextDouble() - 0.5) * 1.5,
                        living.getY() + 0.3 + rand.nextDouble() * 1.2,
                        living.getZ() + (rand.nextDouble() - 0.5) * 1.5,
                        0, 0.02, 0);
            }
        }

        if (living.tickCount % 80 == 0) {
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 0, false, false));
        }

        FanProcessingSounds.playEnderfying(level, living.blockPosition());
    }
}
