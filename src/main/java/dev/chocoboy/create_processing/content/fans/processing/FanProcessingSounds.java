package dev.chocoboy.create_processing.content.fans.processing;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

public final class FanProcessingSounds {

    private FanProcessingSounds() {
    }

    public static void playWithering(Level level, BlockPos pos) {
        if (shouldNotPlayOnTick(level, pos, 36, 17)) return; // ~0.56 sounds/sec
        playLayered(level, pos,
            SoundEvents.WITHER_AMBIENT, 0.08f, 0.88f, 1.02f,
            SoundEvents.SOUL_ESCAPE.value(), 0.05f, 0.95f, 1.10f);
    }

    public static void playPurifying(Level level, BlockPos pos) {
        if (shouldNotPlayOnTick(level, pos, 14, 43)) return; // ~1.43 sounds/sec for solid feedback
        playLayered(level, pos,
            SoundEvents.BEACON_POWER_SELECT, 0.22f, 0.95f, 1.15f,
            SoundEvents.AMETHYST_BLOCK_CHIME, 0.16f, 1.00f, 1.20f);
    }

    public static void playPetrifying(Level level, BlockPos pos) {
        if (shouldNotPlayOnTick(level, pos, 18, 71)) return; // ~1.11 sounds/sec
        playLayered(level, pos,
            SoundEvents.CALCITE_HIT, 0.10f, 0.86f, 0.98f,
            SoundEvents.STONE_HIT, 0.07f, 0.94f, 1.06f);
    }

    public static void playEnderfying(Level level, BlockPos pos) {
        if (shouldNotPlayOnTick(level, pos, 32, 97)) return; // ~0.63 sounds/sec - subtle presence
        playLayered(level, pos,
            SoundEvents.ENDERMAN_TELEPORT, 0.08f, 0.90f, 1.10f,
            SoundEvents.PORTAL_TRAVEL, 0.05f, 0.98f, 1.12f);
    }

    public static void playSanding(Level level, BlockPos pos) {
        if (shouldNotPlayOnTick(level, pos, 16, 59)) return; // ~1.25 sounds/sec
        playLayered(level, pos,
            SoundEvents.SAND_BREAK, 0.09f, 0.92f, 1.05f,
            SoundEvents.SAND_HIT, 0.06f, 0.98f, 1.12f);
    }

    private static boolean shouldNotPlayOnTick(Level level, BlockPos pos, int interval, int salt) {
        long phaseSeed = pos.asLong() ^ salt;
        int phase = Math.floorMod(Long.hashCode(phaseSeed), interval);
        return Math.floorMod(level.getGameTime() + phase, interval) != 0;
    }

    private static void playLayered(Level level, BlockPos pos,
                                    SoundEvent primary, float primaryVolume, float primaryMinPitch, float primaryMaxPitch,
                                    SoundEvent secondary, float secondaryVolume, float secondaryMinPitch, float secondaryMaxPitch) {
        if (level.isClientSide) {
            return;
        }

        play(level, pos, primary, primaryVolume, primaryMinPitch, primaryMaxPitch);
        play(level, pos, secondary, secondaryVolume, secondaryMinPitch, secondaryMaxPitch);
    }

    private static void play(Level level, BlockPos pos, SoundEvent event,
                             float volume, float minPitch, float maxPitch) {
        level.playSound(null, pos, event, SoundSource.BLOCKS,
            volume, minPitch + (level.random.nextFloat() * (maxPitch - minPitch)));
    }
}
