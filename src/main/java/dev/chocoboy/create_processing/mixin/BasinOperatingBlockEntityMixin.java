package dev.chocoboy.create_processing.mixin;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import dev.chocoboy.create_processing.util.HeatSourceHelper;
import dev.chocoboy.create_processing.util.HotPressingHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

        if (!HeatSourceHelper.isBasinHeated(basin)) return;

        HotPressingHelper.findInBasin(basin, level).ifPresent(recipe -> {
            List<Recipe<?>> result = new ArrayList<>(cir.getReturnValue());
            result.add(0, recipe.value());
            cir.setReturnValue(result);
        });
    }
}
