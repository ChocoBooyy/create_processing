package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.SpeedCondition;
import dev.chocoboy.create_processing.content.recipes.SpeedMixingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class SpeedMixingRecipeGen extends CreateProcRecipeGen<SpeedMixingRecipe> {

    {
        speedMixing(id("fast/extraction", "green_dye_from_cactus"), b -> b.require(Items.CACTUS).require(Fluids.WATER, 250).output(Items.GREEN_DYE, 2), SpeedCondition.FAST);
        speedMixing(id("fast/extraction", "red_dye_from_beetroot"), b -> b.require(Items.BEETROOT).require(Fluids.WATER, 250).output(Items.RED_DYE, 2), SpeedCondition.FAST);
        speedMixing(id("fast/extraction", "blue_dye_from_lapis_lazuli"), b -> b.require(Items.LAPIS_LAZULI).require(Fluids.WATER, 250).output(Items.BLUE_DYE, 2), SpeedCondition.FAST);

        speedMixing(id("overdrive/extraction", "green_dye_from_cactus"), b -> b.require(Items.CACTUS).require(Fluids.WATER, 250).output(Items.GREEN_DYE, 4), SpeedCondition.OVERDRIVE);
        speedMixing(id("overdrive/extraction", "red_dye_from_beetroot"), b -> b.require(Items.BEETROOT).require(Fluids.WATER, 250).output(Items.RED_DYE, 4), SpeedCondition.OVERDRIVE);
        speedMixing(id("overdrive/extraction", "blue_dye_from_lapis_lazuli"), b -> b.require(Items.LAPIS_LAZULI).require(Fluids.WATER, 250).output(Items.BLUE_DYE, 4), SpeedCondition.OVERDRIVE);
    }

    public SpeedMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.SPEED_MIXING;
    }

    private void speedMixing(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<SpeedMixingRecipe>> builderOp,
            SpeedCondition condition) {
        ResourceLocation id = asResource(name);
        register(prov -> {
            StandardProcessingRecipe.Builder<SpeedMixingRecipe> b = new StandardProcessingRecipe.Builder<>(
                    params -> new SpeedMixingRecipe(params).withSpeedCondition(condition),
                    id);
            builderOp.apply(b);
            b.build(prov);
        });
    }

    private String id(String group, String name) {
        return group + "/" + name;
    }
}

