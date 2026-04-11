package dev.chocoboy.create_processing.util;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class HeatSourceHelper {

    private HeatSourceHelper() {}

    public static boolean isHeatSourceAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) return true;
        if (state.getFluidState().is(FluidTags.LAVA)) return true;
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            HeatLevel heat = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            return heat != HeatLevel.NONE && heat != HeatLevel.SMOULDERING;
        }
        return false;
    }

    public static boolean isBasinHeated(BasinBlockEntity basin) {
        Level level = basin.getLevel();
        if (level == null) return false;
        HeatLevel heat = BasinBlockEntity.getHeatLevelOf(level.getBlockState(basin.getBlockPos().below()));
        return heat != HeatLevel.NONE && heat != HeatLevel.SMOULDERING;
    }
}
