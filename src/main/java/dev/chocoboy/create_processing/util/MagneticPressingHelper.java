package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dev.chocoboy.create_processing.content.recipes.MagneticCondition;
import dev.chocoboy.create_processing.content.recipes.MagneticPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Optional;

public final class MagneticPressingHelper {

    private MagneticPressingHelper() {}

    public static Optional<Map.Entry<Integer, RecipeHolder<MagneticPressingRecipe>>> findInBasin(
            BasinBlockEntity basin, Level level, MagneticCondition sourceLevel) {
        var inv = basin.getInputInventory();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Optional<RecipeHolder<MagneticPressingRecipe>> recipe =
                CreateProcRecipeTypes.MAGNETIC_PRESSING.find(new SingleRecipeInput(stack), level);
            if (recipe.isPresent() && sourceLevel.satisfies(recipe.get().value().getMagneticCondition())) {
                FilteringBehaviour filter = basin.getFilter();
                var results = recipe.get().value().getRollableResults();
                if (filter != null && !results.isEmpty() && !filter.test(results.get(0).getStack()))
                    continue;
                return Optional.of(Map.entry(slot, recipe.get()));
            }
        }
        return Optional.empty();
    }
}
