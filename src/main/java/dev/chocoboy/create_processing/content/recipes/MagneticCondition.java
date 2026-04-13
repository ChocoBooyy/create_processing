package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum MagneticCondition implements StringRepresentable {
    EXPOSED(Blocks.EXPOSED_COPPER),
    WEATHERED(Blocks.WEATHERED_COPPER),
    OXIDIZED(Blocks.OXIDIZED_COPPER);

    public static final Codec<MagneticCondition> CODEC =
        StringRepresentable.fromEnum(MagneticCondition::values);
    public static final StreamCodec<ByteBuf, MagneticCondition> STREAM_CODEC =
        CatnipStreamCodecBuilders.ofEnum(MagneticCondition.class);

    private final Block block;

    MagneticCondition(Block block) {
        this.block = block;
    }

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }

    public Block getBlock() {
        return block;
    }

    public boolean satisfies(MagneticCondition required) {
        return this.ordinal() >= required.ordinal();
    }
}
