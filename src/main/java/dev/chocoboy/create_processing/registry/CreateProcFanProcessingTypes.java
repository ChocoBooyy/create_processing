package dev.chocoboy.create_processing.registry;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import dev.chocoboy.create_processing.CreateProc;
import net.minecraft.core.Registry;

public final class CreateProcFanProcessingTypes {

    private CreateProcFanProcessingTypes() {}

    private static <T extends FanProcessingType> T register(String name, T type) {
        return Registry.register(CreateBuiltInRegistries.FAN_PROCESSING_TYPE,
            CreateProc.asResource(name), type);
    }

    public static void init() {}
}
