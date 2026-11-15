package dev.skyislands.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Removes the false "experimental settings" warnings that vanilla shows for worlds whose world
 * generation comes from a mod data pack instead of the vanilla built-in pack.
 *
 * <p>Vanilla derives the lifecycle of a world from its dynamic registries ({@code noise_settings},
 * {@code world_preset}, ...). Every entry that does not come from a vanilla built-in pack is tagged
 * {@code Lifecycle.experimental()}, so with this mod installed the lifecycle of <em>every</em> world
 * is experimental - even one that uses a plain vanilla world type - and no data pack can clear that
 * flag. That is why single player asked for a backup on every world load and again on every world
 * creation, even though the islands themselves are perfectly stable.
 *
 * <p>The warnings are only skipped when the world carries no real experimental feature flags
 * ({@code enabled_features} in {@code level.dat}). Worlds that really do enable experiments, and
 * legacy "customized" worlds, keep their warnings exactly as vanilla shows them.
 */

@Mixin(WorldOpenFlows.class)
public abstract class WorldOpenFlowsMixin {

    @Shadow
    protected abstract void openWorldLoadBundledResourcePack(
            LevelStorageSource.LevelStorageAccess levelAccess,
            WorldStem worldStem,
            PackRepository packRepository,
            Runnable onCancel);

    /**
     * Skips the "this world uses experimental settings, create a backup?" screen that vanilla shows
     * on every world load, by taking the branch vanilla takes when the lifecycle is stable.
     */
    @Inject(method = "openWorldCheckWorldStemCompatibility", at = @At("HEAD"), cancellable = true)
    private void skyislands$skipExperimentalBackupPrompt(
            LevelStorageSource.LevelStorageAccess levelAccess,
            WorldStem worldStem,
            PackRepository packRepository,
            Runnable onCancel,
            CallbackInfo ci) {
        LevelDataAndDimensions.WorldDataAndGenSettings worldData = worldStem.worldDataAndGenSettings();
        if (worldData.genSettings().options().isOldCustomizedWorld()) {
            return;
        }
        WorldData data = worldData.data();
        if (FeatureFlags.isExperimental(data.enabledFeatures())) {
            return;
        }
        this.openWorldLoadBundledResourcePack(levelAccess, worldStem, packRepository, onCancel);
        ci.cancel();
    }

    /**
     * Skips the matching "experimental settings" confirmation that vanilla shows while a world is
     * being created.
     */
    @Inject(method = "confirmWorldCreation", at = @At("HEAD"), cancellable = true)
    private static void skyislands$skipExperimentalCreationWarning(
            Minecraft minecraft,
            CreateWorldScreen parent,
            Lifecycle lifecycle,
            Runnable task,
            boolean skipWarning,
            CallbackInfo ci) {
        if (skipWarning || lifecycle == Lifecycle.stable()) {
            return;
        }
        WorldCreationContext settings = ((CreateWorldScreenAccessor) (Object) parent).skyislands$uiState().getSettings();
        if (FeatureFlags.isExperimental(settings.dataConfiguration().enabledFeatures())) {
            return;
        }
        task.run();
        ci.cancel();
    }
}
