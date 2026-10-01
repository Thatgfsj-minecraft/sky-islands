package dev.skyislands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

/**
 * The sky islands chunk generator: pure void everywhere. The overworld
 * starter island is not terrain — it is placed once by {@link IslandBuilder}
 * through plain setBlock, exactly like the modern release. The nether uses
 * this generator too (via the ForgeWorldType createSettings hook), which is
 * what makes the void nether possible on 1.16.5.
 *
 * Class names follow the ForgeGradle "official" channel for 1.16.5
 * (MCP class names + Mojang member names).
 */
public final class SkyIslandsChunkGenerator extends ChunkGenerator {

    public static final ResourceLocation GENERATOR_ID = new ResourceLocation("skyislands", "void");

    public static final Codec<SkyIslandsChunkGenerator> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BiomeProvider.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
            Codec.STRING.fieldOf("skyislands_kind").forGetter(g -> g.kind)
        ).apply(instance, SkyIslandsChunkGenerator::new)
    );

    /** Island type id ("classic"/"oldschool"/"single") or "nether" for the void nether. */
    private final String kind;
    private final IslandType islandType;
    private final boolean nether;

    public SkyIslandsChunkGenerator(BiomeProvider biomeProvider, String kind) {
        super(biomeProvider, new DimensionStructuresSettings(false));
        this.kind = kind;
        this.nether = "nether".equals(kind);
        this.islandType = nether ? null : IslandType.valueOf(kind.toUpperCase(java.util.Locale.ROOT));
    }

    public static SkyIslandsChunkGenerator island(BiomeProvider biomeProvider, IslandType type) {
        return new SkyIslandsChunkGenerator(biomeProvider, type.id());
    }

    public static SkyIslandsChunkGenerator nether(BiomeProvider biomeProvider) {
        return new SkyIslandsChunkGenerator(biomeProvider, "nether");
    }

    /** The overworld island type this generator belongs to; null for the nether variant. */
    public IslandType islandType() {
        return islandType;
    }

    public boolean isNether() {
        return nether;
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public ChunkGenerator withSeed(long seed) {
        return new SkyIslandsChunkGenerator(this.biomeSource.withSeed(seed), kind);
    }

    @Override
    public void buildSurfaceAndBedrock(WorldGenRegion region, IChunk chunk) {
    }

    @Override
    public void fillFromNoise(IWorld world, StructureManager structures, IChunk chunk) {
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Type type) {
        return 0;
    }

    @Override
    public IBlockReader getBaseColumn(int x, int z) {
        return new Blockreader(new BlockState[0]);
    }

    static void registerCodec() {
        Registry.register(Registry.CHUNK_GENERATOR, GENERATOR_ID, CODEC);
    }
}
