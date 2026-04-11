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
public class ColdPressingRecipe extends StandardProcessingRecipe<SingleRecipeInput> {

    private ColdCondition coldCondition;

    public ColdPressingRecipe(ProcessingRecipeParams params) {
        super(CreateProcRecipeTypes.COLD_PRESSING, params);
        this.coldCondition = ColdCondition.CHILLING;
    }

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

    public static final Serializer SERIALIZER = new Serializer();

    public static final class Serializer extends StandardProcessingRecipe.Serializer<ColdPressingRecipe> {

        private static final MapCodec<ColdCondition> CONDITION_CODEC =
            ColdCondition.CODEC.optionalFieldOf("cold_condition", ColdCondition.CHILLING);

        private final MapCodec<ColdPressingRecipe> coldCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, ColdPressingRecipe> coldStreamCodec;

        private Serializer() {
            super(ColdPressingRecipe::new);
            MapCodec<ColdPressingRecipe> delegate = super.codec();
            StreamCodec<RegistryFriendlyByteBuf, ColdPressingRecipe> delegateStream = super.streamCodec();

            this.coldCodec = new MapCodec<>() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.concat(delegate.keys(ops), CONDITION_CODEC.keys(ops));
                }

                @Override
                public <T> DataResult<ColdPressingRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return delegate.decode(ops, input)
                        .flatMap(recipe -> CONDITION_CODEC.decode(ops, input)
                            .map(recipe::withColdCondition));
                }

                @Override
                public <T> RecordBuilder<T> encode(ColdPressingRecipe recipe, DynamicOps<T> ops,
                        RecordBuilder<T> prefix) {
                    prefix = delegate.encode(recipe, ops, prefix);
                    return CONDITION_CODEC.encode(recipe.getColdCondition(), ops, prefix);
                }
            };

            this.coldStreamCodec = StreamCodec.composite(
                delegateStream,
                r -> r,
                ColdCondition.STREAM_CODEC.cast(),
                ColdPressingRecipe::getColdCondition,
                ColdPressingRecipe::withColdCondition
            );
        }

        @Override
        public MapCodec<ColdPressingRecipe> codec() {
            return coldCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ColdPressingRecipe> streamCodec() {
            return coldStreamCodec;
        }
    }
}
