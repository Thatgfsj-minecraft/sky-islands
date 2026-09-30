package dev.skyislands;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

/**
 * Detects sky island worlds when a server starts and builds the starting
 * island once. Runs only when the overworld generator uses one of this mod's
 * noise settings entries; a saved-data marker keeps the island (and its
 * chest) from ever being rebuilt in an existing world, so looted starter
 * chests can never be duplicated.
 */
public final class IslandBootstrap {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String MARKER_ID = "skyislands_island";
    /** Island center; also the world spawn set right after building. */
    public static final BlockPos ISLAND_CENTER = new BlockPos(8, 64, 8);

    public static void onServerStarted(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        if (overworld == null) {
            return;
        }
        IslandType type = null;
        ChunkGenerator generator = overworld.getChunkSource().getGenerator();
        if (generator instanceof NoiseBasedChunkGenerator noise) {
            for (IslandType candidate : IslandType.values()) {
                if (noise.stable(SkyIslands.noiseKey(candidate))) {
                    type = candidate;
                    break;
                }
            }
        }
        if (type == null) {
            return; // not a sky island world
        }
        Marker marker = overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(Marker::new, Marker::load, DataFixTypes.SAVED_DATA_FORCED_CHUNKS),
                MARKER_ID);
        if (marker.built) {
            LOGGER.info("[skyislands] {} island already present, skipping", type.id());
            return;
        }
        IslandBuilder.build(overworld, type);
        overworld.setDefaultSpawnPos(ISLAND_CENTER, 0.0F);
        marker.built = true;
        marker.setDirty();
        LOGGER.info("[skyislands] built {} starting island, world spawn set to 8 64 8", type.id());
    }

    /** One-time flag: the starting island of this world has been built. */
    public static class Marker extends SavedData {

        private boolean built;

        public static Marker load(CompoundTag tag, HolderLookup.Provider provider) {
            Marker marker = new Marker();
            marker.built = tag.getBoolean("built");
            return marker;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
            tag.putBoolean("built", built);
            return tag;
        }
    }

    private IslandBootstrap() {
    }
}
