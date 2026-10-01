package dev.skyislands;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import com.google.common.collect.Lists;

/**
 * Pure void chunk provider: every chunk is empty air, no structures, no
 * decoration. Mirrors the modern skyislands:void noise settings behavior.
 * Empty chunks are built exactly like the vanilla flat generator's empty
 * base (new Chunk(world, x, z)).
 */
public final class VoidChunkProvider implements IChunkProvider {

    private final World worldObj;

    public VoidChunkProvider(World world) {
        this.worldObj = world;
    }

    @Override
    public boolean chunkExists(int x, int z) {
        return true;
    }

    @Override
    public Chunk provideChunk(int x, int z) {
        return new Chunk(this.worldObj, x, z);
    }

    @Override
    public Chunk loadChunk(int x, int z) {
        return provideChunk(x, z);
    }

    @Override
    public void populate(IChunkProvider provider, int x, int z) {
    }

    @Override
    public boolean saveChunks(boolean all, IProgressUpdate progress) {
        return true;
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "SkyIslandsVoidLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType creatureType, int x, int y, int z) {
        return Lists.newArrayList();
    }

    @Override
    public ChunkPosition findClosestStructure(World world, String structureName, int x, int y, int z) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int x, int z) {
    }

    @Override
    public void saveExtraData() {
    }
}
