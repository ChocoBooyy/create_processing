package dev.chocoboy.create_processing.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dev.chocoboy.create_processing.content.recipes.ColdCondition;
import dev.chocoboy.create_processing.util.AmethystSourceHelper;
import dev.chocoboy.create_processing.util.ColdSourceHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BasinBlockEntity.class, remap = false)
public class BasinBlockEntityMixin {

    @Unique
    private ColdCondition create_processing$prevColdCondition = null;

    @Unique
    private boolean create_processing$prevAmethystPresent = false;

    @Inject(method = "tick", at = @At("HEAD"), remap = false)
    private void create_processing$trackColdSource(CallbackInfo ci) {
        BasinBlockEntity self = (BasinBlockEntity) (Object) this;
        Level level = self.getLevel();
        if (level == null || level.isClientSide) return;

        boolean changed = false;

        ColdCondition current = ColdSourceHelper.getColdConditionAt(level, self.getBlockPos().below());
        if (current != create_processing$prevColdCondition) {
            create_processing$prevColdCondition = current;
            changed = true;
        }

        boolean amethystNow = AmethystSourceHelper.isResonantAt(level, self.getBlockPos().below());
        if (amethystNow != create_processing$prevAmethystPresent) {
            create_processing$prevAmethystPresent = amethystNow;
            changed = true;
        }

        if (changed) self.notifyChangeOfContents();
    }
}
