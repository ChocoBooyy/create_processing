package dev.chocoboy.create_processing.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public interface BasinOperatingBlockEntityAccessor {

    @Invoker("getBasin")
    Optional<BasinBlockEntity> create_processing$getBasin();

    @Accessor("currentRecipe")
    Recipe<?> create_processing$getCurrentRecipe();

    @Accessor("currentRecipe")
    void create_processing$setCurrentRecipe(Recipe<?> recipe);
}
