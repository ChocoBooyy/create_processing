package dev.chocoboy.create_processing.content.recipes;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public enum ColdCondition implements StringRepresentable {
    CHILLING("chilling", Blocks.PACKED_ICE),
    FREEZING("freezing", Blocks.BLUE_ICE);

    public static final Codec<ColdCondition> CODEC =
        StringRepresentable.fromEnum(ColdCondition::values);

    private final String name;
    private final Block block;

    ColdCondition(String name, Block block) {
        this.name = name;
        this.block = block;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }
}
