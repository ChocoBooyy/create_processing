package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dev.chocoboy.create_processing.content.recipes.SpeedCondition;
import dev.chocoboy.create_processing.content.recipes.SpeedMixingRecipe;
import dev.chocoboy.create_processing.content.recipes.SpeedPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public final class SpeedProcessingHelper {

    private SpeedProcessingHelper() {}

    @SuppressWarnings("unchecked")
    public static Optional<RecipeHolder<SpeedPressingRecipe>> findPressing(ItemStack stack, Level level,
            SpeedCondition machineSpeed) {
        if (machineSpeed == null) return Optional.empty();
        return level.getRecipeManager()
            .getAllRecipesFor(CreateProcRecipeTypes.SPEED_PRESSING.getType())
            .stream()
            .map(holder -> (RecipeHolder<SpeedPressingRecipe>) (RecipeHolder<?>) holder)
            .filter(holder -> machineSpeed.satisfies(holder.value().getSpeedCondition()))
            .filter(holder -> holder.value().matches(new SingleRecipeInput(stack), level))
            .max(Comparator.comparingInt(holder -> holder.value().getSpeedCondition().ordinal()));
    }

    public static Optional<Map.Entry<Integer, RecipeHolder<SpeedPressingRecipe>>> findPressingInBasin(
            BasinBlockEntity basin, Level level, SpeedCondition machineSpeed) {
        if (machineSpeed == null) return Optional.empty();

        var inv = basin.getInputInventory();
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Optional<RecipeHolder<SpeedPressingRecipe>> recipe = findPressing(stack, level, machineSpeed);
            if (recipe.isEmpty()) continue;

            FilteringBehaviour filter = basin.getFilter();
            var results = recipe.get().value().getRollableResults();
            if (filter != null && !results.isEmpty() && !filter.test(results.get(0).getStack())) continue;
            return Optional.of(Map.entry(slot, recipe.get()));
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static Optional<RecipeHolder<SpeedMixingRecipe>> findMixing(BasinBlockEntity basin, Level level,
            SpeedCondition machineSpeed) {
        if (machineSpeed == null) return Optional.empty();
        return level.getRecipeManager()
            .getAllRecipesFor(CreateProcRecipeTypes.SPEED_MIXING.getType())
            .stream()
            .map(holder -> (RecipeHolder<SpeedMixingRecipe>) (RecipeHolder<?>) holder)
            .filter(holder -> machineSpeed.satisfies(holder.value().getSpeedCondition()))
            .filter(holder -> BasinRecipe.match(basin, holder.value()))
            .max(Comparator.comparingInt(holder -> holder.value().getSpeedCondition().ordinal()));
    }
}

