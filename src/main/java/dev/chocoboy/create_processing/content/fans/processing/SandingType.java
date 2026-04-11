package dev.chocoboy.create_processing.content.fans.processing;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public final class SandingType extends AbstractFanProcessingType {

    private static final int COLOR_LIGHT = 0xEDEBCB;
    private static final int COLOR_DARK = 0xE7E4BB;

    private static final FanEntityTransformHelper.TransformationFeedback ZOMBIE_SANDING_FEEDBACK =
        new FanEntityTransformHelper.TransformationFeedback(
            SoundEvents.HUSK_AMBIENT,
            SoundEvents.HUSK_CONVERTED_TO_ZOMBIE,
            ParticleTypes.WHITE_ASH
        );

    @Nullable private static RecipeManager cachedPolishManager;
    @Nullable private static List<RecipeHolder<?>> cachedPolishRecipes;
    private static int cachedRecipeCount = -1;

    public SandingType() {
        super(CreateProcRecipeTypes.SANDING);
    }

    public static boolean isPolishProcessingRecipe(Recipe<?> recipe) {
        if (!(recipe instanceof ProcessingRecipe<?, ?> processingRecipe)) return false;
        ResourceLocation serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(processingRecipe.getSerializer());
        return serializerId != null && "sandpaper_polishing".equals(serializerId.getPath());
    }

    public static boolean isPolishProcessingRecipe(RecipeHolder<?> recipeHolder) {
        return isPolishProcessingRecipe(recipeHolder.value());
    }

    public static void buildPolishCache(RecipeManager manager) {
        cachedPolishRecipes = manager.getRecipes().stream()
                .filter(SandingType::isPolishProcessingRecipe)
                .collect(Collectors.toList());
        cachedPolishManager = manager;
        cachedRecipeCount = manager.getRecipes().size();
    }

    @Nullable
    public static List<RecipeHolder<?>> getPolishRecipes(RecipeManager manager) {
        if (cachedPolishRecipes == null
                || cachedPolishManager != manager
                || cachedRecipeCount != manager.getRecipes().size()) {
            buildPolishCache(manager);
        }
        return cachedPolishRecipes;
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        return isValidAtBlockTag(level, pos, CreateProcTags.SANDING_CATALYST_BLOCKS);
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (super.canProcess(stack, level)) return true;

        List<RecipeHolder<?>> polishRecipes = getPolishRecipes(level.getRecipeManager());
        if (polishRecipes == null || polishRecipes.isEmpty()) return false;

        for (RecipeHolder<?> holder : polishRecipes) {
            if (!(holder.value() instanceof ProcessingRecipe<?, ?> pr)) continue;
            List<Ingredient> ingredients = pr.getIngredients();
            if (ingredients.size() == 1 && ingredients.getFirst().test(stack)) return true;
        }
        return false;
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        List<ItemStack> directResult = super.process(stack, level);
        if (directResult != null) return directResult;

        List<RecipeHolder<?>> polishRecipes = getPolishRecipes(level.getRecipeManager());
        if (polishRecipes == null || polishRecipes.isEmpty()) return null;

        for (RecipeHolder<?> holder : polishRecipes) {
            if (!(holder.value() instanceof ProcessingRecipe<?, ?> pr)) continue;
            List<Ingredient> ingredients = pr.getIngredients();
            if (ingredients.size() != 1 || !ingredients.getFirst().test(stack)) continue;
            @SuppressWarnings({"rawtypes"})
            List<ItemStack> result = RecipeApplier.applyRecipeOn(level, stack, (ProcessingRecipe) pr, false);
            if (result != null) return result;
        }
        return null;
    }

    @Override
    public int getPriority() {
        return 691275;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        spawnDustWithParticle(level, pos, COLOR_LIGHT, ParticleTypes.CRIT);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        morphStandardAirFlow(particleAccess, random, COLOR_DARK, COLOR_LIGHT, 1 / 24f, ParticleTypes.WHITE_ASH);
    }

    @Override
    protected void affectLivingEntity(LivingEntity living, Level level) {
        if (living.getType() == EntityType.ZOMBIE
                && FanEntityTransformHelper.transformMob(level, living, EntityType.HUSK, ZOMBIE_SANDING_FEEDBACK)) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
        if (living.tickCount % 60 == 0) {
            living.hurt(level.damageSources().generic(), 1.0f);
        }

        FanProcessingSounds.playSanding(level, living.blockPosition());
    }
}
