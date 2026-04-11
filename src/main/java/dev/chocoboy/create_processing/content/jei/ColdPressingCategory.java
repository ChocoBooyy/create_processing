package dev.chocoboy.create_processing.content.jei;

import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public final class ColdPressingCategory extends CreateRecipeCategory<ColdPressingRecipe> {

    private final AnimatedPress press = new AnimatedPress(false);

    public ColdPressingCategory(Info<ColdPressingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ColdPressingRecipe recipe, IFocusGroup focuses) {
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

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                .addItemStack(new ItemStack(recipe.getColdCondition().getBlock()));
    }

    @Override
    public void draw(ColdPressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
            double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 49, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        press.draw(graphics, getBackground().getWidth() / 2 - 17, 22);

        int bx = getBackground().getWidth() / 2 - 17;
        graphics.pose().pushPose();
        graphics.pose().translate(bx, 53, 200.0f);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(-15.5f));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(22.5f));
        AnimatedKinetics.defaultBlockElement(recipe.getColdCondition().getBlock().defaultBlockState())
                .atLocal(0, 1.65, 0)
                .scale(23)
                .render(graphics);
        graphics.pose().popPose();

        AllGuiTextures.JEI_HEAT_BAR.render(graphics, 4, 80);
        Component label = Component.translatable(
                "create_processing.cold_condition." + recipe.getColdCondition().getSerializedName());
        graphics.drawString(Minecraft.getInstance().font, label, 9, 86, 0x5BC0EB, false);
    }
}
