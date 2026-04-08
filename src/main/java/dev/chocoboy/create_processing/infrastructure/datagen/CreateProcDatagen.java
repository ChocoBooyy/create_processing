package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public final class CreateProcDatagen {
    private CreateProcDatagen() {
    }

    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(CreateProc.MOD_ID)) {
            return;
        }

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        if (event.includeServer()) {
            generator.addProvider(true, new CreateProcBlockTagsProvider(output, lookupProvider, existingFileHelper));
            generator.addProvider(true, new CreateProcItemTagsProvider(output, lookupProvider, existingFileHelper));
            generator.addProvider(true, new WitheringRecipeGen(output, lookupProvider));
            generator.addProvider(true, new PurifyingRecipeGen(output, lookupProvider));
        }
    }
}
