package dev.chocoboy.create_processing.content.fans.processing;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes.RecipeTypeEntry;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public abstract class AbstractFanProcessingType implements FanProcessingType {

    private final RecipeTypeEntry recipeType;

    protected AbstractFanProcessingType(RecipeTypeEntry recipeType) {
        this.recipeType = recipeType;
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        return recipeType.find(new SingleRecipeInput(stack), level).isPresent();
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        return recipeType.find(new SingleRecipeInput(stack), level)
            .map(holder -> RecipeApplier.applyRecipeOn(level, stack, holder.value(), false))
            .orElse(null);
    }

    @Override
    public final void affectEntity(Entity entity, Level level) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) return;
        affectLivingEntity(living, level);
    }

    protected abstract void affectLivingEntity(LivingEntity living, Level level);

    protected static void spawnDustWithParticle(Level level, Vec3 pos, int colorLight, ParticleOptions secondary) {
        if (level.random.nextInt(8) != 0) return;
        Vector3f color = new Color(colorLight).asVectorF();
        level.addParticle(new DustParticleOptions(color, 1),
                pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
                pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
        level.addParticle(secondary,
                pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
                pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
    }

    protected static void morphStandardAirFlow(AirFlowParticleAccess access, RandomSource random, int colorDark, int colorLight, float extraChance, ParticleOptions extraParticle) {
        access.setColor(Color.mixColors(colorDark, colorLight, random.nextFloat()));
        access.setAlpha(1f);
        if (random.nextFloat() < extraChance)
            access.spawnExtraParticle(extraParticle, 0.125f);
    }

    protected static boolean isValidAtTags(Level level, BlockPos pos,
            TagKey<Fluid> fluidTag, TagKey<Block> blockTag) {
        return level.getFluidState(pos).is(fluidTag) || level.getBlockState(pos).is(blockTag);
    }

    protected static boolean isValidAtBlockTag(Level level, BlockPos pos, TagKey<Block> blockTag) {
        return level.getBlockState(pos).is(blockTag);
    }
}
