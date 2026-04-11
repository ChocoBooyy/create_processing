package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class ColdPressingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

    private ColdCondition coldCondition;

    /** Used by the base serializer delegate — defaults to CHILLING. */
    public ColdPressingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.COLD_PRESSING, params);
        this.coldCondition = ColdCondition.CHILLING;
    }

    /** Sets coldCondition after base decode. Package-private; only for use by SERIALIZER. */
    ColdPressingRecipe withColdCondition(ColdCondition condition) {
        this.coldCondition = condition;
        return this;
    }

    public ColdCondition getColdCondition() {
        return coldCondition;
    }

    @Override
    protected boolean canRequireHeat() {
        return false;
    }

    @Override
    public boolean matches(SingleRecipeInput inv, Level level) {
        if (inv.isEmpty()) return false;
        return ingredients.getFirst().test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    // -------------------------------------------------------------------------
    // Serializer
    // -------------------------------------------------------------------------

    public static final RecipeSerializer<ColdPressingRecipe> SERIALIZER = new RecipeSerializer<>() {

        private static final StandardProcessingRecipe.Serializer<ColdPressingRecipe> DELEGATE =
            new StandardProcessingRecipe.Serializer<>(ColdPressingRecipe::new);

        private static final MapCodec<ColdCondition> CONDITION_CODEC =
            ColdCondition.CODEC.optionalFieldOf("cold_condition", ColdCondition.CHILLING);

        private static final MapCodec<ColdPressingRecipe> CODEC = new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(com.mojang.serialization.DynamicOps<T> ops) {
                return Stream.concat(DELEGATE.codec().keys(ops), CONDITION_CODEC.keys(ops));
            }

            @Override
            public <T> com.mojang.serialization.DataResult<ColdPressingRecipe> decode(
                    com.mojang.serialization.DynamicOps<T> ops,
                    com.mojang.serialization.MapLike<T> input) {
                return DELEGATE.codec().decode(ops, input)
                    .flatMap(recipe -> CONDITION_CODEC.decode(ops, input)
                        .map(recipe::withColdCondition));
            }

            @Override
            public <T> com.mojang.serialization.RecordBuilder<T> encode(
                    ColdPressingRecipe recipe,
                    com.mojang.serialization.DynamicOps<T> ops,
                    com.mojang.serialization.RecordBuilder<T> prefix) {
                prefix = DELEGATE.codec().encode(recipe, ops, prefix);
                return CONDITION_CODEC.encode(recipe.getColdCondition(), ops, prefix);
            }
        };

        private static final StreamCodec<RegistryFriendlyByteBuf, ColdPressingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                DELEGATE.streamCodec(),
                r -> r,
                ColdCondition.STREAM_CODEC.cast(),
                ColdPressingRecipe::getColdCondition,
                (recipe, condition) -> recipe.withColdCondition(condition)
            );

        @Override
        public MapCodec<ColdPressingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ColdPressingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    };
}
