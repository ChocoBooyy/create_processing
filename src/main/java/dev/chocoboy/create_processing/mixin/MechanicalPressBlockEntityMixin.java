package dev.chocoboy.create_processing.mixin;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.content.recipes.ColdPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.HotPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.MagneticCondition;
import dev.chocoboy.create_processing.content.recipes.MagneticPressingRecipe;
import dev.chocoboy.create_processing.content.recipes.SpeedCondition;
import dev.chocoboy.create_processing.content.recipes.SpeedPressingRecipe;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.util.ColdPressingHelper;
import dev.chocoboy.create_processing.util.ColdSourceHelper;
import dev.chocoboy.create_processing.util.HeatSourceHelper;
import dev.chocoboy.create_processing.util.HotPressingHelper;
import dev.chocoboy.create_processing.util.MagneticPressingHelper;
import dev.chocoboy.create_processing.util.MagneticSourceHelper;
import dev.chocoboy.create_processing.util.SpeedProcessingHelper;
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

    @Inject(method = "tryProcessInWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryHotPressingInWorld(ItemEntity itemEntity, boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BlockPos heatPos = itemEntity.getOnPos().below();
        HeatLevel heatLevel = HeatSourceHelper.getHeatLevelAt(level, heatPos);
        if (!HeatSourceHelper.isActiveHeatLevel(heatLevel)) return;

        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<HotPressingRecipe>> recipe =
            CreateProcRecipeTypes.HOT_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;
        if (!recipe.get().value().getRequiredHeat().testBlazeBurner(heatLevel)) return;

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
        HeatLevel heatLevel = HeatSourceHelper.getHeatLevelAt(level, heatPos);
        if (!HeatSourceHelper.isActiveHeatLevel(heatLevel)) return;

        ItemStack item = input.stack;
        Optional<RecipeHolder<HotPressingRecipe>> recipe =
            CreateProcRecipeTypes.HOT_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;
        if (!recipe.get().value().getRequiredHeat().testBlazeBurner(heatLevel)) return;

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
        ColdCondition sourceLevel = ColdSourceHelper.getColdConditionAt(level, coldPos);
        if (sourceLevel == null) return;

        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<ColdPressingRecipe>> recipe =
            CreateProcRecipeTypes.COLD_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;
        if (!sourceLevel.satisfies(recipe.get().value().getColdCondition())) return;

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
        ColdCondition sourceLevel = ColdSourceHelper.getColdConditionAt(level, coldPos);
        if (sourceLevel == null) return;

        ItemStack item = input.stack;
        Optional<RecipeHolder<ColdPressingRecipe>> recipe =
            CreateProcRecipeTypes.COLD_PRESSING.find(new SingleRecipeInput(item), level);
        if (recipe.isEmpty()) return;
        if (!sourceLevel.satisfies(recipe.get().value().getColdCondition())) return;

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
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        ColdCondition coldSourceLevel = ColdSourceHelper.getColdConditionAt(level, basin.getBlockPos().below());

        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (queued instanceof ColdPressingRecipe coldRecipe) {
            if (coldSourceLevel == null || !coldSourceLevel.satisfies(coldRecipe.getColdCondition())) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (coldSourceLevel == null) return;

        var match = ColdPressingHelper.findInBasin(basin, level, coldSourceLevel);
        if (match.isEmpty()) return;
        int matchSlot = match.get().getKey();
        RecipeHolder<ColdPressingRecipe> recipe = match.get().getValue();

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        var inv = basin.getInputInventory();
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

    @Inject(method = "tryProcessInWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$tryMagneticPressingInWorld(ItemEntity itemEntity, boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BlockPos magnetPos = itemEntity.getOnPos().below();
        MagneticCondition sourceLevel = MagneticSourceHelper.getMagneticConditionAt(level, magnetPos);
        if (sourceLevel == null) return;

        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<MagneticPressingRecipe>> recipe =
            CreateProcRecipeTypes.MAGNETIC_PRESSING.find(new SingleRecipeInput(item), level);
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
    private void create_processing$tryMagneticPressingOnBelt(TransportedItemStack input, List<ItemStack> outputList,
            boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BlockPos magnetPos = self.getBlockPos().below(3);
        MagneticCondition sourceLevel = MagneticSourceHelper.getMagneticConditionAt(level, magnetPos);
        if (sourceLevel == null) return;

        ItemStack item = input.stack;
        Optional<RecipeHolder<MagneticPressingRecipe>> recipe =
            CreateProcRecipeTypes.MAGNETIC_PRESSING.find(new SingleRecipeInput(item), level);
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
    private void create_processing$tryMagneticPressingInBasin(boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        BasinOperatingBlockEntityAccessor accessor = (BasinOperatingBlockEntityAccessor) this;
        Optional<BasinBlockEntity> basinOpt = accessor.create_processing$getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        MagneticCondition magnetSourceLevel = MagneticSourceHelper.getMagneticConditionAt(level, basin.getBlockPos().below());

        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (queued instanceof MagneticPressingRecipe magnetRecipe) {
            if (magnetSourceLevel == null) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (magnetSourceLevel == null) return;

        var match = MagneticPressingHelper.findInBasin(basin, level, magnetSourceLevel);
        if (match.isEmpty()) return;
        int matchSlot = match.get().getKey();
        RecipeHolder<MagneticPressingRecipe> recipe = match.get().getValue();

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        var inv = basin.getInputInventory();
        ItemStack inputStack = inv.getStackInSlot(matchSlot);
        pressingBehaviour.particleItems.add(inputStack.copyWithCount(1));
        List<ItemStack> results = RecipeApplier.applyRecipeOn(
            level, inputStack.copyWithCount(1), recipe.value(), true);
        inputStack.shrink(1);

        if (basin.acceptOutputs(results, Collections.emptyList(), false)) {
            for (ItemStack created : results) {
                if (!created.isEmpty()) {
                    onItemPressed(created);
                    break;
                }
            }
            cir.setReturnValue(true);
        } else {
            inputStack.grow(1);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tryProcessInWorld", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$trySpeedPressingInWorld(ItemEntity itemEntity, boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        if (SpeedCondition.fromMachineSpeed(self.getSpeed()) == null) return;

        ItemStack item = itemEntity.getItem();
        Optional<RecipeHolder<SpeedPressingRecipe>> recipe =
            SpeedProcessingHelper.findPressing(item, level, self.getSpeed());
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
    private void create_processing$trySpeedPressingOnBelt(TransportedItemStack input, List<ItemStack> outputList,
            boolean simulate, CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        if (SpeedCondition.fromMachineSpeed(self.getSpeed()) == null) return;

        ItemStack item = input.stack;
        Optional<RecipeHolder<SpeedPressingRecipe>> recipe =
            SpeedProcessingHelper.findPressing(item, level, self.getSpeed());
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
    private void create_processing$trySpeedPressingInBasin(boolean simulate,
            CallbackInfoReturnable<Boolean> cir) {
        MechanicalPressBlockEntity self = (MechanicalPressBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null) return;

        if (SpeedCondition.fromMachineSpeed(self.getSpeed()) == null) return;

        BasinOperatingBlockEntityAccessor accessor = (BasinOperatingBlockEntityAccessor) this;
        Optional<BasinBlockEntity> basinOpt = accessor.create_processing$getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (queued instanceof SpeedPressingRecipe speedRecipe) {
            if (!speedRecipe.getSpeedCondition().isMetByMachineSpeed(self.getSpeed())) {
                cir.setReturnValue(false);
                return;
            }
        }

        var match = SpeedProcessingHelper.findPressingInBasin(basin, level, self.getSpeed());
        if (match.isEmpty()) return;
        int matchSlot = match.get().getKey();
        RecipeHolder<SpeedPressingRecipe> recipe = match.get().getValue();

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        var inv = basin.getInputInventory();
        ItemStack inputStack = inv.getStackInSlot(matchSlot);
        pressingBehaviour.particleItems.add(inputStack.copyWithCount(1));
        List<ItemStack> results = RecipeApplier.applyRecipeOn(
            level, inputStack.copyWithCount(1), recipe.value(), true);
        inputStack.shrink(1);

        if (basin.acceptOutputs(results, Collections.emptyList(), false)) {
            for (ItemStack created : results) {
                if (!created.isEmpty()) {
                    onItemPressed(created);
                    break;
                }
            }
            cir.setReturnValue(true);
        } else {
            inputStack.grow(1);
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

        HeatLevel basinHeatLevel = basinOpt.map(HeatSourceHelper::getBasinHeatLevel).orElse(HeatLevel.NONE);

        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (queued instanceof HotPressingRecipe hotRecipe) {
            if (basinOpt.isEmpty() || !HeatSourceHelper.isActiveHeatLevel(basinHeatLevel) || !hotRecipe.getRequiredHeat().testBlazeBurner(basinHeatLevel)) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (basinOpt.isEmpty() || !HeatSourceHelper.isActiveHeatLevel(basinHeatLevel)) return;
        BasinBlockEntity basin = basinOpt.get();

        var match = HotPressingHelper.findInBasin(basin, level, basinHeatLevel);
        if (match.isEmpty()) return;
        int matchSlot = match.get().getKey();
        RecipeHolder<HotPressingRecipe> recipe = match.get().getValue();

        if (simulate) {
            cir.setReturnValue(true);
            return;
        }

        var inv = basin.getInputInventory();
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
