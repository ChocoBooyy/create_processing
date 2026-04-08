package dev.chocoboy.create_processing.infrastructure.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

@JeiPlugin
public final class CreateProcJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = CreateProc.asResource("jei_plugin");

    private CreateRecipeCategory<FanRecipe> fanWitheringCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        fanWitheringCategory = new CreateRecipeCategory.Builder<>(FanRecipe.class)
            .addTypedRecipes(CreateProcRecipeTypes.WITHERING)
            .catalystStack(() -> {
                ItemStack fan = AllBlocks.ENCASED_FAN.asStack();
                fan.set(DataComponents.CUSTOM_NAME,
                    Component.translatable("create_processing.recipe.fan_withering.fan")
                        .withStyle(s -> s.withItalic(false)));
                return fan;
            })
            .doubleItemIcon(AllItems.PROPELLER.get(), Blocks.WITHER_ROSE)
            .emptyBackground(178, 72)
            .build(CreateProc.asResource("fan_withering"), FanWitheringCategory::new);

        registration.addRecipeCategories(fanWitheringCategory);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        fanWitheringCategory.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        fanWitheringCategory.registerCatalysts(registration);
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
}
