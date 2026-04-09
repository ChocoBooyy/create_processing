package dev.chocoboy.create_processing.registry;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.recipes.FanRecipe;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public final class CreateProcRecipeTypes {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER =
        DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, CreateProc.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> TYPE_REGISTER =
        DeferredRegister.create(Registries.RECIPE_TYPE, CreateProc.MOD_ID);

    public static RecipeTypeEntry WITHERING;
    public static RecipeTypeEntry SANDING;
    public static RecipeTypeEntry PURIFYING;
    public static RecipeTypeEntry PETRIFYING;
    public static RecipeTypeEntry ENDERFYING;

    static {
        WITHERING = registerStandard("withering", params -> new FanRecipe(WITHERING, params));
        SANDING = registerStandard("sanding", params -> new FanRecipe(SANDING, params));
        PURIFYING = registerStandard("purifying", params -> new FanRecipe(PURIFYING, params));
        PETRIFYING = registerStandard("petrifying", params -> new FanRecipe(PETRIFYING, params));
        ENDERFYING = registerStandard("enderfying", params -> new FanRecipe(ENDERFYING, params));
    }

    private CreateProcRecipeTypes() {}

    public static void register(IEventBus modEventBus) {
        ShapedRecipePattern.setCraftingSize(9, 9);
        SERIALIZER_REGISTER.register(modEventBus);
        TYPE_REGISTER.register(modEventBus);
    }

    static RecipeTypeEntry registerStandard(String name, StandardProcessingRecipe.Factory<?> factory) {
        String recipeName = Lang.asId(name);
        ResourceLocation id = CreateProc.asResource(recipeName);
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializer =
            SERIALIZER_REGISTER.register(recipeName, () -> new StandardProcessingRecipe.Serializer<>(factory));
        DeferredHolder<RecipeType<?>, RecipeType<?>> type =
            TYPE_REGISTER.register(recipeName, () -> RecipeType.simple(id));
        return new RecipeTypeEntry(id, serializer, type);
    }

    public record RecipeTypeEntry(
            ResourceLocation id,
            DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> serializer,
            DeferredHolder<RecipeType<?>, RecipeType<?>> type
    ) implements IRecipeTypeInfo {

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) serializer.get();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <I extends RecipeInput, R extends Recipe<I>> RecipeType<R> getType() {
            return (RecipeType<R>) type.get();
        }

        public <I extends RecipeInput, R extends Recipe<I>> Optional<RecipeHolder<R>> find(I input, Level level) {
            return level.getRecipeManager().getRecipeFor(getType(), input, level);
        }
    }
}
