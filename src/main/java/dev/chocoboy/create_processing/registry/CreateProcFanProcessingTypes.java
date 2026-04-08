package dev.chocoboy.create_processing.registry;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.content.fans.processing.WitheringType;
import net.minecraft.core.Registry;

public final class CreateProcFanProcessingTypes {

    public static final WitheringType WITHERING = register("withering", new WitheringType());

    private CreateProcFanProcessingTypes() {}

    private static <T extends FanProcessingType> T register(String name, T type) {
        return Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE,
            CreateProc.asResource(name), type);
    }

    public static void init() {}
}
