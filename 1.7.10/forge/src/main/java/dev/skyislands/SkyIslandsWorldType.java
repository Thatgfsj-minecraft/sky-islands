package dev.skyislands;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * World type for one island flavor. The world provider asks the world type
 * for both the chunk manager and the chunk generator, so a pure-void
 * overworld is just these two overrides. The island itself is NOT terrain:
 * it is placed once by IslandBuilder through plain setBlock (parity with
 * the modern release).
 */
public final class SkyIslandsWorldType extends WorldType {

    private final IslandType type;

    public SkyIslandsWorldType(IslandType type) {
        super(type.typeName());
        this.type = type;
    }

    public IslandType islandType() {
        return type;
    }

    @Override
    public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        return new VoidChunkProvider(world);
    }

    @Override
    public WorldChunkManager getChunkManager(World world) {
        return new PlainsChunkManager();
    }
}
