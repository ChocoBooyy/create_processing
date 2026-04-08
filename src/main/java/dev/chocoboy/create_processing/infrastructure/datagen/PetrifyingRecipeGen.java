package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class PetrifyingRecipeGen extends CreateProcRecipeGen<FanRecipe> {

    {
        create("leaves_to_stone", b -> b
            .require(CreateProcTags.minecraftItemTag("leaves"))
            .output(Items.STONE));

        create("saplings_to_flint", b -> b
            .require(CreateProcTags.minecraftItemTag("saplings"))
            .output(Items.FLINT));

        convert(Items.SAND, Items.SANDSTONE);
        convert(Items.RED_SAND, Items.RED_SANDSTONE);

        convert(Items.MOSS_BLOCK, Items.TUFF);
        convert(Items.GRASS_BLOCK, Items.COBBLESTONE);
        convert(Items.DIRT, Items.STONE);
        convert(Items.MYCELIUM, Items.STONE);
        convert(Items.PODZOL, Items.COBBLESTONE);
        convert(Items.VINE, Items.COBBLESTONE);
        convert(Items.DEAD_BUSH, Items.FLINT);
        convert(Items.BONE, Items.CALCITE);

        convert(Items.GRAVEL, Items.COBBLESTONE);
        convert(Items.CLAY_BALL, Items.STONE);

        convert(Items.NETHERRACK, Items.BASALT);
    }

    public PetrifyingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected CreateProcRecipeTypes.RecipeTypeEntry getRecipeType() {
        return CreateProcRecipeTypes.PETRIFYING;
    }
}
