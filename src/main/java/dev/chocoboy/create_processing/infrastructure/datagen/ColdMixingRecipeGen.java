package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.content.recipes.ColdMixingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class ColdMixingRecipeGen extends CreateProcRecipeGen<ColdMixingRecipe> {

    {
        coldMixingChilling("mud_from_dirt", b -> b
            .require(Items.DIRT)
            .require(Items.SNOWBALL)
            .output(Items.MUD));

        coldMixingFreezing("blue_ice_from_snow", b -> b
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .require(Items.ICE)
            .output(Items.BLUE_ICE));
    }

    public ColdMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.COLD_MIXING;
    }

    private void coldMixingChilling(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<ColdMixingRecipe>> builderOp) {
        create("chilling/" + name, builderOp);
    }

    private void coldMixingFreezing(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<ColdMixingRecipe>> builderOp) {
        ResourceLocation id = asResource("freezing/" + name);
        register(prov -> {
            StandardProcessingRecipe.Builder<ColdMixingRecipe> b = new StandardProcessingRecipe.Builder<>(
                    params -> new ColdMixingRecipe(params).withColdCondition(ColdCondition.FREEZING),
                    id);
            builderOp.apply(b);
            b.build(prov);
        });
    }
}
