package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.content.recipes.ColdMixingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class ColdMixingHelper {

    private ColdMixingHelper() {}

    @SuppressWarnings("unchecked")
    public static Optional<RecipeHolder<ColdMixingRecipe>> findInBasin(
            BasinBlockEntity basin, Level level, ColdCondition sourceLevel) {
        return level.getRecipeManager()
            .getAllRecipesFor(CreateProcRecipeTypes.COLD_MIXING.getType())
            .stream()
            .map(holder -> (RecipeHolder<ColdMixingRecipe>) (RecipeHolder<?>) holder)
            .filter(holder -> sourceLevel.satisfies(holder.value().getColdCondition()))
            .filter(holder -> BasinRecipe.match(basin, holder.value()))
            .findFirst();
    }
}
