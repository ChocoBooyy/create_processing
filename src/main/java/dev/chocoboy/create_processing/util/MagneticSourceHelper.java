package dev.chocoboy.create_processing.util;

import dev.chocoboy.create_processing.content.recipes.MagneticCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;

public final class MagneticSourceHelper {

    private MagneticSourceHelper() {}

    @Nullable
    public static MagneticCondition getMagneticConditionAt(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(Blocks.LODESTONE)) return MagneticCondition.MAGNETIC;
        return null;
    }
}
