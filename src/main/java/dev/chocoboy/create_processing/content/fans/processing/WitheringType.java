package dev.chocoboy.create_processing.content.fans.processing;

import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class WitheringType extends AbstractFanProcessingType {

    private static final int COLOR_DARK  = 0x180c30;
    private static final int COLOR_LIGHT = 0x1e0f3d;

    public WitheringType() {
        super(CreateProcRecipeTypes.WITHERING);
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
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
        if (level.random.nextInt(8) != 0) return;
        Vector3f color = new Color(COLOR_LIGHT).asVectorF();
        level.addParticle(new DustParticleOptions(color, 1),
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
        level.addParticle(ParticleTypes.SOUL,
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(COLOR_DARK, COLOR_LIGHT, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextFloat() < 1 / 32f)
            particleAccess.spawnExtraParticle(ParticleTypes.SOUL, 0.125f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) return;
        living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 0, false, false));
        if (entity instanceof WitherBoss witherBoss) {
            witherBoss.heal(2f);
        } else if (entity instanceof WitherSkeleton witherSkeleton) {
            witherSkeleton.heal(1f);
        } else {
            level.playSound(null, entity.blockPosition(),
                SoundEvents.WITHER_SKELETON_AMBIENT, SoundSource.NEUTRAL, 0.25f, 1f);
        }
    }
}
