package dev.chocoboy.create_processing.mixin;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.content.recipes.ColdMixingRecipe;
import dev.chocoboy.create_processing.content.recipes.ResonanceMixingRecipe;
import dev.chocoboy.create_processing.util.AmethystSourceHelper;
import dev.chocoboy.create_processing.util.ColdMixingHelper;
import dev.chocoboy.create_processing.util.ColdPressingHelper;
import dev.chocoboy.create_processing.util.ColdSourceHelper;
import dev.chocoboy.create_processing.util.HeatSourceHelper;
import dev.chocoboy.create_processing.util.HotPressingHelper;
import dev.chocoboy.create_processing.util.ResonanceMixingHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public abstract class BasinOperatingBlockEntityMixin {

    @Shadow(remap = false) protected abstract Optional<BasinBlockEntity> getBasin();

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true, remap = false)
    private void create_processing$addHotPressingCandidates(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        if (!(((Object) this) instanceof MechanicalPressBlockEntity press)) return;
        Level level = press.getLevel();
        if (level == null || level.isClientSide) return;

        Optional<BasinBlockEntity> basinOpt = getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        HeatLevel basinHeatLevel = HeatSourceHelper.getBasinHeatLevel(basin);
        if (!HeatSourceHelper.isActiveHeatLevel(basinHeatLevel)) return;

        HotPressingHelper.findInBasin(basin, level, basinHeatLevel).ifPresent(entry -> {
            List<Recipe<?>> result = new ArrayList<>(cir.getReturnValue());
            result.add(0, entry.getValue().value());
            cir.setReturnValue(result);
        });
    }

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true, remap = false)
    private void create_processing$addColdPressingCandidates(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        if (!(((Object) this) instanceof MechanicalPressBlockEntity press)) return;
        Level level = press.getLevel();
        if (level == null || level.isClientSide) return;

        Optional<BasinBlockEntity> basinOpt = getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        ColdCondition coldSourceLevel = ColdSourceHelper.getColdConditionAt(level, basin.getBlockPos().below());
        if (coldSourceLevel == null) return;

        ColdPressingHelper.findInBasin(basin, level, coldSourceLevel).ifPresent(entry -> {
            List<Recipe<?>> result = new ArrayList<>(cir.getReturnValue());
            result.add(0, entry.getValue().value());
            cir.setReturnValue(result);
        });
    }

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true, remap = false)
    private void create_processing$addColdMixingCandidates(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        if (!(((Object) this) instanceof MechanicalMixerBlockEntity mixer)) return;
        Level level = mixer.getLevel();
        if (level == null || level.isClientSide) return;

        Optional<BasinBlockEntity> basinOpt = getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        ColdCondition coldSourceLevel = ColdSourceHelper.getColdConditionAt(level, basin.getBlockPos().below());
        if (coldSourceLevel == null) return;

        ColdMixingHelper.findInBasin(basin, level, coldSourceLevel).ifPresent(holder -> {
            List<Recipe<?>> result = new ArrayList<>(cir.getReturnValue());
            result.add(0, holder.value());
            cir.setReturnValue(result);
        });
    }

    @Inject(method = "applyBasinRecipe", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$checkColdMixingCatalyst(CallbackInfo ci) {
        if (!(((Object) this) instanceof MechanicalMixerBlockEntity mixer)) return;
        Level level = mixer.getLevel();
        if (level == null) return;

        BasinOperatingBlockEntityAccessor accessor = (BasinOperatingBlockEntityAccessor) this;
        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (!(queued instanceof ColdMixingRecipe coldRecipe)) return;

        Optional<BasinBlockEntity> basinOpt = getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        ColdCondition coldSource = ColdSourceHelper.getColdConditionAt(level, basin.getBlockPos().below());
        if (coldSource != null && coldSource.satisfies(coldRecipe.getColdCondition())) return;

        accessor.create_processing$setCurrentRecipe(null);
        basin.notifyChangeOfContents();
        ci.cancel();
    }

    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true, remap = false)
    private void create_processing$addResonanceMixingCandidates(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        if (!(((Object) this) instanceof MechanicalMixerBlockEntity mixer)) return;
        Level level = mixer.getLevel();
        if (level == null || level.isClientSide) return;

        Optional<BasinBlockEntity> basinOpt = getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        if (!AmethystSourceHelper.isResonantAt(level, basin.getBlockPos().below())) return;

        ResonanceMixingHelper.findInBasin(basin, level).ifPresent(holder -> {
            List<Recipe<?>> result = new ArrayList<>(cir.getReturnValue());
            result.add(0, holder.value());
            cir.setReturnValue(result);
        });
    }

    @Inject(method = "applyBasinRecipe", at = @At("HEAD"), cancellable = true, remap = false)
    private void create_processing$checkResonanceMixingCatalyst(CallbackInfo ci) {
        if (!(((Object) this) instanceof MechanicalMixerBlockEntity mixer)) return;
        Level level = mixer.getLevel();
        if (level == null) return;

        BasinOperatingBlockEntityAccessor accessor = (BasinOperatingBlockEntityAccessor) this;
        Recipe<?> queued = accessor.create_processing$getCurrentRecipe();
        if (!(queued instanceof ResonanceMixingRecipe)) return;

        Optional<BasinBlockEntity> basinOpt = getBasin();
        if (basinOpt.isEmpty()) return;
        BasinBlockEntity basin = basinOpt.get();

        if (AmethystSourceHelper.isResonantAt(level, basin.getBlockPos().below())) return;

        accessor.create_processing$setCurrentRecipe(null);
        basin.notifyChangeOfContents();
        ci.cancel();
    }
}
