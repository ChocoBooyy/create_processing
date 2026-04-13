package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.MagneticCondition;
import dev.chocoboy.create_processing.content.recipes.MagneticPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class MagneticPressingRecipeGen extends CreateProcRecipeGen<MagneticPressingRecipe> {

    {
        magneticPressingExposed("iron_nugget_from_gravel", Items.GRAVEL, Items.IRON_NUGGET, 2);
        magneticPressingExposed("iron_nugget_from_red_sand", Items.RED_SAND, Items.IRON_NUGGET, 3);
        magneticPressingExposed("iron_nugget_from_crushed_raw_iron", AllItems.CRUSHED_IRON.get(), Items.IRON_NUGGET, 4);
        magneticPressingExposed("iron_nugget_from_andesite_alloy", AllItems.ANDESITE_ALLOY.get(), Items.IRON_NUGGET, 2);

        magneticPressingWeathered("raw_iron_from_iron_ore", b -> b.require(Items.IRON_ORE).output(Items.RAW_IRON, 1).output(Items.IRON_NUGGET, 2));
        magneticPressingWeathered("raw_iron_from_deepslate_iron_ore", b -> b.require(Items.DEEPSLATE_IRON_ORE).output(Items.RAW_IRON, 1).output(Items.IRON_NUGGET, 5));
        magneticPressingWeathered("gold_nugget_from_nether_gold_ore", Items.NETHER_GOLD_ORE, Items.GOLD_NUGGET, 6);
        magneticPressingWeathered("crushed_zinc_from_zinc_ore", AllBlocks.ZINC_ORE.get(), AllItems.CRUSHED_ZINC.get(), 2);
        magneticPressingWeathered("crushed_zinc_from_deepslate_zinc_ore", AllBlocks.DEEPSLATE_ZINC_ORE.get(), AllItems.CRUSHED_ZINC.get(), 4);
        magneticPressingWeathered("zinc_nugget_from_crushed_raw_zinc", AllItems.CRUSHED_ZINC.get(), AllItems.ZINC_NUGGET.get(), 4);

        magneticPressingOxidized("compass_from_lodestone", Items.LODESTONE, Items.COMPASS, 4);
        magneticPressingOxidized("components_from_electron_tube", b -> b.require(AllItems.ELECTRON_TUBE.get()).output(Items.IRON_NUGGET, 4).output(AllItems.COPPER_NUGGET.get(), 2));
    }

    public MagneticPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        registerSheetToIngotRecipes();
        super.buildRecipes(output);
    }

    private void registerSheetToIngotRecipes() {
        BuiltInRegistries.ITEM.entrySet().stream()
            .filter(e -> e.getKey().location().getPath().endsWith("_sheet"))
            .forEach(e -> {
                String path = e.getKey().location().getPath();
                String sheetNamespace = e.getKey().location().getNamespace();
                String metal = path.substring(0, path.length() - "_sheet".length());
                String ingotMetal = metal.equals("golden") ? "gold" : metal;

                TagKey<Item> plateTag = TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("c", "plates/" + ingotMetal));

                Item ingot = BuiltInRegistries.ITEM.get(
                    ResourceLocation.fromNamespaceAndPath(sheetNamespace, ingotMetal + "_ingot"));
                if (ingot == Items.AIR) {
                    ingot = BuiltInRegistries.ITEM.get(
                        ResourceLocation.fromNamespaceAndPath("minecraft", ingotMetal + "_ingot"));
                }
                if (ingot != Items.AIR) {
                    final Item finalIngot = ingot;
                    magneticPressingOxidized("plates_" + ingotMetal, b -> b.require(plateTag).output(finalIngot));
                }
            });
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.MAGNETIC_PRESSING;
    }

    private void magneticPressingExposed(String name, ItemLike input, ItemLike output, int count) {
        magneticPressing(name, b -> b.require(input).output(output, count), MagneticCondition.EXPOSED);
    }

    private void magneticPressingWeathered(String name, ItemLike input, ItemLike output, int count) {
        magneticPressing(name, b -> b.require(input).output(output, count), MagneticCondition.WEATHERED);
    }

    private void magneticPressingWeathered(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<MagneticPressingRecipe>> builderOp) {
        magneticPressing(name, builderOp, MagneticCondition.WEATHERED);
    }

    private void magneticPressingOxidized(String name, ItemLike input, ItemLike output) {
        magneticPressing(name, b -> b.require(input).output(output), MagneticCondition.OXIDIZED);
    }

    private void magneticPressingOxidized(String name, ItemLike input, ItemLike output, int count) {
        magneticPressing(name, b -> b.require(input).output(output, count), MagneticCondition.OXIDIZED);
    }

    private void magneticPressingOxidized(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<MagneticPressingRecipe>> builderOp) {
        magneticPressing(name, builderOp, MagneticCondition.OXIDIZED);
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
