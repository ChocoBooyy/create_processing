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
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class ColdMixingRecipeGen extends CreateProcRecipeGen<ColdMixingRecipe> {

    {
        coldMixingChilling("mud_from_dirt", Items.DIRT, Items.SNOWBALL, Items.MUD);
        coldMixingChilling("snow_block_from_snowballs", Items.SNOWBALL, 4, Items.SNOW_BLOCK);
        coldMixingChilling("packed_ice_from_ice_and_snow", b -> b.require(Items.ICE).require(Items.SNOW_BLOCK).require(Items.SNOW_BLOCK).output(Items.PACKED_ICE));
        coldMixingChilling("coarse_dirt_from_dirt_and_gravel", b -> b.require(Items.DIRT).require(Items.DIRT).require(Items.GRAVEL).require(Items.GRAVEL).output(Items.COARSE_DIRT, 4));
        coldMixingChilling("magma_block_from_magma_cream", Items.MAGMA_CREAM, 4, Items.MAGMA_BLOCK);
        coldMixingChilling("slime_block_from_slimeballs", Items.SLIME_BALL, 9, Items.SLIME_BLOCK);
        coldMixingChilling("wet_sponge_from_sponge", Items.SPONGE, Items.WATER_BUCKET, Items.WET_SPONGE);

        coldMixingFreezing("blue_ice_from_snow", Items.SNOW_BLOCK, 4, Items.ICE, Items.BLUE_ICE);
        coldMixingFreezing("prismarine_bricks_from_shards", Items.PRISMARINE_SHARD, 9, Items.PRISMARINE_BRICKS);
        coldMixingFreezing("dark_prismarine_from_shards", Items.PRISMARINE_SHARD, 8, Items.INK_SAC, Items.DARK_PRISMARINE);
        coldMixingFreezing("ice_from_water_and_snow", b -> b.require(Items.WATER_BUCKET).require(Items.SNOW_BLOCK).output(Items.ICE, 2).output(Items.BUCKET));
        coldMixingFreezing("powder_snow_bucket_from_snowballs", Items.SNOWBALL, 4, Items.BUCKET, Items.POWDER_SNOW_BUCKET);
        coldMixingFreezing("calcite_from_dripstone", b -> b.require(Items.POINTED_DRIPSTONE).require(Items.POINTED_DRIPSTONE).require(Items.WATER_BUCKET).output(Items.CALCITE, 2).output(Items.BUCKET));
    }

    public ColdMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.COLD_MIXING;
    }

    private void coldMixingChilling(String name, ItemLike a, ItemLike b, ItemLike output) {
        coldMixingChilling(name, builder -> builder.require(a).require(b).output(output));
    }

    private void coldMixingChilling(String name, ItemLike input, int times, ItemLike output) {
        coldMixingChilling(name, b -> {
            var builder = b;
            for (int i = 0; i < times; i++) builder = builder.require(input);
            return builder.output(output);
        });
    }

    private void coldMixingChilling(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<ColdMixingRecipe>> builderOp) {
        create("chilling/" + name, builderOp);
    }

    private void coldMixingFreezing(String name, ItemLike input, int times, ItemLike extra, ItemLike output) {
        coldMixingFreezing(name, b -> {
            var builder = b;
            for (int i = 0; i < times; i++) builder = builder.require(input);
            return builder.require(extra).output(output);
        });
    }

    private void coldMixingFreezing(String name, ItemLike input, int times, ItemLike output) {
        coldMixingFreezing(name, b -> {
            var builder = b;
            for (int i = 0; i < times; i++) builder = builder.require(input);
            return builder.output(output);
        });
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
