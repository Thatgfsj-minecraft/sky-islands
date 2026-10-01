package dev.skyislands;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import com.mojang.serialization.Lifecycle;

import java.util.Map;
import java.util.Properties;

/**
 * Mod constants plus world construction for the sky island world types.
 * On 1.16.5 the dedicated server only understands a hardcoded level-type
 * switch (see vanilla {@code WorldGenSettings.create}), and the nether stem
 * of a custom world type would stay vanilla — so both loaders install a
 * mixin on {@code WorldGenSettings.create} that routes our level-type ids to
 * {@link #createFromProperties}, which also swaps the nether stem to the
 * shared void nether generator (parity with the modern world presets).
 */
public final class SkyIslandsWorldGen {

    public static final String MOD_ID = "skyislands";
    public static final String MOD_NAME = "Sky Islands";

    private SkyIslandsWorldGen() {
    }

    /** Must run before any world exists (mod init on both loaders). */
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
        return SkyIslandsChunkGenerator.island(new OverworldBiomeSource(seed, false, false, biomes), type);
    }

    public static ChunkGenerator netherGenerator(Registry<Biome> biomes, long seed) {
        return SkyIslandsChunkGenerator.nether(MultiNoiseBiomeSource.Preset.NETHER.biomeSource(biomes, seed));
    }

    /** Full world settings: our overworld, our void nether, vanilla end. */
    public static WorldGenSettings createSettings(RegistryAccess access, long seed, boolean structures, IslandType type) {
        Registry<DimensionType> dimTypes = access.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        Registry<Biome> biomes = access.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<NoiseGeneratorSettings> noise = access.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);

        MappedRegistry<LevelStem> stems = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental());
        stems.register(LevelStem.OVERWORLD, new LevelStem(
            () -> dimTypes.getOrThrow(DimensionType.OVERWORLD_LOCATION),
            overworldGenerator(biomes, seed, type)), Lifecycle.stable());
        stems.register(LevelStem.NETHER, new LevelStem(
            () -> dimTypes.getOrThrow(DimensionType.NETHER_LOCATION),
            netherGenerator(biomes, seed)), Lifecycle.stable());

        // Copy the vanilla end stem (defaultDimensions only contains nether + end).
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry
                : DimensionType.defaultDimensions(dimTypes, biomes, noise, seed).entrySet()) {
            if (entry.getKey() != LevelStem.NETHER) {
                stems.register(entry.getKey(), entry.getValue(), Lifecycle.stable());
            }
        }
        return new WorldGenSettings(seed, structures, false, stems);
    }

    /** Mirrors the vanilla create() seed/structures parsing for our level-type ids. */
    public static WorldGenSettings createFromProperties(RegistryAccess access, Properties properties, IslandType type) {
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
        String structuresProp = properties.getProperty("generate-structures");
        boolean structures = structuresProp == null || Boolean.parseBoolean(structuresProp);
        return createSettings(access, seed, structures, type);
    }
}
