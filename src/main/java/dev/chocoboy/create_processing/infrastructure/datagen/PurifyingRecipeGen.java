package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class PurifyingRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    {
        convert(Items.WITHER_ROSE, Items.POPPY);
        convert(Items.ROTTEN_FLESH, Items.LEATHER);
        convert(Items.POISONOUS_POTATO, Items.POTATO);
        convert(Items.MYCELIUM, Items.GRASS_BLOCK);
        convert(Items.COBBLED_DEEPSLATE, Items.COBBLESTONE);
        convert(Items.DEEPSLATE, Items.STONE);
        convert(Items.SOUL_SAND, Items.SAND);
        convert(Items.SOUL_SOIL, Items.DIRT);
        convert(Items.CRYING_OBSIDIAN, Items.OBSIDIAN);
        convert(Items.FERMENTED_SPIDER_EYE, Items.SPIDER_EYE);
    }

    public PurifyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.PURIFYING;
    }
}

