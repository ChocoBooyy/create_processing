package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class HotPressingRecipeGen extends CreateProcRecipeGen<HotPressingRecipe> {

    {
        hotPressing(id("heated/base", "nether_brick_from_netherrack"), Items.NETHERRACK, Items.NETHER_BRICK);
        hotPressing(id("heated/base", "brick_from_clay_ball"), Items.CLAY_BALL, Items.BRICK);
        hotPressing(id("heated/base", "glass_from_sand"), Items.SAND, Items.GLASS);
        hotPressing(id("heated/base", "glass_from_red_sand"), Items.RED_SAND, Items.GLASS);
        hotPressing(id("heated/base", "terracotta_from_clay"), Items.CLAY, Items.TERRACOTTA);
        hotPressing(id("heated/base", "deepslate_from_cobbled_deepslate"), Items.COBBLED_DEEPSLATE, Items.DEEPSLATE);
        hotPressing(id("heated/base", "smooth_stone_from_stone"), Items.STONE, Items.SMOOTH_STONE);
        hotPressing(id("heated/base", "popped_chorus_fruit_from_chorus_fruit"), Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT);
        hotPressing(id("heated/base", "smooth_sandstone_from_sandstone"), Items.SANDSTONE, Items.SMOOTH_SANDSTONE);
        hotPressing(id("heated/base", "smooth_red_sandstone_from_red_sandstone"), Items.RED_SANDSTONE, Items.SMOOTH_RED_SANDSTONE);
        hotPressing(id("heated/base", "smooth_quartz_from_quartz_block"), Items.QUARTZ_BLOCK, Items.SMOOTH_QUARTZ);
        hotPressing(id("heated/base", "smooth_basalt_from_basalt"), Items.BASALT, Items.SMOOTH_BASALT);
        hotPressing(id("heated/base", "cracked_stone_bricks_from_stone_bricks"), Items.STONE_BRICKS, Items.CRACKED_STONE_BRICKS);
        hotPressing(id("heated/base", "cracked_nether_bricks_from_nether_bricks"), Items.NETHER_BRICKS, Items.CRACKED_NETHER_BRICKS);
        hotPressing(id("heated/base", "cracked_deepslate_bricks_from_deepslate_bricks"), Items.DEEPSLATE_BRICKS, Items.CRACKED_DEEPSLATE_BRICKS);
        hotPressing(id("heated/base", "cracked_deepslate_tiles_from_deepslate_tiles"), Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES);
        hotPressing(id("heated/base", "sponge_from_wet_sponge"), Items.WET_SPONGE, Items.SPONGE);

        hotPressing(id("heated/extraction", "green_dye_from_cactus"), Items.CACTUS, Items.GREEN_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "lime_dye_from_sea_pickle"), Items.SEA_PICKLE, Items.LIME_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "red_dye_from_beetroot"), Items.BEETROOT, Items.RED_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "yellow_dye_from_sunflower"), Items.SUNFLOWER, Items.YELLOW_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "black_dye_from_charcoal"), Items.CHARCOAL, Items.BLACK_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "blaze_powder_from_blaze_rod"), Items.BLAZE_ROD, Items.BLAZE_POWDER, 3, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "bone_meal_from_bone"), Items.BONE, Items.BONE_MEAL, 4, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "dried_kelp_from_kelp"), Items.KELP, Items.DRIED_KELP, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "sugar_from_sugar_cane"), Items.SUGAR_CANE, Items.SUGAR, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "black_dye_from_ink_sac"), Items.INK_SAC, Items.BLACK_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "blue_dye_from_lapis_lazuli"), Items.LAPIS_LAZULI, Items.BLUE_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "brown_dye_from_cocoa_beans"), Items.COCOA_BEANS, Items.BROWN_DYE, 2, HeatCondition.HEATED);
        hotPressing(id("heated/extraction", "red_dye_from_sweet_berries"), Items.SWEET_BERRIES, Items.RED_DYE, 2, HeatCondition.HEATED);

        hotPressingWithBonus(id("superheated/bonus", "flint_from_gravel"), Items.GRAVEL, Items.FLINT, 0.25f, Items.FLINT);
        hotPressingWithBonus(id("superheated/bonus", "stone_from_cobblestone"), Items.COBBLESTONE, Items.STONE, 0.25f, Items.STONE);
        hotPressingWithBonus(id("superheated/bonus", "glass_from_sand"), Items.SAND, Items.GLASS, 0.25f, Items.GLASS);
        hotPressingWithBonus(id("superheated/bonus", "glass_from_red_sand"), Items.RED_SAND, Items.GLASS, 0.25f, Items.GLASS);
        hotPressingWithBonus(id("superheated/bonus", "iron_ingot_from_raw_iron"), Items.RAW_IRON, Items.IRON_INGOT, 0.25f, Items.IRON_NUGGET);
        hotPressingWithBonus(id("superheated/bonus", "copper_ingot_from_raw_copper"), Items.RAW_COPPER, Items.COPPER_INGOT, 0.15f, Items.COPPER_INGOT);
        hotPressingWithBonus(id("superheated/bonus", "gold_ingot_from_raw_gold"), Items.RAW_GOLD, Items.GOLD_INGOT, 0.25f, Items.GOLD_NUGGET);

        addAutomaticCrushedOreRecipes();
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

    private void hotPressing(String name, ItemLike input, ItemLike output, HeatCondition heat) {
        create(name, b -> b
            .require(input)
            .output(output)
            .requiresHeat(heat));
    }

    private void hotPressing(String name, ItemLike input, ItemLike output, int count, HeatCondition heat) {
        create(name, b -> b
            .require(input)
            .output(output, count)
            .requiresHeat(heat));
    }

    private void hotPressingWithBonus(String name, ItemLike input, ItemLike output, float bonusChance, ItemLike bonusOutput) {
        create(name, b -> b
                .require(input)
                .output(output)
                .output(bonusChance, bonusOutput)
                .requiresHeat(HeatCondition.SUPERHEATED));
    }

    private void hotPressingCrushed(String name, ItemLike input, ItemLike output) {
        hotPressingWithBonus(name, input, output, 0.5f, output);
    }

    private String id(String group, String name) {
        return group + "/" + name;
    }

    private void addAutomaticCrushedOreRecipes() {
        BuiltInRegistries.ITEM.keySet()
            .stream()
            .filter(id -> id.getPath().startsWith("crushed_raw_"))
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .forEach(crushedId -> resolveIngotFor(crushedId).ifPresent(ingot -> {
                Item crushedItem = BuiltInRegistries.ITEM.get(crushedId);
                String oreName = crushedId.getPath().substring("crushed_raw_".length());
                String recipeName = id("superheated/crushed/" + crushedId.getNamespace(), oreName + "_ingot_from_" + crushedId.getPath());
                hotPressingCrushed(recipeName, crushedItem, ingot);
            }));
    }

    private Optional<ItemLike> resolveIngotFor(ResourceLocation crushedId) {
        String oreName = crushedId.getPath().substring("crushed_raw_".length());

        Optional<ItemLike> sameNamespace = findItem(crushedId.withPath(oreName + "_ingot"));
        if (sameNamespace.isPresent()) {
            return sameNamespace;
        }

        Optional<ItemLike> minecraft = findItem(ResourceLocation.withDefaultNamespace(oreName + "_ingot"));
        if (minecraft.isPresent()) {
            return minecraft;
        }

        return findItem(CreateProc.asResource(oreName + "_ingot"));
    }

    private Optional<ItemLike> findItem(ResourceLocation id) {
        return BuiltInRegistries.ITEM.containsKey(id)
            ? Optional.of(BuiltInRegistries.ITEM.get(id))
            : Optional.empty();
    }
}
