package dev.skyislands;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

/**
 * The sky islands chunk generator: pure void everywhere. The overworld
 * starter island is not terrain — it is placed once by {@link IslandBuilder}
 * through plain setBlock, exactly like the modern release. The nether uses
 * this generator too (via the world settings swap in {@link SkyIslandsWorldGen}),
 * which is what makes the void nether possible on 1.16.5.
 */
public final class SkyIslandsChunkGenerator extends ChunkGenerator {

    public static final ResourceLocation GENERATOR_ID = new ResourceLocation("skyislands", "void");

    public static final Codec<SkyIslandsChunkGenerator> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(g -> g.biomeSource),
            Codec.STRING.fieldOf("skyislands_kind").forGetter(g -> g.kind)
        ).apply(instance, SkyIslandsChunkGenerator::new)
    );

    /** Island type id ("classic"/"oldschool"/"single") or "nether" for the void nether. */
    private final String kind;
    private final IslandType islandType;
    private final boolean nether;

    public SkyIslandsChunkGenerator(BiomeSource biomeSource, String kind) {
        super(biomeSource, new StructureSettings(false));
        this.kind = kind;
        this.nether = "nether".equals(kind);
        this.islandType = nether ? null : IslandType.valueOf(kind.toUpperCase(java.util.Locale.ROOT));
    }

    public static SkyIslandsChunkGenerator island(BiomeSource biomeSource, IslandType type) {
        return new SkyIslandsChunkGenerator(biomeSource, type.id());
    }

    public static SkyIslandsChunkGenerator nether(BiomeSource biomeSource) {
        return new SkyIslandsChunkGenerator(biomeSource, "nether");
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
    public void buildSurfaceAndBedrock(WorldGenRegion region, ChunkAccess chunk) {
    }

    @Override
    public void fillFromNoise(LevelAccessor level, StructureFeatureManager structures, ChunkAccess chunk) {
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types type) {
        return 0;
    }

    @Override
    public BlockGetter getBaseColumn(int x, int z) {
        return new NoiseColumn(new net.minecraft.world.level.block.state.BlockState[0]);
    }

    static void registerCodec() {
        Registry.register(Registry.CHUNK_GENERATOR, GENERATOR_ID, CODEC);
    }
}
