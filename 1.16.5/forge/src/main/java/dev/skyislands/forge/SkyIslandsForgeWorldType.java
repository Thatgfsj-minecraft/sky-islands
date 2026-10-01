package dev.skyislands.forge;

import dev.skyislands.IslandType;
import dev.skyislands.SkyIslandsWorldGen;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraftforge.common.world.ForgeWorldType;

/**
 * One sky island world type on Forge 1.16.5. The IChunkGeneratorFactory
 * supplies the void overworld generator; the createSettings override swaps
 * the nether stem to the shared void nether generator (vanilla end kept),
 * which is what Forge's DimensionGeneratorSettings patch calls for both the
 * dedicated server (level-type=skyislands:<id>) and the client GUI.
 */
public final class SkyIslandsForgeWorldType extends ForgeWorldType {

    private final IslandType type;

    public SkyIslandsForgeWorldType(IslandType type) {
        super((biomes, dimensionSettings, seed, generatorSettings) ->
                SkyIslandsWorldGen.overworldGenerator(biomes, seed, type));
        this.type = type;
    }

    public IslandType islandType() {
        return type;
    }

    @Override
    public DimensionGeneratorSettings createSettings(
            DynamicRegistries registries, long seed, boolean generateStructures, boolean bonusChest,
            String generatorSettings) {
        return SkyIslandsWorldGen.createSettings(registries, seed, generateStructures, type);
    }
}
