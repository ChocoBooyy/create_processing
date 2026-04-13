package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.SpeedCondition;
import dev.chocoboy.create_processing.content.recipes.SpeedPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;

public final class SpeedPressingRecipeGen extends CreateProcRecipeGen<SpeedPressingRecipe> {

    {
        speedPressing(id("fast/basic", "glass_pane_from_glass"), Blocks.GLASS, Items.GLASS_PANE, 2, SpeedCondition.FAST);
        speedPressing(id("fast/basic", "bone_meal_from_bone_block"), Blocks.BONE_BLOCK, Items.BONE_MEAL, 9, SpeedCondition.FAST);
        speedPressing(id("fast/materials", "gravel_from_cobblestone"), Blocks.COBBLESTONE, Blocks.GRAVEL, SpeedCondition.FAST);
        speedPressing(id("fast/materials", "quartz_from_quartz_block"), Blocks.QUARTZ_BLOCK, Items.QUARTZ, 4, SpeedCondition.FAST);
        speedPressing(id("fast/materials", "pointed_dripstone_from_dripstone_block"), Blocks.DRIPSTONE_BLOCK, Items.POINTED_DRIPSTONE, 4, SpeedCondition.FAST);
        speedPressing(id("fast/materials", "wheat_from_hay_block"), Blocks.HAY_BLOCK, Items.WHEAT, 10, SpeedCondition.FAST);
        speedPressing(id("fast/materials", "honeycomb_from_honeycomb_block"), Blocks.HONEYCOMB_BLOCK, Items.HONEYCOMB, 5, SpeedCondition.FAST);
        speedPressing(id("fast/materials", "prismarine_crystals_from_sea_lantern"), Blocks.SEA_LANTERN, Items.PRISMARINE_CRYSTALS, 3, SpeedCondition.FAST);
        speedPressing(id("fast/automation", "clay_ball_from_clay"), Blocks.CLAY, Items.CLAY_BALL, 4, SpeedCondition.FAST);
        speedPressing(id("fast/automation", "brick_from_bricks"), Blocks.BRICKS, Items.BRICK, 4, SpeedCondition.FAST);
        speedPressing(id("fast/automation", "nether_brick_from_nether_bricks"), Blocks.NETHER_BRICKS, Items.NETHER_BRICK, 4, SpeedCondition.FAST);
        speedPressing(id("fast/automation", "string_from_white_wool"), Items.WHITE_WOOL, Items.STRING, 4, SpeedCondition.FAST);

        speedPressing(id("overdrive/basic", "obsidian_from_crying_obsidian"), Blocks.CRYING_OBSIDIAN, Blocks.OBSIDIAN, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/basic", "amethyst_shard_from_amethyst_block"), Blocks.AMETHYST_BLOCK, Items.AMETHYST_SHARD, 4, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/materials", "flint_from_gravel"), Blocks.GRAVEL, Items.FLINT, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/materials", "prismarine_shard_from_prismarine"), Blocks.PRISMARINE, Items.PRISMARINE_SHARD, 4, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/materials", "glowstone_dust_from_glowstone"), Blocks.GLOWSTONE, Items.GLOWSTONE_DUST, 6, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/materials", "redstone_from_redstone_block"), Blocks.REDSTONE_BLOCK, Items.REDSTONE, 10, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/materials", "nether_wart_from_nether_wart_block"), Blocks.NETHER_WART_BLOCK, Items.NETHER_WART, 10, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/materials", "prismarine_shard_from_prismarine_bricks"), Blocks.PRISMARINE_BRICKS, Items.PRISMARINE_SHARD, 10, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/automation", "clay_ball_from_clay"), Blocks.CLAY, Items.CLAY_BALL, 5, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/automation", "brick_from_bricks"), Blocks.BRICKS, Items.BRICK, 5, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/automation", "nether_brick_from_nether_bricks"), Blocks.NETHER_BRICKS, Items.NETHER_BRICK, 5, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/automation", "string_from_white_wool"), Items.WHITE_WOOL, Items.STRING, 5, SpeedCondition.OVERDRIVE);
    }

    public SpeedPressingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, CreateProc.MOD_ID);
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CreateProcRecipeTypes.SPEED_PRESSING;
    }

    private void speedPressing(String name, ItemLike input, ItemLike output, SpeedCondition condition) {
        speedPressing(name, b -> b.require(input).output(output), condition);
    }

    private void speedPressing(String name, ItemLike input, ItemLike output, int count, SpeedCondition condition) {
        speedPressing(name, b -> b.require(input).output(output, count), condition);
    }

    private void speedPressing(String name,
            UnaryOperator<StandardProcessingRecipe.Builder<SpeedPressingRecipe>> builderOp,
            SpeedCondition condition) {
        ResourceLocation id = asResource(name);
        register(prov -> {
            StandardProcessingRecipe.Builder<SpeedPressingRecipe> b = new StandardProcessingRecipe.Builder<>(
                    params -> new SpeedPressingRecipe(params).withSpeedCondition(condition),
                    id);
            builderOp.apply(b);
            b.build(prov);
        });
    }

    private String id(String group, String name) {
        return group + "/" + name;
    }
}

