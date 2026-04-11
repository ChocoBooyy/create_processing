package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;

public final class HotPressingRecipeGen extends CreateProcRecipeGen<HotPressingRecipe> {

    {
        hotPressing("iron_ingot_from_raw_iron", Items.RAW_IRON, Items.IRON_INGOT);
        hotPressing("copper_ingot_from_raw_copper", Items.RAW_COPPER, Items.COPPER_INGOT);
        hotPressing("gold_ingot_from_raw_gold", Items.RAW_GOLD, Items.GOLD_INGOT);
    }

    public HotPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.HOT_PRESSING;
    }

    private void hotPressing(String name, ItemLike input, ItemLike output) {
        create(name, b -> b
            .require(input)
            .output(output)
            .requiresHeat(HeatCondition.HEATED));
    }
}
