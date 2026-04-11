package dev.chocoboy.create_processing.infrastructure.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import dev.chocoboy.create_processing.content.fans.processing.SandingType;
import dev.chocoboy.create_processing.content.jei.HotPressingCategory;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
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
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@JeiPlugin
public final class CreateProcJeiPlugin implements IModPlugin {

	private static final ResourceLocation PLUGIN_UID = CreateProc.asResource("jei_plugin");

	private CreateRecipeCategory<FanRecipe> fanWitheringCategory;
	private CreateRecipeCategory<FanRecipe> fanSandingCategory;
	private CreateRecipeCategory<FanRecipe> fanPurifyingCategory;
	private CreateRecipeCategory<FanRecipe> fanPetrifyingCategory;
	private CreateRecipeCategory<FanRecipe> fanEnderfyingCategory;
	private CreateRecipeCategory<HotPressingRecipe> hotPressingCategory;

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		fanWitheringCategory = buildFanCategory(
                "fan_withering",
		        CreateProcRecipeTypes.WITHERING,
		        Blocks.WITHER_ROSE,
		        FanWitheringCategory::new,
		        builder -> {}
		);

		fanPurifyingCategory = buildFanCategory(
                "fan_purifying",
		        CreateProcRecipeTypes.PURIFYING,
		        Blocks.BEACON,
		        FanPurifyingCategory::new,
		        builder -> {}
		);

		fanSandingCategory = buildFanCategory(
		        "fan_sanding",
		        CreateProcRecipeTypes.SANDING,
		        Blocks.SAND,
		        FanSandingCategory::new,
		        builder -> builder.addRecipeListConsumer(recipes -> consumeAllRecipes(recipeHolder -> {
				if (SandingType.isPolishProcessingRecipe(recipeHolder)) {
                    @SuppressWarnings({"unchecked", "rawtypes"})
					RecipeHolder<FanRecipe> casted = (RecipeHolder) recipeHolder;
					recipes.add(casted);
				}
		        }))
		);

		fanPetrifyingCategory = buildFanCategory(
		        "fan_petrifying",
		        CreateProcRecipeTypes.PETRIFYING,
		        AllPaletteStoneTypes.LIMESTONE.baseBlock.get(),
		        FanPetrifyingCategory::new,
		        builder -> {}
		);

		fanEnderfyingCategory = buildFanCategory(
		        "fan_enderfying",
		        CreateProcRecipeTypes.ENDERFYING,
		        Blocks.CHORUS_FLOWER,
		        FanEnderfyingCategory::new,
		        builder -> {}
		);

		hotPressingCategory = buildCategory(
		        HotPressingRecipe.class,
		        "hot_pressing",
		        HotPressingCategory::new,
		        builder -> builder
				.addTypedRecipes(CreateProcRecipeTypes.HOT_PRESSING)
				.catalystStack(AllBlocks.MECHANICAL_PRESS::asStack)
				.doubleItemIcon(AllBlocks.MECHANICAL_PRESS.get(), AllItems.BLAZE_CAKE.get())
				.emptyBackground(185, 100)
		);

		registration.addRecipeCategories(
		        fanWitheringCategory,
		        fanSandingCategory,
		        fanPurifyingCategory,
		        fanPetrifyingCategory,
		        fanEnderfyingCategory,
		        hotPressingCategory
		);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		fanWitheringCategory.registerRecipes(registration);
		fanSandingCategory.registerRecipes(registration);
		fanPurifyingCategory.registerRecipes(registration);
		fanPetrifyingCategory.registerRecipes(registration);
		fanEnderfyingCategory.registerRecipes(registration);
		hotPressingCategory.registerRecipes(registration);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		fanWitheringCategory.registerCatalysts(registration);
		fanSandingCategory.registerCatalysts(registration);
		fanPurifyingCategory.registerCatalysts(registration);
		fanPetrifyingCategory.registerCatalysts(registration);
		fanEnderfyingCategory.registerCatalysts(registration);
		hotPressingCategory.registerCatalysts(registration);
	}

	private static final class FanWitheringCategory extends ProcessingViaFanCategory.MultiOutput<FanRecipe> {

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

	private static final class FanPurifyingCategory extends ProcessingViaFanCategory.MultiOutput<FanRecipe> {

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

	private static final class FanSandingCategory extends ProcessingViaFanCategory.MultiOutput<FanRecipe> {

		private FanSandingCategory(Info<FanRecipe> info) {
			super(info);
		}

		@Override
		protected void renderAttachedBlock(GuiGraphics graphics) {
			GuiGameElement.of(Blocks.SAND.defaultBlockState())
				.scale(SCALE)
				.atLocal(0, 0, 2)
				.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
				.render(graphics);
		}
	}

	private static final class FanPetrifyingCategory extends ProcessingViaFanCategory.MultiOutput<FanRecipe> {

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

	private static final class FanEnderfyingCategory extends ProcessingViaFanCategory.MultiOutput<FanRecipe> {

		private FanEnderfyingCategory(Info<FanRecipe> info) {
			super(info);
		}

		@Override
		protected void renderAttachedBlock(GuiGraphics graphics) {
			GuiGameElement.of(Blocks.CHORUS_FLOWER.defaultBlockState())
				.scale(SCALE)
				.atLocal(0, 0, 2)
				.lighting(AnimatedKinetics.DEFAULT_LIGHTING)
				.render(graphics);
		}
	}

	private static void consumeAllRecipes(Consumer<RecipeHolder<?>> consumer) {
		Objects.requireNonNull(Minecraft.getInstance().getConnection())
			.getRecipeManager()
			.getRecipes()
			.forEach(consumer);
	}

	private static <T extends Recipe<?>> CreateRecipeCategory<T> buildCategory(
            Class<T> recipeClass,
	        String name,
	        CreateRecipeCategory.Factory<T> factory,
	        Consumer<CreateRecipeCategory.Builder<T>> config
	) {
		CreateRecipeCategory.Builder<T> builder = new CreateRecipeCategory.Builder<>(recipeClass);
		config.accept(builder);
		return builder.build(CreateProc.asResource(name), factory);
	}

	private static CreateRecipeCategory<FanRecipe> buildFanCategory(
            String name,
	        IRecipeTypeInfo recipeType,
	        net.minecraft.world.level.ItemLike secondaryIcon,
	        CreateRecipeCategory.Factory<FanRecipe> factory,
	        Consumer<CreateRecipeCategory.Builder<FanRecipe>> config
	) {
		return buildCategory(
		        FanRecipe.class,
		        name,
		        factory,
		        builder -> {
				builder
					.addTypedRecipes(recipeType)
					.catalystStack(AllBlocks.ENCASED_FAN::asStack)
					.doubleItemIcon(AllItems.PROPELLER.get(), secondaryIcon)
					.emptyBackground(178, 72);
				config.accept(builder);
		        }
		);
	}
}
