package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.api.data.recipe.StandardProcessingRecipeGen;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class CreateProcRecipeGen<R extends StandardProcessingRecipe<?>> extends StandardProcessingRecipeGen<R> {

    protected CreateProcRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String defaultNamespace) {
        super(output, registries, defaultNamespace);
    }

    @Override
    protected abstract IRecipeTypeInfo getRecipeType();

    protected GeneratedRecipe convert(ItemLike input, ItemLike output) {
        return convert(() -> Ingredient.of(input), () -> output);
    }

    protected GeneratedRecipe convert(Supplier<Ingredient> input, Supplier<ItemLike> output) {
        return create(asResource(getPath(output) + "_from_" + getPath(() -> input.get().getItems()[0].getItem())),
            builder -> builder.withItemIngredients(input.get()).output(output.get()));
    }

    protected static String getPath(Supplier<ItemLike> itemLike) {
        return RegisteredObjectsHelper.getKeyOrThrow(itemLike.get().asItem()).getPath();
    }
}
