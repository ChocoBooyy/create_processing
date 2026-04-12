package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum ColdCondition implements StringRepresentable {
    CHILLING(Blocks.PACKED_ICE),
    FREEZING(Blocks.BLUE_ICE);

    public static final Codec<ColdCondition> CODEC =
        StringRepresentable.fromEnum(ColdCondition::values);
    public static final StreamCodec<ByteBuf, ColdCondition> STREAM_CODEC =
        CatnipStreamCodecBuilders.ofEnum(ColdCondition.class);

    private final Block block;

    ColdCondition(Block block) {
        this.block = block;
    }

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }

    public Block getBlock() {
        return block;
    }

    public boolean satisfies(ColdCondition required) {
        return this.ordinal() >= required.ordinal();
    }
}
