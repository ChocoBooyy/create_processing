package dev.chocoboy.create_processing.content.fans.processing;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import dev.chocoboy.create_processing.registry.CreateProcRecipeTypes;
import dev.chocoboy.create_processing.registry.CreateProcTags;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

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

    @Nullable
    private static RecipeManager cachedPolishManager;
    @Nullable
    private static List<RecipeHolder<?>> cachedPolishRecipes;
    private static int cachedRecipeCount = -1;

    public SandingType() {
        super(CreateProcRecipeTypes.SANDING);
    }

    public static boolean isPolishProcessingRecipe(Recipe<?> recipe) {
        if (!(recipe instanceof ProcessingRecipe<?, ?> processingRecipe)) {
            return false;
        }

        ResourceLocation serializerId = BuiltInRegistries.RECIPE_SERIALIZER.getKey(processingRecipe.getSerializer());
        return serializerId != null && "sandpaper_polishing".equals(serializerId.getPath());
    }

    public static boolean isPolishProcessingRecipe(RecipeHolder<?> recipeHolder) {
        return isPolishProcessingRecipe(recipeHolder.value());
    }

    public static void buildPolishCache(RecipeManager manager) {
        List<RecipeHolder<?>> recipes = manager.getRecipes()
            .stream()
            .filter(SandingType::isPolishProcessingRecipe)
            .collect(Collectors.toList());

        cachedPolishManager = manager;
        cachedPolishRecipes = recipes;
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
        return level.getBlockState(pos).is(CreateProcTags.SANDING_CATALYST_BLOCKS);
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        if (super.canProcess(stack, level)) {
            return true;
        }

        List<RecipeHolder<?>> polishRecipes = getPolishRecipes(level.getRecipeManager());
        if (polishRecipes == null || polishRecipes.isEmpty()) {
            return false;
        }

        for (RecipeHolder<?> holder : polishRecipes) {
            Recipe<?> recipe = holder.value();
            if (!(recipe instanceof ProcessingRecipe<?, ?> processingRecipe)) {
                continue;
            }

            List<Ingredient> ingredients = processingRecipe.getIngredients();
            if (ingredients.size() == 1 && ingredients.getFirst().test(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Nullable
    public List<ItemStack> process(ItemStack stack, Level level) {
        List<ItemStack> directResult = super.process(stack, level);
        if (directResult != null) {
            return directResult;
        }

        List<RecipeHolder<?>> polishRecipes = getPolishRecipes(level.getRecipeManager());
        if (polishRecipes == null || polishRecipes.isEmpty()) {
            return null;
        }

        for (RecipeHolder<?> holder : polishRecipes) {
            Recipe<?> recipe = holder.value();
            if (!(recipe instanceof ProcessingRecipe<?, ?> processingRecipe)) {
                continue;
            }

            List<Ingredient> ingredients = processingRecipe.getIngredients();
            if (ingredients.size() != 1 || !ingredients.getFirst().test(stack)) {
                continue;
            }

            @SuppressWarnings({"rawtypes", "unchecked"})
            List<ItemStack> result = RecipeApplier.applyRecipeOn(level, stack, (ProcessingRecipe) processingRecipe, false);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Override
    public int getPriority() {
        return 691275;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0) return;
        Vector3f color = new Color(COLOR_LIGHT).asVectorF();
        level.addParticle(new DustParticleOptions(color, 1),
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
        level.addParticle(ParticleTypes.CRIT,
            pos.x + (level.random.nextFloat() - 0.5f) * 0.5f, pos.y + 0.5f,
            pos.z + (level.random.nextFloat() - 0.5f) * 0.5f, 0, 1 / 8f, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        particleAccess.setColor(Color.mixColors(COLOR_DARK, COLOR_LIGHT, random.nextFloat()));
        particleAccess.setAlpha(1f);
        if (random.nextFloat() < 1 / 24f)
            particleAccess.spawnExtraParticle(ParticleTypes.WHITE_ASH, 0.125f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide || !(entity instanceof LivingEntity living)) return;

        if (entity.getType() == EntityType.ZOMBIE
                && FanEntityTransformHelper.transformMob(level, entity, EntityType.HUSK, ZOMBIE_SANDING_FEEDBACK)) {
            return;
        }

        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
        if (living.tickCount % 60 == 0) {
            living.hurt(level.damageSources().generic(), 1.0f);
        }

        FanProcessingSounds.playSanding(level, entity.blockPosition());
    }
}
