package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public final class ColdPressingRecipeGen extends CreateProcRecipeGen<ColdPressingRecipe> {

    {
        coldPressing("chilling/water_bucket_to_ice", Items.WATER_BUCKET, Items.ICE);
        coldPressing("chilling/snowball_to_snow_block", Items.SNOWBALL, Items.SNOW_BLOCK);
    }

    public ColdPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.COLD_PRESSING;
    }

    private void coldPressing(String name, ItemLike input, ItemLike output) {
        create(name, b -> b.require(input).output(output));
    }
}
