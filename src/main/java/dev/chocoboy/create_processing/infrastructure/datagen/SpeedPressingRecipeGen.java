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

        speedPressing(id("overdrive/basic", "obsidian_from_crying_obsidian"), Blocks.CRYING_OBSIDIAN, Blocks.OBSIDIAN, SpeedCondition.OVERDRIVE);
        speedPressing(id("overdrive/basic", "amethyst_shard_from_amethyst_block"), Blocks.AMETHYST_BLOCK, Items.AMETHYST_SHARD, 4, SpeedCondition.OVERDRIVE);
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

