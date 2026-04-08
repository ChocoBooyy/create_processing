package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class WitheringRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    {
        create("meat_to_rotten_flesh", b -> b
                .require(CreateProcTags.MEAT)
                .output(Items.ROTTEN_FLESH));

        create("small_flowers_to_wither_rose", b -> b
                .require(CreateProcTags.minecraftItemTag("small_flowers"))
                .output(Items.WITHER_ROSE));

        create("tall_flowers_to_dead_bush", b -> b
                .require(CreateProcTags.minecraftItemTag("tall_flowers"))
                .output(Items.DEAD_BUSH));

        create("saplings_to_dead_bush", b -> b
                .require(CreateProcTags.minecraftItemTag("saplings"))
                .output(Items.DEAD_BUSH));

        convert(Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE);
        convert(Items.STONE_BRICKS, Items.CRACKED_STONE_BRICKS);
        convert(Items.DEEPSLATE_BRICKS, Items.CRACKED_DEEPSLATE_BRICKS);
        convert(Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES);
        convert(Items.NETHER_BRICKS, Items.CRACKED_NETHER_BRICKS);
        convert(Items.FERN, Items.DEAD_BUSH);
        convert(Items.CACTUS, Items.DEAD_BUSH);
        convert(Items.CHARCOAL, Items.COAL);
        convert(Items.POTATO, Items.POISONOUS_POTATO);
        convert(Items.COBBLESTONE, Items.COBBLED_DEEPSLATE);
        convert(Items.STONE, Items.DEEPSLATE);
        convert(Items.SLIME_BALL, Items.CLAY_BALL);
        convert(Items.GRASS_BLOCK, Items.MYCELIUM);
    }

    public WitheringRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.WITHERING;
    }
}
