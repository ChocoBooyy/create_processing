package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

@ParametersAreNonnullByDefault
public class SpeedPressingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

    private SpeedCondition speedCondition;

    public SpeedPressingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.SPEED_PRESSING, params);
        this.speedCondition = SpeedCondition.FAST;
    }

    public SpeedPressingRecipe withSpeedCondition(SpeedCondition condition) {
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

    public static final Serializer SERIALIZER = new Serializer();

    public static final class Serializer extends StandardProcessingRecipe.Serializer<SpeedPressingRecipe> {

        private static final MapCodec<SpeedCondition> CONDITION_CODEC =
            SpeedCondition.CODEC.optionalFieldOf("speed_condition", SpeedCondition.FAST);

        private final MapCodec<SpeedPressingRecipe> speedCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, SpeedPressingRecipe> speedStreamCodec;

        private Serializer() {
            super(SpeedPressingRecipe::new);
            MapCodec<SpeedPressingRecipe> delegate = super.codec();
            StreamCodec<RegistryFriendlyByteBuf, SpeedPressingRecipe> delegateStream = super.streamCodec();

            this.speedCodec = new MapCodec<>() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.concat(delegate.keys(ops), CONDITION_CODEC.keys(ops));
                }

                @Override
                public <T> DataResult<SpeedPressingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return delegate.decode(ops, input)
                        .flatMap(recipe -> CONDITION_CODEC.decode(ops, input)
                            .map(recipe::withSpeedCondition));
                }

                @Override
                public <T> RecordBuilder<T> encode(SpeedPressingRecipe recipe, DynamicOps<T> ops,
                        RecordBuilder<T> prefix) {
                    prefix = delegate.encode(recipe, ops, prefix);
                    return CONDITION_CODEC.encode(recipe.getSpeedCondition(), ops, prefix);
                }
            };

            this.speedStreamCodec = StreamCodec.composite(
                delegateStream,
                r -> r,
                SpeedCondition.STREAM_CODEC.cast(),
                SpeedPressingRecipe::getSpeedCondition,
                SpeedPressingRecipe::withSpeedCondition
            );
        }

        @Override
        public MapCodec<SpeedPressingRecipe> codec() {
            return speedCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SpeedPressingRecipe> streamCodec() {
            return speedStreamCodec;
        }
    }
}

