package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public final class EnderfyingRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    GeneratedRecipe

        CHORUS_FRUIT = create("chorus_fruit_from_apple", b -> b
            .require(Items.APPLE)
            .output(.50f, Items.CHORUS_FRUIT)),

        ENDER_PEARL = create("ender_pearl_from_magma_cream", b -> b
            .require(Items.MAGMA_CREAM)
            .output(.15f, Items.ENDER_PEARL)),

        GHAST_TEAR = create("ghast_tear_from_blue_ice", b -> b
            .require(Items.BLUE_ICE)
            .output(.10f, Items.GHAST_TEAR)
            .output(.05f, Items.GHAST_TEAR)),

        DRAGON_BREATH = convert(Items.POTION, Items.DRAGON_BREATH),
        END_STONE = convert(Items.DEEPSLATE, Items.END_STONE),
        CRYING_OBSIDIAN = convert(Items.OBSIDIAN, Items.CRYING_OBSIDIAN),
        END_ROD = convert(Items.BLAZE_ROD, Items.END_ROD);

    public EnderfyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.ENDERFYING;
    }
}
