package dev.chocoboy.create_processing.registry;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.fans.processing.EnderfyingType;
import dev.chocoboy.create_processing.content.fans.processing.PetrifyingType;
import dev.chocoboy.create_processing.content.fans.processing.PurifyingType;
import dev.chocoboy.create_processing.content.fans.processing.SandingType;
import dev.chocoboy.create_processing.content.fans.processing.WitheringType;
import net.minecraft.core.Registry;

public final class CreateProcFanProcessingTypes {

    private CreateProcFanProcessingTypes() {}

    public static final WitheringType WITHERING = register("withering", new WitheringType());
    public static final SandingType SANDING = register("sanding", new SandingType());
    public static final PurifyingType PURIFYING = register("purifying", new PurifyingType());
    public static final PetrifyingType PETRIFYING = register("petrifying", new PetrifyingType());
    public static final EnderfyingType ENDERFYING = register("enderfying", new EnderfyingType());

    private static <T extends FanProcessingType> T register(String name, T type) {
        return Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE,
            CreateProc.asResource(name), type);
    }

    public static void init() {
    }
}
