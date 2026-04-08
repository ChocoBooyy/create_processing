package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class PurifyingRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    GeneratedRecipe POPPY = convert(Items.WITHER_ROSE, Items.POPPY);
    GeneratedRecipe LEATHER = convert(Items.ROTTEN_FLESH, Items.LEATHER);
    GeneratedRecipe POTATO = convert(Items.POISONOUS_POTATO, Items.POTATO);
    GeneratedRecipe GRASS_BLOCK = convert(Items.MYCELIUM, Items.GRASS_BLOCK);
    GeneratedRecipe COBBLESTONE = convert(Items.COBBLED_DEEPSLATE, Items.COBBLESTONE);
    GeneratedRecipe STONE = convert(Items.DEEPSLATE, Items.STONE);
    GeneratedRecipe SAND = convert(Items.SOUL_SAND, Items.SAND);
    GeneratedRecipe DIRT = convert(Items.SOUL_SOIL, Items.DIRT);
    GeneratedRecipe OBSIDIAN = convert(Items.CRYING_OBSIDIAN, Items.OBSIDIAN);
    GeneratedRecipe SPIDER_EYE = convert(Items.FERMENTED_SPIDER_EYE, Items.SPIDER_EYE);

    public PurifyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.PURIFYING;
    }
}

