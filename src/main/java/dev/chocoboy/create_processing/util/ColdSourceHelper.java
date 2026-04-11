package dev.chocoboy.create_processing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class ColdSourceHelper {

    private ColdSourceHelper() {}

    public static boolean isColdSourceAt(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        return state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE);
    }
}
