package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;

public final class HotPressingHelper {

    private HotPressingHelper() {}

    public static Optional<Map.Entry<Integer, RecipeHolder<HotPressingRecipe>>> findInBasin(
            BasinBlockEntity basin, Level level, HeatLevel basinHeatLevel) {
        var inv = basin.getInputInventory();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Optional<RecipeHolder<HotPressingRecipe>> recipe =
                CreateProcRecipeTypes.HOT_PRESSING.find(new SingleRecipeInput(stack), level);
            if (recipe.isPresent() && recipe.get().value().getRequiredHeat().testBlazeBurner(basinHeatLevel))
                return Optional.of(Map.entry(slot, recipe.get()));
        }
        return Optional.empty();
    }
}
