package dev.chocoboy.create_processing.infrastructure.datagen;

import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class CreateProcItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

    public CreateProcItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, Registries.ITEM, lookupProvider,
            item -> BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow(),
            CreateProc.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CreateProcTags.MEAT)
            .add(Items.BEEF, Items.PORKCHOP, Items.CHICKEN, Items.MUTTON,
                Items.COD, Items.SALMON, Items.RABBIT);
    }
}
