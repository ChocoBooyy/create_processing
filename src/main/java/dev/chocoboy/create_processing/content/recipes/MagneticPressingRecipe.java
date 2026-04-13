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
public class MagneticPressingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

    private MagneticCondition magneticCondition;

    public MagneticPressingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.MAGNETIC_PRESSING, params);
        this.magneticCondition = MagneticCondition.EXPOSED;
    }

    public MagneticPressingRecipe withMagneticCondition(MagneticCondition condition) {
        this.magneticCondition = condition;
        return this;
    }

    public MagneticCondition getMagneticCondition() {
        return magneticCondition;
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

    public static final class Serializer extends StandardProcessingRecipe.Serializer<MagneticPressingRecipe> {

        private static final MapCodec<MagneticCondition> CONDITION_CODEC =
            MagneticCondition.CODEC.optionalFieldOf("magnetic_condition", MagneticCondition.EXPOSED);

        private final MapCodec<MagneticPressingRecipe> magneticCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, MagneticPressingRecipe> magneticStreamCodec;

        private Serializer() {
            super(MagneticPressingRecipe::new);
            MapCodec<MagneticPressingRecipe> delegate = super.codec();
            StreamCodec<RegistryFriendlyByteBuf, MagneticPressingRecipe> delegateStream = super.streamCodec();

            this.magneticCodec = new MapCodec<>() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.concat(delegate.keys(ops), CONDITION_CODEC.keys(ops));
                }

                @Override
                public <T> DataResult<MagneticPressingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return delegate.decode(ops, input)
                        .flatMap(recipe -> CONDITION_CODEC.decode(ops, input)
                            .map(recipe::withMagneticCondition));
                }

                @Override
                public <T> RecordBuilder<T> encode(MagneticPressingRecipe recipe, DynamicOps<T> ops,
                        RecordBuilder<T> prefix) {
                    prefix = delegate.encode(recipe, ops, prefix);
                    return CONDITION_CODEC.encode(recipe.getMagneticCondition(), ops, prefix);
                }
            };

            this.magneticStreamCodec = StreamCodec.composite(
                delegateStream,
                r -> r,
                MagneticCondition.STREAM_CODEC.cast(),
                MagneticPressingRecipe::getMagneticCondition,
                MagneticPressingRecipe::withMagneticCondition
            );
        }

        @Override
        public MapCodec<MagneticPressingRecipe> codec() {
            return magneticCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MagneticPressingRecipe> streamCodec() {
            return magneticStreamCodec;
        }
    }
}
