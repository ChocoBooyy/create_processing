package dev.chocoboy.create_processing.content.recipes;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ResonanceMixingRecipe extends BasinRecipe {

    public ResonanceMixingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.RESONANCE_MIXING, params);
    }

    @Override
    protected boolean canRequireHeat() {
        return false;
    }
}
