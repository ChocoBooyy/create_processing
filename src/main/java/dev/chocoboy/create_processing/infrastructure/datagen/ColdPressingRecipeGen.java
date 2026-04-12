package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class ColdPressingRecipeGen extends CreateProcRecipeGen<ColdPressingRecipe> {

    {
        coldPressingChilling("mud_to_clay", Items.MUD, Items.CLAY_BALL, 2);
        coldPressingChilling("sponge_from_wet_sponge", Items.WET_SPONGE, Items.SPONGE);
        coldPressingChilling("melon_slices_from_melon", Items.MELON, Items.MELON_SLICE, 9);
        coldPressingChilling("pumpkin_seeds_from_pumpkin", Items.PUMPKIN, Items.PUMPKIN_SEEDS, 4);
        coldPressingChilling("dried_kelp_from_kelp", Items.KELP, Items.DRIED_KELP, 3);
        coldPressingChilling("sandstone_from_sand", Items.SAND, Items.SANDSTONE);
        coldPressingChilling("red_sandstone_from_red_sand", Items.RED_SAND, Items.RED_SANDSTONE);
        coldPressingChilling("string_from_vine", Items.VINE, Items.STRING);
        coldPressingChilling("honey_bottle_from_honey_block", Items.HONEY_BLOCK, Items.HONEY_BOTTLE, 4);
        coldPressingChilling("slimeball_from_slime_block", Items.SLIME_BLOCK, Items.SLIME_BALL, 9);
        coldPressingChilling("snowball_from_powder_snow_bucket", Items.POWDER_SNOW_BUCKET, Items.SNOWBALL, 4);
        coldPressingChilling("rabbit_hide_from_leather", Items.LEATHER, Items.RABBIT_HIDE, 2);

        coldPressingFreezing("blue_ice_from_packed_ice", Items.PACKED_ICE, Items.BLUE_ICE);
        coldPressingFreezing("packed_ice_from_snow_block", Items.SNOW_BLOCK, Items.PACKED_ICE);
        coldPressingFreezing("prismarine_crystals_from_shard", Items.PRISMARINE_SHARD, Items.PRISMARINE_CRYSTALS, 2);
        coldPressingFreezing("obsidian_from_crying_obsidian", Items.CRYING_OBSIDIAN, Items.OBSIDIAN);
        coldPressingFreezing("blue_dye_from_lapis", Items.LAPIS_LAZULI, Items.BLUE_DYE, 4);
        coldPressingFreezing("string_from_cobweb", Items.COBWEB, Items.STRING, 9);

        coldPressingFreezingWithBonus("blaze_powder_from_magma_cream", Items.MAGMA_CREAM, Items.BLAZE_POWDER, Items.SLIME_BALL, 1.0f);
        coldPressingFreezingWithBonus("ice_from_water_bucket", Items.WATER_BUCKET, Items.ICE, Items.PACKED_ICE, 0.25f);
        coldPressingFreezing("snowball_from_snow_block", Items.SNOW_BLOCK, Items.SNOWBALL, 4);
    }

    public ColdPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.COLD_PRESSING;
    }

    private void coldPressingChilling(String name, ItemLike input, ItemLike output) {
        create("chilling/" + name, b -> b.require(input).output(output));
    }

    private void coldPressingChilling(String name, ItemLike input, ItemLike output, int count) {
        create("chilling/" + name, b -> b.require(input).output(output, count));
    }

    private void coldPressingFreezing(String name, ItemLike input, ItemLike output) {
        coldPressingFreezing(name, b -> b.require(input).output(output));
    }

    private void coldPressingFreezing(String name, ItemLike input, ItemLike output, int count) {
        coldPressingFreezing(name, b -> b.require(input).output(output, count));
    }

    private void coldPressingFreezingWithBonus(String name, ItemLike input, ItemLike output,
            ItemLike bonus, float bonusChance) {
        coldPressingFreezing(name, b -> b.require(input).output(output).output(bonusChance, bonus));
    }

    private void coldPressingFreezing(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<ColdPressingRecipe>> builderOp) {
        ResourceLocation id = asResource("freezing/" + name);
        register(prov -> {
            StandardProcessingRecipe.Builder<ColdPressingRecipe> b = new StandardProcessingRecipe.Builder<>(
                    params -> new ColdPressingRecipe(params).withColdCondition(ColdCondition.FREEZING),
                    id);
            builderOp.apply(b);
            b.build(prov);
        });
    }
}
