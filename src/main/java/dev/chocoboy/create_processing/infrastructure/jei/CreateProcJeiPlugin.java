package dev.chocoboy.create_processing.infrastructure.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

@JeiPlugin
public final class CreateProcJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = CreateProc.asResource("jei_plugin");

    private CreateRecipeCategory<FanRecipe> fanWitheringCategory;
    private CreateRecipeCategory<FanRecipe> fanPurifyingCategory;
    private CreateRecipeCategory<FanRecipe> fanPetrifyingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        fanWitheringCategory = new CreateRecipeCategory.Builder<>(FanRecipe.class)
            .addTypedRecipes(CreateProcRecipeTypes.WITHERING)
            .catalystStack(AllBlocks.ENCASED_FAN::asStack)
            .doubleItemIcon(AllItems.PROPELLER.get(), Blocks.WITHER_ROSE)
            .emptyBackground(178, 72)
            .build(CreateProc.asResource("fan_withering"), FanWitheringCategory::new);

        fanPurifyingCategory = new CreateRecipeCategory.Builder<>(FanRecipe.class)
            .addTypedRecipes(CreateProcRecipeTypes.PURIFYING)
            .catalystStack(AllBlocks.ENCASED_FAN::asStack)
            .doubleItemIcon(AllItems.PROPELLER.get(), Blocks.BEACON)
            .emptyBackground(178, 72)
            .build(CreateProc.asResource("fan_purifying"), FanPurifyingCategory::new);

        fanPetrifyingCategory = new CreateRecipeCategory.Builder<>(FanRecipe.class)
            .addTypedRecipes(CreateProcRecipeTypes.PETRIFYING)
            .catalystStack(AllBlocks.ENCASED_FAN::asStack)
            .doubleItemIcon(AllItems.PROPELLER.get(), AllPaletteStoneTypes.LIMESTONE.baseBlock.get())
            .emptyBackground(178, 72)
            .build(CreateProc.asResource("fan_petrifying"), FanPetrifyingCategory::new);

        registration.addRecipeCategories(fanWitheringCategory, fanPurifyingCategory, fanPetrifyingCategory);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        fanWitheringCategory.registerRecipes(registration);
        fanPurifyingCategory.registerRecipes(registration);
        fanPetrifyingCategory.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        fanWitheringCategory.registerCatalysts(registration);
        fanPurifyingCategory.registerCatalysts(registration);
        fanPetrifyingCategory.registerCatalysts(registration);
    }

    private static final class FanWitheringCategory extends ProcessingViaFanCategory<FanRecipe> {

        private FanWitheringCategory(Info<FanRecipe> info) {
            super(info);
        }

        @Override
        protected void renderAttachedBlock(GuiGraphics graphics) {
            GuiGameElement.of(Blocks.WITHER_ROSE.defaultBlockState())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
        }
    }

    private static final class FanPurifyingCategory extends ProcessingViaFanCategory<FanRecipe> {

        private FanPurifyingCategory(Info<FanRecipe> info) {
            super(info);
        }

        @Override
        protected void renderAttachedBlock(GuiGraphics graphics) {
            GuiGameElement.of(Blocks.BEACON.defaultBlockState())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
        }
    }

    private static final class FanPetrifyingCategory extends ProcessingViaFanCategory<FanRecipe> {

        private FanPetrifyingCategory(Info<FanRecipe> info) {
            super(info);
        }

        @Override
        protected void renderAttachedBlock(GuiGraphics graphics) {
            GuiGameElement.of(AllPaletteStoneTypes.LIMESTONE.baseBlock.get().defaultBlockState())
                .scale(SCALE)
                .atLocal(0, 0, 2)
                .lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
        }
    }
}
