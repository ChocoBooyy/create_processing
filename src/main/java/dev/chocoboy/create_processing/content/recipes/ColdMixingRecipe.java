package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class ColdMixingRecipe extends BasinRecipe {

    private ColdCondition coldCondition;

    public ColdMixingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.COLD_MIXING, params);
        this.coldCondition = ColdCondition.CHILLING;
    }

    public ColdMixingRecipe withColdCondition(ColdCondition condition) {
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

    public static final Serializer SERIALIZER = new Serializer();

    public static final class Serializer extends StandardProcessingRecipe.Serializer<ColdMixingRecipe> {

        private static final MapCodec<ColdCondition> CONDITION_CODEC =
            ColdCondition.CODEC.optionalFieldOf("cold_condition", ColdCondition.CHILLING);

        private final MapCodec<ColdMixingRecipe> coldCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, ColdMixingRecipe> coldStreamCodec;

        private Serializer() {
            super(ColdMixingRecipe::new);
            MapCodec<ColdMixingRecipe> delegate = super.codec();
            StreamCodec<RegistryFriendlyByteBuf, ColdMixingRecipe> delegateStream = super.streamCodec();

            this.coldCodec = new MapCodec<>() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.concat(delegate.keys(ops), CONDITION_CODEC.keys(ops));
                }

                @Override
                public <T> DataResult<ColdMixingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return delegate.decode(ops, input)
                        .flatMap(recipe -> CONDITION_CODEC.decode(ops, input)
                            .map(recipe::withColdCondition));
                }

                @Override
                public <T> RecordBuilder<T> encode(ColdMixingRecipe recipe, DynamicOps<T> ops,
                        RecordBuilder<T> prefix) {
                    prefix = delegate.encode(recipe, ops, prefix);
                    return CONDITION_CODEC.encode(recipe.getColdCondition(), ops, prefix);
                }
            };

            this.coldStreamCodec = StreamCodec.composite(
                delegateStream,
                r -> r,
                ColdCondition.STREAM_CODEC.cast(),
                ColdMixingRecipe::getColdCondition,
                ColdMixingRecipe::withColdCondition
            );
        }

        @Override
        public MapCodec<ColdMixingRecipe> codec() {
            return coldCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ColdMixingRecipe> streamCodec() {
            return coldStreamCodec;
        }
    }
}
