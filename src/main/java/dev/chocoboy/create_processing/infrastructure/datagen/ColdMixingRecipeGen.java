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

        coldMixingChilling("snow_block_from_snowballs", b -> b
            .require(Items.SNOWBALL)
            .require(Items.SNOWBALL)
            .require(Items.SNOWBALL)
            .require(Items.SNOWBALL)
            .output(Items.SNOW_BLOCK));

        coldMixingChilling("packed_ice_from_ice_and_snow", b -> b
            .require(Items.ICE)
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .output(Items.PACKED_ICE));

        coldMixingChilling("coarse_dirt_from_dirt_and_gravel", b -> b
            .require(Items.DIRT)
            .require(Items.DIRT)
            .require(Items.GRAVEL)
            .require(Items.GRAVEL)
            .output(Items.COARSE_DIRT, 4));

        coldMixingChilling("magma_block_from_magma_cream", b -> b
            .require(Items.MAGMA_CREAM)
            .require(Items.MAGMA_CREAM)
            .require(Items.MAGMA_CREAM)
            .require(Items.MAGMA_CREAM)
            .output(Items.MAGMA_BLOCK));

        coldMixingChilling("slime_block_from_slimeballs", b -> b
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .require(Items.SLIME_BALL)
            .output(Items.SLIME_BLOCK));

        coldMixingChilling("wet_sponge_from_sponge", b -> b
            .require(Items.SPONGE)
            .require(Items.WATER_BUCKET)
            .output(Items.WET_SPONGE));

        coldMixingFreezing("blue_ice_from_snow", b -> b
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .require(Items.SNOW_BLOCK)
            .require(Items.ICE)
            .output(Items.BLUE_ICE));

        coldMixingFreezing("prismarine_bricks_from_shards", b -> b
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .output(Items.PRISMARINE_BRICKS));

        coldMixingFreezing("dark_prismarine_from_shards", b -> b
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.PRISMARINE_SHARD)
            .require(Items.INK_SAC)
            .output(Items.DARK_PRISMARINE));

        coldMixingFreezing("ice_from_water_and_snow", b -> b
            .require(Items.WATER_BUCKET)
            .require(Items.SNOW_BLOCK)
            .output(Items.ICE, 2)
            .output(Items.BUCKET));

        coldMixingFreezing("powder_snow_bucket_from_snowballs", b -> b
            .require(Items.SNOWBALL)
            .require(Items.SNOWBALL)
            .require(Items.SNOWBALL)
            .require(Items.SNOWBALL)
            .require(Items.BUCKET)
            .output(Items.POWDER_SNOW_BUCKET));

        coldMixingFreezing("calcite_from_dripstone", b -> b
            .require(Items.POINTED_DRIPSTONE)
            .require(Items.POINTED_DRIPSTONE)
            .require(Items.WATER_BUCKET)
            .output(Items.CALCITE, 2)
            .output(Items.BUCKET));
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
