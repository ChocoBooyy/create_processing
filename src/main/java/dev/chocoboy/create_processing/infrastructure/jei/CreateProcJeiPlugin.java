package dev.chocoboy.create_processing.infrastructure.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.fans.processing.SandingType;
import dev.chocoboy.create_processing.content.jei.ColdMixingCategory;
import dev.chocoboy.create_processing.content.jei.ColdPressingCategory;
import dev.chocoboy.create_processing.content.jei.HotPressingCategory;
import dev.chocoboy.create_processing.content.jei.MagneticPressingCategory;
import dev.chocoboy.create_processing.content.jei.ResonanceMixingCategory;
import dev.chocoboy.create_processing.content.jei.SpeedMixingCategory;
import dev.chocoboy.create_processing.content.jei.SpeedPressingCategory;
import dev.chocoboy.create_processing.content.recipes.ColdMixingRecipe;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.MagneticPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.ResonanceMixingRecipe;
import dev.chocoboy.create_processing.content.recipes.SpeedMixingRecipe;
import dev.chocoboy.create_processing.content.recipes.SpeedPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@JeiPlugin
public final class CreateProcJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = CreateProc.asResource("jei_plugin");

    private final List<CreateRecipeCategory<?>> categories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        categories.add(buildFanCategory("fan_withering", CreateProcRecipeTypes.WITHERING, Blocks.WITHER_ROSE,builder -> {}));
        categories.add(buildFanCategory("fan_purifying", CreateProcRecipeTypes.PURIFYING, Blocks.BEACON,builder -> {}));
        categories.add(buildFanCategory("fan_sanding", CreateProcRecipeTypes.SANDING, Blocks.SAND,builder -> builder.addRecipeListConsumer(recipes -> consumeAllRecipes(holder -> {
            if (SandingType.isPolishProcessingRecipe(holder)) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                RecipeHolder<FanRecipe> casted = (RecipeHolder) holder;
                recipes.add(casted);
            }
        }))));
        categories.add(buildFanCategory("fan_petrifying", CreateProcRecipeTypes.PETRIFYING, AllPaletteStoneTypes.LIMESTONE.baseBlock.get(),builder -> {}));
        categories.add(buildFanCategory("fan_enderfying", CreateProcRecipeTypes.ENDERFYING, Blocks.CHORUS_FLOWER,builder -> {}));

        categories.add(buildCategory(
            HotPressingRecipe.class,
            "hot_pressing",
            HotPressingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.HOT_PRESSING)
                .catalystStack(AllBlocks.MECHANICAL_PRESS::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), AllBlocks.BLAZE_BURNER.get())
                .emptyBackground(177, 100)
        ));

        categories.add(buildCategory(
            ColdPressingRecipe.class,
            "cold_pressing",
            ColdPressingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.COLD_PRESSING)
                .catalystStack(AllBlocks.MECHANICAL_PRESS::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), Blocks.PACKED_ICE)
                .emptyBackground(177, 100)
        ));

        categories.add(buildCategory(
            ColdMixingRecipe.class,
            "cold_mixing",
            ColdMixingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.COLD_MIXING)
                .catalystStack(AllBlocks.MECHANICAL_MIXER::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_MIXER.get(), Blocks.PACKED_ICE)
                .emptyBackground(177, 120)
        ));

        categories.add(buildCategory(
            ResonanceMixingRecipe.class,
            "resonance_mixing",
            ResonanceMixingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.RESONANCE_MIXING)
                .catalystStack(AllBlocks.MECHANICAL_MIXER::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_MIXER.get(), Blocks.AMETHYST_BLOCK)
                .emptyBackground(177, 120)
        ));

        categories.add(buildCategory(
            MagneticPressingRecipe.class,
            "magnetic_pressing",
            MagneticPressingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.MAGNETIC_PRESSING)
                .catalystStack(AllBlocks.MECHANICAL_PRESS::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), Blocks.LODESTONE)
                .emptyBackground(177, 100)
        ));

        categories.add(buildCategory(
            SpeedPressingRecipe.class,
            "speed_pressing",
            SpeedPressingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.SPEED_PRESSING)
                .catalystStack(AllBlocks.MECHANICAL_PRESS::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), Blocks.COMPARATOR)
                .emptyBackground(177, 100)
                .addRecipeListConsumer(recipes -> recipes.sort(Comparator.comparingInt(holder ->
                    holder.value().getSpeedCondition().ordinal())))
        ));

        categories.add(buildCategory(
            SpeedMixingRecipe.class,
            "speed_mixing",
            SpeedMixingCategory::new,
            builder -> builder
                .addTypedRecipes(CreateProcRecipeTypes.SPEED_MIXING)
                .catalystStack(AllBlocks.MECHANICAL_MIXER::asStack)
                .doubleItemIcon(AllBlocks.MECHANICAL_MIXER.get(), Blocks.COMPARATOR)
                .emptyBackground(177, 120)
                .addRecipeListConsumer(recipes -> recipes.sort(Comparator.comparingInt(holder ->
                    holder.value().getSpeedCondition().ordinal())))
        ));

        registration.addRecipeCategories(categories.toArray(CreateRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        categories.forEach(cat -> cat.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        categories.forEach(cat -> cat.registerCatalysts(registration));
    }

    private static CreateRecipeCategory<FanRecipe> buildFanCategory(
            String name,
            IRecipeTypeInfo recipeType,
            Block catalystBlock,
            Consumer<CreateRecipeCategory.Builder<FanRecipe>> config) {
        return buildCategory(
                FanRecipe.class,
                name,
                info -> new FanProcessingCategory(info, catalystBlock.defaultBlockState()),
                builder -> {
                    builder
                            .addTypedRecipes(recipeType)
                            .catalystStack(AllBlocks.ENCASED_FAN::asStack)
                            .doubleItemIcon(AllItems.PROPELLER.get(), catalystBlock)
                            .emptyBackground(178, 72);
                    config.accept(builder);
                }
        );
    }

    private static <T extends Recipe<?>> CreateRecipeCategory<T> buildCategory(
            Class<T> recipeClass,
            String name,
            CreateRecipeCategory.Factory<T> factory,
            Consumer<CreateRecipeCategory.Builder<T>> config) {
        CreateRecipeCategory.Builder<T> builder = new CreateRecipeCategory.Builder<>(recipeClass);
        config.accept(builder);
        return builder.build(CreateProc.asResource(name), factory);
    }

    private static void consumeAllRecipes(Consumer<RecipeHolder<?>> consumer) {
        Objects.requireNonNull(Minecraft.getInstance().getConnection())
                .getRecipeManager()
                .getRecipes()
                .forEach(consumer);
    }

    private static final class FanProcessingCategory extends ProcessingViaFanCategory.MultiOutput<FanRecipe> {

        private final BlockState catalystBlock;

        private FanProcessingCategory(Info<FanRecipe> info, BlockState catalystBlock) {
            super(info);
            this.catalystBlock = catalystBlock;
        }

        @Override
        protected void renderAttachedBlock(GuiGraphics graphics) {
            GuiGameElement.of(catalystBlock)
                    .scale(SCALE)
                    .atLocal(0, 0, 2)
                    .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                    .render(graphics);
        }
    }
}
