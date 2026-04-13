package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;

public enum SpeedCondition implements StringRepresentable {
    FAST(64, 0x5BC0EB),
    OVERDRIVE(256, 0xF7931E);

    public static final Codec<SpeedCondition> CODEC =
        StringRepresentable.fromEnum(SpeedCondition::values);
    public static final StreamCodec<ByteBuf, SpeedCondition> STREAM_CODEC =
        CatnipStreamCodecBuilders.ofEnum(SpeedCondition.class);

    private final int minimumSpeed;
    private final int color;

    SpeedCondition(int minimumSpeed, int color) {
        this.minimumSpeed = minimumSpeed;
        this.color = color;
    }

    @Override
    public String getSerializedName() {
        return Lang.asId(name());
    }

    public int getMinimumSpeed() {
        return minimumSpeed;
    }

    public int getColor() {
        return color;
    }

    public boolean satisfies(SpeedCondition required) {
        return minimumSpeed >= required.minimumSpeed;
    }

    public boolean isMetByMachineSpeed(float machineSpeed) {
        return Math.abs(machineSpeed) >= minimumSpeed;
    }

    @Nullable
    public static SpeedCondition fromMachineSpeed(float machineSpeed) {
        float absSpeed = Math.abs(machineSpeed);
        if (absSpeed >= OVERDRIVE.minimumSpeed) return OVERDRIVE;
        if (absSpeed >= FAST.minimumSpeed) return FAST;
        return null;
    }
}
