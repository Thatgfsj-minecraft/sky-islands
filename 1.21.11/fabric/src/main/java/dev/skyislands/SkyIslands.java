package dev.skyislands;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

/**
 * Mod constants plus the noise settings registry keys backing each island
 * type. One noise settings entry per preset so a running world can be mapped
 * back to its island type from the overworld generator alone (the world
 * preset key itself is not persisted in the save).
 */
public final class SkyIslands {

    public static final String MOD_ID = "skyislands";
    public static final String MOD_NAME = "Sky Islands";

    public static ResourceKey<NoiseGeneratorSettings> noiseKey(IslandType type) {
        return ResourceKey.create(Registries.NOISE_SETTINGS,
                Identifier.fromNamespaceAndPath(MOD_ID, type.id()));
    }

    private SkyIslands() {
    }
}
