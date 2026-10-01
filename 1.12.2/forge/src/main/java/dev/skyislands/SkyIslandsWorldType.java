package dev.skyislands;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;

/**
 * World type for one island flavor. Vanilla's WorldProvider asks the world
 * type for both the biome provider and the chunk generator, so a pure-void
 * overworld is just these two overrides. The island itself is NOT terrain:
 * it is placed once by IslandBuilder through plain setBlockState (parity
 * with the modern release).
 */
public final class SkyIslandsWorldType extends WorldType {

    private final IslandType type;

    public SkyIslandsWorldType(IslandType type) {
        super(type.name_());
        this.type = type;
    }

    public IslandType islandType() {
        return type;
    }

    @Override
    public IChunkGenerator getChunkGenerator(World world, String generatorOptions) {
        return new VoidChunkGenerator(world);
    }

    @Override
    public BiomeProvider getBiomeProvider(World world) {
        return new PlainsBiomeProvider();
    }
}
