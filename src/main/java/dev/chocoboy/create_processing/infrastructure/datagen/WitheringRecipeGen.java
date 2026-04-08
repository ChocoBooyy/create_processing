package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class WitheringRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    GeneratedRecipe ROTTEN_FLESH = create("meat_to_rotten_flesh", b -> b
        .require(CreateProcTags.MEAT)
        .output(Items.ROTTEN_FLESH));

    GeneratedRecipe WITHER_ROSE = create("small_flowers_to_wither_rose", b -> b
        .require(CreateProcTags.minecraftItemTag("small_flowers"))
        .output(Items.WITHER_ROSE));

    GeneratedRecipe DEAD_BUSH = create("tall_flowers_to_dead_bush", b -> b
        .require(CreateProcTags.minecraftItemTag("tall_flowers"))
        .output(Items.DEAD_BUSH));

    GeneratedRecipe COAL = convert(Items.CHARCOAL, Items.COAL);
    GeneratedRecipe POISONOUS_POTATO = convert(Items.POTATO, Items.POISONOUS_POTATO);
    GeneratedRecipe COBBLED_DEEPSLATE = convert(Items.COBBLESTONE, Items.COBBLED_DEEPSLATE);
    GeneratedRecipe DEEPSLATE = convert(Items.STONE, Items.DEEPSLATE);
    GeneratedRecipe CLAY = convert(Items.SLIME_BALL, Items.CLAY_BALL);
    GeneratedRecipe MYCELIUM = convert(Items.GRASS_BLOCK, Items.MYCELIUM);

    public WitheringRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.WITHERING;
    }
}
