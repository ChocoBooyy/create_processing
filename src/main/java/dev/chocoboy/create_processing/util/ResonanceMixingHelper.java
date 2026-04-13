package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import dev.chocoboy.create_processing.content.recipes.ResonanceMixingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class ResonanceMixingHelper {

    private ResonanceMixingHelper() {}

    @SuppressWarnings("unchecked")
    public static Optional<RecipeHolder<ResonanceMixingRecipe>> findInBasin(
            BasinBlockEntity basin, Level level) {
        return level.getRecipeManager()
            .getAllRecipesFor(CreateProcRecipeTypes.RESONANCE_MIXING.getType())
            .stream()
            .map(holder -> (RecipeHolder<ResonanceMixingRecipe>) (RecipeHolder<?>) holder)
            .filter(holder -> BasinRecipe.match(basin, holder.value()))
            .findFirst();
    }
}
