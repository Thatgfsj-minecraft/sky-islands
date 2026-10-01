package dev.skyislands;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Detects sky island worlds when a server starts and builds the starting
 * island once. Runs only when the overworld generator is this mod's void
 * generator; a saved-data marker keeps the island (and its chest) from ever
 * being rebuilt in an existing world, so looted starter chests can never be
 * duplicated. Direct translation of the modern IslandBootstrap onto the
 * 1.16.5 SavedData API.
 */
public final class IslandBootstrap {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MARKER_ID = "skyislands_island";
    /** Island center; also the world spawn set right after building. */
    public static final BlockPos ISLAND_CENTER = new BlockPos(8, 64, 8);

    public static void onServerStarted(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        if (overworld == null) {
            return;
        }
        ChunkGenerator generator = overworld.getChunkSource().getGenerator();
        IslandType type = SkyIslandsWorldGen.overworldIslandType(generator);
        if (type == null) {
            return; // not a sky island world
        }
        Marker marker = overworld.getDataStorage().computeIfAbsent(Marker::new, MARKER_ID);
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

        public Marker() {
            super(MARKER_ID);
        }

        @Override
        public void load(CompoundTag tag) {
            this.built = tag.getBoolean("built");
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean("built", this.built);
            return tag;
        }
    }

    private IslandBootstrap() {
    }
}
