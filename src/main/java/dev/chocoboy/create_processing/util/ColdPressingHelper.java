package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;

public final class ColdPressingHelper {

    private ColdPressingHelper() {}

    public static Optional<Map.Entry<Integer, RecipeHolder<ColdPressingRecipe>>> findInBasin(
            BasinBlockEntity basin, Level level, ColdCondition sourceLevel) {
        var inv = basin.getInputInventory();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Optional<RecipeHolder<ColdPressingRecipe>> recipe =
                CreateProcRecipeTypes.COLD_PRESSING.find(new SingleRecipeInput(stack), level);
            if (recipe.isPresent() && sourceLevel.satisfies(recipe.get().value().getColdCondition()))
                return Optional.of(Map.entry(slot, recipe.get()));
        }
        return Optional.empty();
    }
}
