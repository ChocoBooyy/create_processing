package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.MagneticCondition;
import dev.chocoboy.create_processing.content.recipes.MagneticPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class MagneticPressingRecipeGen extends CreateProcRecipeGen<MagneticPressingRecipe> {

    {
        magneticPressing("iron_nugget_from_sand", Items.SAND, Items.IRON_NUGGET, MagneticCondition.EXPOSED);
        magneticPressing("flint_and_nugget_from_gravel", b -> b.require(Items.GRAVEL).output(Items.FLINT).output(1.0f, Items.IRON_NUGGET), MagneticCondition.EXPOSED);
        magneticPressing("iron_nugget_from_coarse_dirt", b -> b.require(Items.COARSE_DIRT).output(Items.DIRT).output(1.0f, Items.IRON_NUGGET), MagneticCondition.EXPOSED);

        magneticPressing("raw_iron_from_iron_ore", Items.IRON_ORE, Items.RAW_IRON, 2, MagneticCondition.WEATHERED);
        magneticPressing("raw_iron_from_deepslate_iron_ore", b -> b.require(Items.DEEPSLATE_IRON_ORE).output(Items.RAW_IRON).output(1.0f, Items.IRON_NUGGET), MagneticCondition.WEATHERED);
        magneticPressing("iron_nugget_from_red_sand", Items.RED_SAND, Items.IRON_NUGGET, 3, MagneticCondition.WEATHERED);

        magneticPressing("iron_ingot_from_raw_iron", Items.RAW_IRON, Items.IRON_INGOT, MagneticCondition.OXIDIZED);
        magneticPressing("compass_from_lodestone", Items.LODESTONE, Items.COMPASS, 4, MagneticCondition.OXIDIZED);
        magneticPressing("iron_from_chain", b -> b.require(Items.CHAIN).output(Items.IRON_INGOT).output(Items.IRON_NUGGET, 2), MagneticCondition.OXIDIZED);
    }

    public MagneticPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.MAGNETIC_PRESSING;
    }

    private void magneticPressing(String name, ItemLike input, ItemLike output, MagneticCondition cond) {
        magneticPressing(name, b -> b.require(input).output(output), cond);
    }

    private void magneticPressing(String name, ItemLike input, ItemLike output, int count, MagneticCondition cond) {
        magneticPressing(name, b -> b.require(input).output(output, count), cond);
    }

    private void magneticPressing(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<MagneticPressingRecipe>> builderOp,
            MagneticCondition cond) {
        ResourceLocation id = asResource(cond.getSerializedName() + "/" + name);
        register(prov -> {
            StandardProcessingRecipe.Builder<MagneticPressingRecipe> b = new StandardProcessingRecipe.Builder<>(
                    params -> new MagneticPressingRecipe(params).withMagneticCondition(cond),
                    id);
            builderOp.apply(b);
            b.build(prov);
        });
    }
}
