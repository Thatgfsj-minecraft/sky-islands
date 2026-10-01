package dev.skyislands;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.init.Biomes;

import java.util.List;

/**
 * Fixed plains biome over the void — normal biome feel, no GenLayer cost,
 * and no mob spawns beyond vanilla plains rules in an empty world.
 */
public final class PlainsBiomeProvider extends BiomeProvider {

    public PlainsBiomeProvider() {
        super();
    }

    @Override
    public List<Biome> getBiomesToSpawnIn() {
        return com.google.common.collect.Lists.newArrayList(Biomes.PLAINS);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return Biomes.PLAINS;
    }

    @Override
    public Biome getBiome(BlockPos pos, Biome defaultBiome) {
        return Biomes.PLAINS;
    }

    @Override
    public float getTemperatureAtHeight(float temperature, int height) {
        return temperature;
    }

    @Override
    public Biome[] getBiomesForGeneration(Biome[] biomes, int x, int z, int width, int height) {
        for (int i = 0; i < biomes.length; i++) {
            biomes[i] = Biomes.PLAINS;
        }
        return biomes;
    }

    @Override
    public Biome[] getBiomes(Biome[] listToReuse, int x, int z, int width, int length) {
        return getBiomes(listToReuse, x, z, width, length, true);
    }

    @Override
    public Biome[] getBiomes(Biome[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new Biome[width * length];
        }
        for (int i = 0; i < width * length; i++) {
            listToReuse[i] = Biomes.PLAINS;
        }
        return listToReuse;
    }

    @Override
    public boolean areBiomesViable(int x, int z, int radius, List<Biome> viable) {
        return viable.contains(Biomes.PLAINS);
    }

    @Override
    public BlockPos findBiomePosition(int x, int z, int range, List<Biome> biomes, java.util.Random random) {
        return biomes.contains(Biomes.PLAINS) ? new BlockPos(x, 0, z) : null;
    }

    @Override
    public void cleanupCache() {
    }

    @Override
    public boolean isFixedBiome() {
        return true;
    }

    @Override
    public Biome getFixedBiome() {
        return Biomes.PLAINS;
    }
}
