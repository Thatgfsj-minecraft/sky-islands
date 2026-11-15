package dev.skyislands.mixin;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the world creation screen state so {@link WorldOpenFlowsMixin} can read the feature flags
 * of the world that is about to be created.
 */
@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor {

    @Accessor("uiState")
    WorldCreationUiState skyislands$uiState();
}
