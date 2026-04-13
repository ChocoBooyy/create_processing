package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.ResonanceMixingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class ResonanceMixingRecipeGen extends CreateProcRecipeGen<ResonanceMixingRecipe> {

    {
        resonanceMixing("amethyst_shard_from_calcite", Items.CALCITE, 2, Items.AMETHYST_SHARD, 2);
        resonanceMixing("tinted_glass_from_glass", Items.GLASS, Items.AMETHYST_SHARD, Items.TINTED_GLASS);
        resonanceMixing("amethyst_block_from_shards", Items.AMETHYST_SHARD, 4, Items.AMETHYST_BLOCK);
        resonanceMixing("calcite_from_smooth_basalt", Items.SMOOTH_BASALT, 2, Items.CALCITE, 2);
        resonanceMixing("smooth_basalt_from_basalt", Items.BASALT, 2, Items.SMOOTH_BASALT, 2);
        resonanceMixing("echo_shard_from_amethyst", b -> b.require(Items.AMETHYST_SHARD).require(Items.AMETHYST_SHARD).require(Items.AMETHYST_SHARD).require(Items.AMETHYST_SHARD).require(Items.ECHO_SHARD).output(Items.ECHO_SHARD, 2));
        resonanceMixing("quartz_from_calcite", Items.CALCITE, Items.AMETHYST_SHARD, Items.QUARTZ, 4);
    }

    public ResonanceMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.RESONANCE_MIXING;
    }

    private void resonanceMixing(String name, ItemLike input, int times, ItemLike output) {
        create(name, b -> {
            var builder = b;
            for (int i = 0; i < times; i++) builder = builder.require(input);
            return builder.output(output);
        });
    }

    private void resonanceMixing(String name, ItemLike input, int times, ItemLike output, int outputCount) {
        create(name, b -> {
            var builder = b;
            for (int i = 0; i < times; i++) builder = builder.require(input);
            return builder.output(output, outputCount);
        });
    }

    private void resonanceMixing(String name, ItemLike a, ItemLike b, ItemLike output) {
        create(name, builder -> builder.require(a).require(b).output(output));
    }

    private void resonanceMixing(String name, ItemLike a, ItemLike b, ItemLike output, int outputCount) {
        create(name, builder -> builder.require(a).require(b).output(output, outputCount));
    }

    private void resonanceMixing(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<ResonanceMixingRecipe>> builderOp) {
        create(name, builderOp);
    }
}
