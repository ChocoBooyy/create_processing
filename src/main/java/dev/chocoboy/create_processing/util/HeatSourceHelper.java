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

    public static HeatLevel getHeatLevelAt(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.FIRE) || state.is(Blocks.SOUL_FIRE)) return HeatLevel.KINDLED;
        if (state.getFluidState().is(FluidTags.LAVA)) return HeatLevel.SEETHING;
        if (state.hasProperty(BlazeBurnerBlock.HEAT_LEVEL)) {
            return state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
        }
        return HeatLevel.NONE;
    }

    public static boolean isActiveHeatLevel(HeatLevel level) {
        return level != HeatLevel.NONE && level != HeatLevel.SMOULDERING;
    }

    public static HeatLevel getBasinHeatLevel(BasinBlockEntity basin) {
        Level level = basin.getLevel();
        if (level == null) return HeatLevel.NONE;
        return BasinBlockEntity.getHeatLevelOf(level.getBlockState(basin.getBlockPos().below()));
    }

}
