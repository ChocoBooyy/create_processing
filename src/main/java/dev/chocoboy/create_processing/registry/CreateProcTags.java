package dev.chocoboy.create_processing.registry;

import dev.chocoboy.create_processing.CreateProc;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class CreateProcTags {

    public static final TagKey<Block> WITHERING_CATALYST_BLOCKS =
        BlockTags.create(CreateProc.asResource("fan_processing_catalysts/withering"));
    public static final TagKey<Fluid> WITHERING_CATALYST_FLUIDS =
        FluidTags.create(CreateProc.asResource("fan_processing_catalysts/withering"));
    public static final TagKey<Block> PURIFYING_CATALYST_BLOCKS =
        BlockTags.create(CreateProc.asResource("fan_processing_catalysts/purifying"));
    public static final TagKey<Fluid> PURIFYING_CATALYST_FLUIDS =
        FluidTags.create(CreateProc.asResource("fan_processing_catalysts/purifying"));
    public static final TagKey<Block> PETRIFYING_CATALYST_BLOCKS =
        BlockTags.create(CreateProc.asResource("fan_processing_catalysts/petrifying"));
    public static final TagKey<Block> SANDING_CATALYST_BLOCKS =
        BlockTags.create(CreateProc.asResource("fan_processing_catalysts/sanding"));
    public static final TagKey<Block> ENDERFYING_CATALYST_BLOCKS =
        BlockTags.create(CreateProc.asResource("fan_processing_catalysts/enderfying"));
    public static final TagKey<Item> MEAT =
        ItemTags.create(CreateProc.asResource("meat"));

    private CreateProcTags() {}

    public static TagKey<Item> minecraftItemTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(path));
    }
}
