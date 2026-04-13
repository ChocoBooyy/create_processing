package dev.chocoboy.create_processing.infrastructure.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {

    public static final CommonConfig COMMON;
    public static final ModConfigSpec COMMON_SPEC;

    static {
        Pair<CommonConfig, ModConfigSpec> pair = new ModConfigSpec.Builder()
            .configure(CommonConfig::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    public static class CommonConfig {
        public final ModConfigSpec.BooleanValue enableWitheringFan;
        public final ModConfigSpec.BooleanValue enablePurifyingFan;
        public final ModConfigSpec.BooleanValue enablePetrifyingFan;
        public final ModConfigSpec.BooleanValue enableSandingFan;
        public final ModConfigSpec.BooleanValue enableEnderifyingFan;
        public final ModConfigSpec.BooleanValue enableHotPressing;
        public final ModConfigSpec.BooleanValue enableColdPressing;
        public final ModConfigSpec.BooleanValue enableMagneticPressing;
        public final ModConfigSpec.BooleanValue enableSpeedPressing;
        public final ModConfigSpec.BooleanValue enableColdMixing;
        public final ModConfigSpec.BooleanValue enableResonanceMixing;
        public final ModConfigSpec.BooleanValue enableSpeedMixing;

        CommonConfig(ModConfigSpec.Builder builder) {
            builder.comment("Processing Type Enablement").push("processing");

            builder.comment("Fan Processing Types").push("fanProcessing");

            enableWitheringFan = builder
                .comment("Enable withering fan processing")
                .define("enableWitheringFan", true);

            enablePurifyingFan = builder
                .comment("Enable purifying fan processing")
                .define("enablePurifyingFan", true);

            enablePetrifyingFan = builder
                .comment("Enable petrifying fan processing")
                .define("enablePetrifyingFan", true);

            enableSandingFan = builder
                .comment("Enable sanding fan processing")
                .define("enableSandingFan", true);

            enableEnderifyingFan = builder
                .comment("Enable enderfying fan processing")
                .define("enableEnderifyingFan", true);

            builder.pop();
            builder.comment("Pressing Types").push("pressing");

            enableHotPressing = builder
                .comment("Enable hot pressing with blaze burner catalyst")
                .define("enableHotPressing", true);

            enableColdPressing = builder
                .comment("Enable cold pressing with packed ice catalyst")
                .define("enableColdPressing", true);

            enableMagneticPressing = builder
                .comment("Enable magnetic pressing with lodestone catalyst")
                .define("enableMagneticPressing", true);

            enableSpeedPressing = builder
                .comment("Enable speed-tiered pressing (FAST @ 64 RPM, OVERDRIVE @ 256 RPM)")
                .define("enableSpeedPressing", true);

            builder.pop();
            builder.comment("Mixing Types").push("mixing");

            enableColdMixing = builder
                .comment("Enable cold mixing with packed ice catalyst")
                .define("enableColdMixing", true);

            enableResonanceMixing = builder
                .comment("Enable resonance mixing with amethyst catalyst")
                .define("enableResonanceMixing", true);

            enableSpeedMixing = builder
                .comment("Enable speed-tiered mixing (FAST @ 64 RPM, OVERDRIVE @ 256 RPM)")
                .define("enableSpeedMixing", true);

            builder.pop();
        }
    }
}

