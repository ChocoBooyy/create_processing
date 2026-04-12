package dev.chocoboy.create_processing.util;

import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public final class ColdSourceHelper {

    private ColdSourceHelper() {}

    @Nullable
    public static ColdCondition getColdConditionAt(Level level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (state.is(ColdCondition.FREEZING.getBlock())) return ColdCondition.FREEZING;
        if (state.is(ColdCondition.CHILLING.getBlock())) return ColdCondition.CHILLING;
        return null;
    }

}
