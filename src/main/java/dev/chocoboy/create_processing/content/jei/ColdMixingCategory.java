package dev.chocoboy.create_processing.content.jei;

import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.chocoboy.create_processing.content.recipes.ColdMixingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.createmod.catnip.data.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public final class ColdMixingCategory extends CreateRecipeCategory<ColdMixingRecipe> {

    private final AnimatedMixer mixer = new AnimatedMixer();

    public ColdMixingCategory(Info<ColdMixingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ColdMixingRecipe recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.getIngredients());

        int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
        int i = 0;

        for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {
            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                ItemStack copy = itemStack.copy();
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            }
            builder.addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for (SizedFluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            addFluidSlot(builder, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19, fluidIngredient);
            i++;
        }

        List<ProcessingOutput> results = recipe.getRollableResults();
        List<FluidStack> fluidResults = recipe.getFluidResults();
        size = results.size() + fluidResults.size();
        i = 0;

        for (ProcessingOutput output : results) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;
            builder.addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
        for (FluidStack fluidResult : fluidResults) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;
            addFluidSlot(builder, xPosition, yPosition, fluidResult);
            i++;
        }

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81)
                .addItemStack(new ItemStack(recipe.getColdCondition().getBlock()));
    }

    @Override
    public void draw(ColdMixingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics,
            double mouseX, double mouseY) {
        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;
        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);

        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 68);
        AllGuiTextures.JEI_HEAT_BAR.render(graphics, 4, 80);

        int bx = getBackground().getWidth() / 2 + 3;
        graphics.pose().pushPose();
        graphics.pose().translate(bx, 53, 200.0f);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(-15.5f));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(22.5f));
        AnimatedKinetics.defaultBlockElement(recipe.getColdCondition().getBlock().defaultBlockState())
                .atLocal(0, 1.65, 0)
                .scale(23)
                .render(graphics);
        graphics.pose().popPose();

        Component label = Component.translatable(
                "create_processing.cold_condition." + recipe.getColdCondition().getSerializedName());
        graphics.drawString(Minecraft.getInstance().font, label, 9, 86, 0x5BC0EB, false);

        mixer.draw(graphics, bx, 34);
    }
}
