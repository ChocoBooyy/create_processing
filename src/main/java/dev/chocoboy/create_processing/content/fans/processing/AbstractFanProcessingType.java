package dev.chocoboy.create_processing.content.fans.processing;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes.RecipeTypeEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

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

    protected static boolean isValidAtTags(Level level, BlockPos pos,
            TagKey<Fluid> fluidTag, TagKey<Block> blockTag) {
        return level.getFluidState(pos).is(fluidTag) || level.getBlockState(pos).is(blockTag);
    }
}
