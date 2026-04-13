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
public class SpeedMixingRecipe extends BasinRecipe {

    private SpeedCondition speedCondition;

    public SpeedMixingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.SPEED_MIXING, params);
        this.speedCondition = SpeedCondition.FAST;
    }

    public SpeedMixingRecipe withSpeedCondition(SpeedCondition condition) {
        this.speedCondition = condition;
        return this;
    }

    public SpeedCondition getSpeedCondition() {
        return speedCondition;
    }

    @Override
    protected boolean canRequireHeat() {
        return false;
    }

    public static final Serializer SERIALIZER = new Serializer();

    public static final class Serializer extends StandardProcessingRecipe.Serializer<SpeedMixingRecipe> {

        private static final MapCodec<SpeedCondition> CONDITION_CODEC =
            SpeedCondition.CODEC.optionalFieldOf("speed_condition", SpeedCondition.FAST);

        private final MapCodec<SpeedMixingRecipe> speedCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, SpeedMixingRecipe> speedStreamCodec;

        private Serializer() {
            super(SpeedMixingRecipe::new);
            MapCodec<SpeedMixingRecipe> delegate = super.codec();
            StreamCodec<RegistryFriendlyByteBuf, SpeedMixingRecipe> delegateStream = super.streamCodec();

            this.speedCodec = new MapCodec<>() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.concat(delegate.keys(ops), CONDITION_CODEC.keys(ops));
                }

                @Override
                public <T> DataResult<SpeedMixingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return delegate.decode(ops, input)
                        .flatMap(recipe -> CONDITION_CODEC.decode(ops, input)
                            .map(recipe::withSpeedCondition));
                }

                @Override
                public <T> RecordBuilder<T> encode(SpeedMixingRecipe recipe, DynamicOps<T> ops,
                        RecordBuilder<T> prefix) {
                    prefix = delegate.encode(recipe, ops, prefix);
                    return CONDITION_CODEC.encode(recipe.getSpeedCondition(), ops, prefix);
                }
            };

            this.speedStreamCodec = StreamCodec.composite(
                delegateStream,
                r -> r,
                SpeedCondition.STREAM_CODEC.cast(),
                SpeedMixingRecipe::getSpeedCondition,
                SpeedMixingRecipe::withSpeedCondition
            );
        }

        @Override
        public MapCodec<SpeedMixingRecipe> codec() {
            return speedCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SpeedMixingRecipe> streamCodec() {
            return speedStreamCodec;
        }
    }
}

