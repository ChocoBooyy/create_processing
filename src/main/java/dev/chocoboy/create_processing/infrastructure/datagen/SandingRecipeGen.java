package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public final class SandingRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    {
        convert(Blocks.EXPOSED_COPPER, Blocks.COPPER_BLOCK);
        convert(Blocks.WEATHERED_COPPER, Blocks.EXPOSED_COPPER);
        convert(Blocks.OXIDIZED_COPPER, Blocks.WEATHERED_COPPER);

        convert(Blocks.EXPOSED_CUT_COPPER, Blocks.CUT_COPPER);
        convert(Blocks.WEATHERED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER);
        convert(Blocks.OXIDIZED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER);

        convert(Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.CUT_COPPER_SLAB);
        convert(Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB);
        convert(Blocks.OXIDIZED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB);

        convert(Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.CUT_COPPER_STAIRS);
        convert(Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS);
        convert(Blocks.OXIDIZED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS);

        convert(Items.POLISHED_ANDESITE, Items.ANDESITE);
        convert(Items.POLISHED_ANDESITE_SLAB, Items.ANDESITE_SLAB);
        convert(Items.POLISHED_ANDESITE_STAIRS, Items.ANDESITE_STAIRS);

        convert(Items.POLISHED_GRANITE, Items.GRANITE);
        convert(Items.POLISHED_GRANITE_SLAB, Items.GRANITE_SLAB);
        convert(Items.POLISHED_GRANITE_STAIRS, Items.GRANITE_STAIRS);

        convert(Items.POLISHED_DIORITE, Items.DIORITE);
        convert(Items.POLISHED_DIORITE_SLAB, Items.DIORITE_SLAB);
        convert(Items.POLISHED_DIORITE_STAIRS, Items.DIORITE_STAIRS);

        convert(Items.POLISHED_DEEPSLATE, Items.COBBLED_DEEPSLATE);
        convert(Items.POLISHED_DEEPSLATE_SLAB, Items.COBBLED_DEEPSLATE_SLAB);
        convert(Items.POLISHED_DEEPSLATE_STAIRS, Items.COBBLED_DEEPSLATE_STAIRS);
        convert(Items.POLISHED_DEEPSLATE_WALL, Items.COBBLED_DEEPSLATE_WALL);

        convert(Items.POLISHED_BASALT, Items.BASALT);
        convert(Items.MUD, Items.PACKED_MUD);
        convert(Items.WARPED_NYLIUM, Items.NETHERRACK);
        convert(Items.CRIMSON_NYLIUM, Items.NETHERRACK);
        convert(Items.MAGMA_BLOCK, Items.NETHERRACK);
    }

    public SandingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.SANDING;
    }
}
