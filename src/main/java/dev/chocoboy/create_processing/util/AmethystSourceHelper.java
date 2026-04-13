package dev.chocoboy.create_processing.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public final class AmethystSourceHelper {

    private AmethystSourceHelper() {}

    public static boolean isResonantAt(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.AMETHYST_BLOCK);
    }
}
