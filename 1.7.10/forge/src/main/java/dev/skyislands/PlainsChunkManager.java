package dev.skyislands;

import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;

import com.google.common.collect.Lists;

/**
 * Fixed plains chunk manager over the void — normal biome feel, no GenLayer
 * cost. 1.7.10 naming (snapshot_20140925).
 */
public final class PlainsChunkManager extends WorldChunkManager {

    public PlainsChunkManager() {
        super();
    }

    @Override
    public List getBiomesToSpawnIn() {
        return Lists.newArrayList(BiomeGenBase.plains);
    }

    @Override
    public BiomeGenBase getBiomeGenAt(int x, int z) {
        return BiomeGenBase.plains;
    }

    @Override
    public float[] getRainfall(float[] listToReuse, int x, int z, int width, int length) {
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new float[width * length];
        }
        for (int i = 0; i < width * length; i++) {
            listToReuse[i] = BiomeGenBase.plains.getFloatRainfall();
        }
        return listToReuse;
    }

    @Override
    public float getTemperatureAtHeight(float temperature, int height) {
        return temperature;
    }

    @Override
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] listToReuse, int x, int z, int width, int length) {
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new BiomeGenBase[width * length];
        }
        for (int i = 0; i < width * length; i++) {
            listToReuse[i] = BiomeGenBase.plains;
        }
        return listToReuse;
    }

    @Override
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] listToReuse, int x, int z, int width, int length) {
        return getBiomesForGeneration(listToReuse, x, z, width, length);
    }

    @Override
    public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] listToReuse, int x, int z, int width, int length, boolean cacheFlag) {
        return getBiomesForGeneration(listToReuse, x, z, width, length);
    }

    @Override
    public boolean areBiomesViable(int x, int z, int range, List viable) {
        return viable.contains(BiomeGenBase.plains);
    }

    @Override
    public ChunkPosition findBiomePosition(int x, int z, int range, List biomes, Random random) {
        return biomes.contains(BiomeGenBase.plains) ? new ChunkPosition(x, 0, z) : null;
    }

    @Override
    public void cleanupCache() {
    }

    @Override
    public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
        return original;
    }
}
