package dev.chocoboy.create_processing;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = CreateProcessing.MOD_ID, dist = Dist.CLIENT)
public class CreateProcessingClient {

    public CreateProcessingClient(IEventBus modEventBus) {
        modEventBus.addListener(CreateProcessingClient::onClientSetup);
    }

    private static void onClientSetup(final FMLClientSetupEvent event) {
        CreateProcessing.LOGGER.info("Client setup for {}", CreateProcessing.NAME);
    }
}

