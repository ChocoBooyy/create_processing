package dev.chocoboy.create_processing.infrastructure.datagen;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.palettes.AllPaletteStoneTypes;
import dev.chocoboy.create_processing.CreateProc;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class CreateProcBlockTagsProvider extends BlockTagsProvider {

    public CreateProcBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CreateProc.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CreateProcTags.WITHERING_CATALYST_BLOCKS)
            .add(Blocks.WITHER_ROSE);

        tag(CreateProcTags.PURIFYING_CATALYST_BLOCKS)
            .add(Blocks.BEACON);

        tag(CreateProcTags.PETRIFYING_CATALYST_BLOCKS)
            .add(AllPaletteStoneTypes.LIMESTONE.baseBlock.get());

        tag(AllTags.AllBlockTags.FAN_TRANSPARENT.tag)
            .addTag(CreateProcTags.WITHERING_CATALYST_BLOCKS)
            .addTag(CreateProcTags.PURIFYING_CATALYST_BLOCKS)
            .addTag(CreateProcTags.PETRIFYING_CATALYST_BLOCKS);
    }
}
