package dev.skyislands;

import com.mojang.serialization.Lifecycle;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

import java.util.Map;
import java.util.Properties;

/**
 * Mod constants plus world construction for the sky island world types
 * (Forge side). Forge patches DimensionGeneratorSettings.create so that a
 * level-type matching a registered ForgeWorldType routes to its
 * createSettings override — that override installs both this mod's void
 * overworld generator and the shared void nether stem, giving full parity
 * with the modern world presets without any mixin.
 */
public final class SkyIslandsWorldGen {

    public static final String MOD_ID = "skyislands";
    public static final String MOD_NAME = "Sky Islands";

    private SkyIslandsWorldGen() {
    }

    /** Must run before any world exists (mod construction on Forge). */
    public static void register() {
        SkyIslandsChunkGenerator.registerCodec();
    }

    /** The island type of a placed overworld generator; null when not ours. */
    public static IslandType overworldIslandType(ChunkGenerator generator) {
        if (generator instanceof SkyIslandsChunkGenerator) {
            return ((SkyIslandsChunkGenerator) generator).islandType();
        }
        return null;
    }

    /** True when the given generator is the shared void nether generator. */
    public static boolean isNetherVoid(ChunkGenerator generator) {
        return generator instanceof SkyIslandsChunkGenerator && ((SkyIslandsChunkGenerator) generator).isNether();
    }

    public static ChunkGenerator overworldGenerator(Registry<Biome> biomes, long seed, IslandType type) {
        return SkyIslandsChunkGenerator.island(new OverworldBiomeProvider(seed, false, false, biomes), type);
    }

    public static ChunkGenerator netherGenerator(Registry<Biome> biomes, long seed) {
        return SkyIslandsChunkGenerator.nether(NetherBiomeProvider.Preset.NETHER.biomeSource(biomes, seed));
    }

    /** Full world settings: our overworld, our void nether, vanilla end. */
    public static DimensionGeneratorSettings createSettings(
            DynamicRegistries access, long seed, boolean structures, IslandType type) {
        Registry<DimensionType> dimTypes = access.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        Registry<Biome> biomes = access.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<DimensionSettings> noise = access.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);

        SimpleRegistry<Dimension> dims = new SimpleRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        dims.register(Dimension.OVERWORLD, new Dimension(
            () -> dimTypes.getOrThrow(DimensionType.OVERWORLD_LOCATION),
            overworldGenerator(biomes, seed, type)), Lifecycle.stable());
        dims.register(Dimension.NETHER, new Dimension(
            () -> dimTypes.getOrThrow(DimensionType.NETHER_LOCATION),
            netherGenerator(biomes, seed)), Lifecycle.stable());

        // Copy the vanilla end stem (defaultDimensions only contains nether + end).
        for (Map.Entry<RegistryKey<Dimension>, Dimension> entry
                : DimensionType.defaultDimensions(dimTypes, biomes, noise, seed).entrySet()) {
            if (entry.getKey() != Dimension.NETHER) {
                dims.register(entry.getKey(), entry.getValue(), Lifecycle.stable());
            }
        }
        return new DimensionGeneratorSettings(seed, structures, false, dims);
    }

    /** Seed parsing mirror (kept for symmetry with the fabric-side mixin). */
    public static long parseSeed(Properties properties) {
        String levelSeed = properties.getProperty("level-seed", "");
        long seed = new java.util.Random().nextLong();
        if (!levelSeed.isEmpty()) {
            try {
                long parsed = Long.parseLong(levelSeed);
                if (parsed != 0L) {
                    seed = parsed;
                }
            } catch (NumberFormatException ex) {
                seed = levelSeed.hashCode();
            }
        }
        return seed;
    }
}
