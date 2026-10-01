package dev.skyislands.mixin;

import dev.skyislands.IslandType;
import dev.skyislands.client.SkyIslandsWorldPreset;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * The world creation screen cycles WorldPreset.PRESETS, so appending our
 * presets makes the three sky island world types selectable in the GUI.
 * Client-only; the dedicated server path goes through WorldGenSettingsMixin.
 */
@Mixin(WorldPreset.class)
public abstract class WorldPresetMixin {

    @Shadow
    @Final
    protected static List<WorldPreset> PRESETS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void skyislands$addPresets(CallbackInfo ci) {
        PRESETS.add(new SkyIslandsWorldPreset(IslandType.CLASSIC));
        PRESETS.add(new SkyIslandsWorldPreset(IslandType.SMALL));
        PRESETS.add(new SkyIslandsWorldPreset(IslandType.SINGLE));
    }
}
