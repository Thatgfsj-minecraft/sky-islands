package dev.skyislands;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Detects sky island worlds when a server starts and builds the starting
 * island once. Runs only when the overworld generator is this mod's void
 * generator; a per-world saved-data marker keeps the island (and its chest)
 * from ever being rebuilt in an existing world, so looted starter chests can
 * never be duplicated.
 */
public final class IslandBootstrap {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String MARKER_ID = "skyislands_island";
    /** Island center; also the world spawn set right after building. */
    public static final BlockPos ISLAND_CENTER = new BlockPos(8, 64, 8);

    public static void onServerStarted(net.minecraft.server.MinecraftServer server) {
        ServerWorld overworld = server.overworld();
        if (overworld == null) {
            return;
        }
        IslandType type = SkyIslandsWorldGen.overworldIslandType(
                overworld.getChunkSource().getGenerator());
        if (type == null) {
            return; // not a sky island world
        }
        Marker marker = overworld.getDataStorage().computeIfAbsent(Marker::new, MARKER_ID);
        if (marker.built) {
            LOGGER.info( "[skyislands] {} island already present, skipping", type.id());
            return;
        }
        IslandBuilder.build(overworld, type);
        overworld.setDefaultSpawnPos(ISLAND_CENTER, 0.0F);
        marker.built = true;
        marker.setDirty();
        LOGGER.info( "[skyislands] built {} starting island, world spawn set to 8 64 8", type.id());
    }

    /** One-time flag: the starting island of this world has been built. */
    public static class Marker extends WorldSavedData {

        private boolean built;

        public Marker() {
            super(MARKER_ID);
        }

        @Override
        public void load(CompoundNBT tag) {
            this.built = tag.getBoolean("built");
        }

        @Override
        public CompoundNBT save(CompoundNBT tag) {
            tag.putBoolean("built", this.built);
            return tag;
        }
    }

    private IslandBootstrap() {
    }
}
