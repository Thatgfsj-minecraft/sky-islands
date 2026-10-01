package dev.skyislands.client;

import dev.skyislands.IslandType;
import dev.skyislands.SkyIslandsWorldGen;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;

/**
 * The GUI world type entry for one island type. Overrides create() (the
 * method the preset cycling button calls) so GUI-created worlds also get the
 * void nether stem, matching the modern world presets. Requires the widened
 * WorldPreset constructor (access widener on Fabric).
 */
public final class SkyIslandsWorldPreset extends WorldPreset {

    private final IslandType type;

    public SkyIslandsWorldPreset(IslandType type) {
        super("skyislands." + type.id());
        this.type = type;
    }

    @Override
    public WorldGenSettings create(RegistryAccess.RegistryHolder holder, long seed, boolean features, boolean bonusChest) {
        return SkyIslandsWorldGen.createSettings(holder, seed, features, type);
    }

    @Override
    protected ChunkGenerator generator(Registry<Biome> biomes, Registry<NoiseGeneratorSettings> noise, long seed) {
        return SkyIslandsWorldGen.overworldGenerator(biomes, seed, type);
    }
}
