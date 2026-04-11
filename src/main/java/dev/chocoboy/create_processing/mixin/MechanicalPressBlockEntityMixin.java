package dev.chocoboy.create_processing.mixin;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.util.ColdSourceHelper;
import dev.chocoboy.create_processing.util.HeatSourceHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(MechanicalPressBlockEntity.class)
public abstract class MechanicalPressBlockEntityMixin {

    @Shadow(remap = false) public PressingBehaviour pressingBehaviour;
    @Shadow(remap = false) public abstract boolean canProcessInBulk();
    @Shadow(remap = false) public abstract void onItemPressed(ItemStack stack);

    // Note: hot and cold pressing both inject at HEAD of tryProcessInWorld/tryProcessOnBelt.
    // Mixin 0.8.5 (in use) does not support @Inject priority — ordering is non-deterministic.
    // In practice this is safe because isHeatSourceAt and isColdSourceAt are mutually exclusive
    // (no block is both a heat source and a cold source). Upgrade to Mixin 0.8.7+ to add explicit ordering.
    @Inject(method = "tryProcessInWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryHotPressingInWorld(ItemEntity itemEntity, boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BlockPos heatPos = itemEntity.getOnPos().below();
        if (!HeatSourceHelper.isHeatSourceAt(level, heatPos)) return;

        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<HotPressingRecipe>> recipe =
            CreateProcRecipeTypes.HOT_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        pressingBehaviour.particleItems.add(item);
        ItemStack itemCreated = ItemStack.EMPTY;
        if (canProcessInBulk() || item.getCount() == 1) {
            RecipeApplier.applyRecipeOn(itemEntity, recipe.get().value(), true);
            itemCreated = itemEntity.getItem().copy();
        } else {
            List<ItemStack> results = RecipeApplier.applyRecipeOn(
                level, item.copyWithCount(1), recipe.get().value(), true);
            for (ItemStack result : results) {
                if (itemCreated.isEmpty()) itemCreated = result.copy();
                ItemEntity created = new ItemEntity(
                    level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                created.setDefaultPickUpDelay();
                created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
                level.addFreshEntity(created);
            }
            item.shrink(1);
        }

        if (!itemCreated.isEmpty()) onItemPressed(itemCreated);
        cir.setReturnValue(true);
    }

    @Inject(method = "tryProcessOnBelt", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryHotPressingOnBelt(TransportedItemStack input, List<ItemStack> outputList,
            boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        // press -> air -> belt/depot -> heat (3 blocks below press)
        BlockPos heatPos = self.getBlockPos().below(3);
        if (!HeatSourceHelper.isHeatSourceAt(level, heatPos)) return;

        ItemStack item = input.stack;
        Optional<RecipeHolder<HotPressingRecipe>> recipe =
            CreateProcRecipeTypes.HOT_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        pressingBehaviour.particleItems.add(item);
        List<ItemStack> results = RecipeApplier.applyRecipeOn(level,
            canProcessInBulk() ? item : item.copyWithCount(1), recipe.get().value(), true);
        for (ItemStack created : results) {
            if (!created.isEmpty()) {
                onItemPressed(created);
                break;
            }
        }
        outputList.addAll(results);
        cir.setReturnValue(true);
    }

    @Inject(method = "tryProcessInWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryColdPressingInWorld(ItemEntity itemEntity, boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BlockPos coldPos = itemEntity.getOnPos().below();
        // One block below the floor block the item rests on — mirrors hot pressing position logic.
        if (!ColdSourceHelper.isColdSourceAt(level, coldPos)) return;

        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<ColdPressingRecipe>> recipe =
            CreateProcRecipeTypes.COLD_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        pressingBehaviour.particleItems.add(item);
        ItemStack itemCreated = ItemStack.EMPTY;
        if (canProcessInBulk() || item.getCount() == 1) {
            RecipeApplier.applyRecipeOn(itemEntity, recipe.get().value(), true);
            itemCreated = itemEntity.getItem().copy();
        } else {
            List<ItemStack> results = RecipeApplier.applyRecipeOn(
                level, item.copyWithCount(1), recipe.get().value(), true);
            for (ItemStack result : results) {
                if (itemCreated.isEmpty()) itemCreated = result.copy();
                ItemEntity created = new ItemEntity(
                    level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                created.setDefaultPickUpDelay();
                created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
                level.addFreshEntity(created);
            }
            item.shrink(1);
        }

        if (!itemCreated.isEmpty()) onItemPressed(itemCreated);
        cir.setReturnValue(true);
    }

    @Inject(method = "tryProcessOnBelt", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryColdPressingOnBelt(TransportedItemStack input, List<ItemStack> outputList,
            boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        // press -> air -> belt/depot -> cold source (3 blocks below press)
        BlockPos coldPos = self.getBlockPos().below(3);
        if (!ColdSourceHelper.isColdSourceAt(level, coldPos)) return;

        ItemStack item = input.stack;
        Optional<RecipeHolder<ColdPressingRecipe>> recipe =
            CreateProcRecipeTypes.COLD_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        pressingBehaviour.particleItems.add(item);
        List<ItemStack> results = RecipeApplier.applyRecipeOn(level,
            canProcessInBulk() ? item : item.copyWithCount(1), recipe.get().value(), true);
        for (ItemStack created : results) {
            if (!created.isEmpty()) {
                onItemPressed(created);
                break;
            }
        }
        outputList.addAll(results);
        cir.setReturnValue(true);
    }

    @Inject(method = "tryProcessInBasin", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryColdPressingInBasin(boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BasinOperatingBlockEntityAccessor accessor = (BasinOperatingBlockEntityAccessor) this;
        Optional<BasinBlockEntity> basinOpt = accessor.create_processing$getBasin();

        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (queued instanceof ColdPressingRecipe) {
            if (basinOpt.isEmpty() || !ColdSourceHelper.isColdSourceAt(
                    level, basinOpt.get().getBlockPos().below())) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (basinOpt.isEmpty() || !ColdSourceHelper.isColdSourceAt(
                level, basinOpt.get().getBlockPos().below())) return;
        BasinBlockEntity basin = basinOpt.get();

        var inv = basin.getInputInventory();
        int matchSlot = -1;
        RecipeHolder<ColdPressingRecipe> recipe = null;
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Optional<RecipeHolder<ColdPressingRecipe>> found =
                CreateProcRecipeTypes.COLD_PRESSING.find(new SingleRecipeInput(stack), level);
            if (found.isPresent()) { matchSlot = slot; recipe = found.get(); break; }
        }
        if (recipe == null) return;

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        ItemStack input = inv.getStackInSlot(matchSlot);
        pressingBehaviour.particleItems.add(input.copyWithCount(1));
        List<ItemStack> results = RecipeApplier.applyRecipeOn(
            level, input.copyWithCount(1), recipe.value(), true);
        input.shrink(1);

        if (basin.acceptOutputs(results, Collections.emptyList(), false)) {
            for (ItemStack created : results) {
                if (!created.isEmpty()) {
                    onItemPressed(created);
                    break;
                }
            }
            cir.setReturnValue(true);
        } else {
            input.grow(1);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tryProcessInBasin", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryHotPressingInBasin(boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BasinOperatingBlockEntityAccessor accessor = (BasinOperatingBlockEntityAccessor) this;
        Optional<BasinBlockEntity> basinOpt = accessor.create_processing$getBasin();

        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (queued instanceof HotPressingRecipe) {
            if (basinOpt.isEmpty() || !HeatSourceHelper.isBasinHeated(basinOpt.get())) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (basinOpt.isEmpty() || !HeatSourceHelper.isBasinHeated(basinOpt.get())) return;
        BasinBlockEntity basin = basinOpt.get();

        var inv = basin.getInputInventory();
        int matchSlot = -1;
        RecipeHolder<HotPressingRecipe> recipe = null;
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            Optional<RecipeHolder<HotPressingRecipe>> found =
                CreateProcRecipeTypes.HOT_PRESSING.find(new SingleRecipeInput(stack), level);
            if (found.isPresent()) { matchSlot = slot; recipe = found.get(); break; }
        }
        if (recipe == null) return;

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        ItemStack input = inv.getStackInSlot(matchSlot);
        pressingBehaviour.particleItems.add(input.copyWithCount(1));
        List<ItemStack> results = RecipeApplier.applyRecipeOn(
            level, input.copyWithCount(1), recipe.value(), true);
        input.shrink(1);

        if (basin.acceptOutputs(results, Collections.emptyList(), false)) {
            for (ItemStack created : results) {
                if (!created.isEmpty()) {
                    onItemPressed(created);
                    break;
                }
            }
            cir.setReturnValue(true);
        } else {
            input.grow(1);
            cir.setReturnValue(false);
        }
    }

}
