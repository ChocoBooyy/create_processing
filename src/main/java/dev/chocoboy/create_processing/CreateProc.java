package dev.chocoboy.create_processing;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import dev.chocoboy.create_processing.infrastructure.datagen.CreateProcDatagen;
import dev.chocoboy.create_processing.registry.CreateProcFanProcessingTypes;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(CreateProc.MOD_ID)
public class CreateProc {

    public static final String MOD_ID = "create_processing";
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item ->
            new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE));
    }

    public CreateProc(IEventBus modEventBus, ModContainer modContainer) {
        REGISTRATE.registerEventListeners(modEventBus);
        CreateProcRecipeTypes.register(modEventBus);
        modEventBus.addListener(CreateProcDatagen::gatherData);
        modEventBus.addListener((RegisterEvent event) -> CreateProcFanProcessingTypes.init());
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
