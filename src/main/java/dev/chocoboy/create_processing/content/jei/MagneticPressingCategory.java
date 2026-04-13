package dev.chocoboy.create_processing.content.jei;

import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.chocoboy.create_processing.content.recipes.MagneticPressingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public final class MagneticPressingCategory extends CreateRecipeCategory<MagneticPressingRecipe> {

    private final AnimatedPress press = new AnimatedPress(false);

    public MagneticPressingCategory(Info<MagneticPressingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MagneticPressingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));

        List<ProcessingOutput> results = recipe.getRollableResults();
        for (int i = 0; i < results.size(); i++) {
            ProcessingOutput output = results.get(i);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
        }
    }

    @Override
    public void draw(MagneticPressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
            double mouseX, double mouseY) {
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        press.draw(graphics, getBackground().getWidth() / 2 + 3, 22);

        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 86);
        graphics.pose().pushPose();
        graphics.pose().translate(getBackground().getWidth() / 2 + 3, 53, 200.0f);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(-15.5f));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(22.5f));
        AnimatedKinetics.defaultBlockElement(recipe.getMagneticCondition().getBlock().defaultBlockState())
                .atLocal(0, 1.65, 0)
                .scale(23)
                .render(graphics);
        graphics.pose().popPose();
    }
}
