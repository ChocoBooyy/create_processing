package dev.chocoboy.create_processing.content.fans.processing;

import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class PetrifyingType extends AbstractFanProcessingType {

    private static final int COLOR_DARK = 0x5a5a5a;
    private static final int COLOR_LIGHT = 0x9e9e9e;

    public PetrifyingType() {
        super(CreateProcRecipeTypes.PETRIFYING);
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(CreateProcTags.PETRIFYING_CATALYST_BLOCKS);
    }

    @Override
    public int getPriority() {
        return 691280;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) return;
        Vector3f color = new Color(COLOR_LIGHT).asVectorF();
        level.addParticle(new DustParticleOptions(color, 1),
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
        level.addParticle(ParticleTypes.SMOKE,
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(COLOR_DARK, COLOR_LIGHT, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextFloat() < 1 / 24f)
            particleAccess.spawnExtraParticle(ParticleTypes.SMOKE, 0.125f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) return;

        boolean resistant = living instanceof IronGolem
            || living instanceof WitherBoss
            || living instanceof EnderDragon;
        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, resistant ? 0 : 1, false, false));

        if (!resistant) {
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 0, false, false));
            if (living.tickCount % 40 == 0) {
                living.hurt(level.damageSources().generic(), 1.0f);
            }
        }

        FanProcessingSounds.playPetrifying(level, entity.blockPosition());
    }
}
