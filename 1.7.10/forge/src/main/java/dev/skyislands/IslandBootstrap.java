package dev.skyislands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;

/**
 * Detects sky island worlds when a server starts and builds the starting
 * island once. Runs only when the overworld terrain type is one of this
 * mod's world types; a per-world saved-data marker keeps the island (and
 * its chest) from ever being rebuilt in an existing world, so looted
 * starter chests can never be duplicated.
 */
public final class IslandBootstrap {

    private static final String MARKER_ID = "skyislands_island";
    /** Island center; also the world spawn set right after building. */
    public static final ChunkCoordinates ISLAND_CENTER = new ChunkCoordinates(8, 64, 8);

    private static final org.apache.logging.log4j.Logger LOGGER = org.apache.logging.log4j.LogManager.getLogger();

    public static void onServerStarted() {
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null) {
            return;
        }
        if (!(overworld.getWorldInfo().getTerrainType() instanceof SkyIslandsWorldType)) {
            return; // not a sky island world
        }
        SkyIslandsWorldType worldType = (SkyIslandsWorldType) overworld.getWorldInfo().getTerrainType();
        IslandType type = worldType.islandType();
        MapStorage storage = overworld.perWorldStorage;
        Marker marker = (Marker) storage.loadData(Marker.class, MARKER_ID);
        if (marker == null) {
            marker = new Marker(MARKER_ID);
            storage.setData(MARKER_ID, marker);
        }
        if (marker.built) {
            LOGGER.info("[skyislands] %s island already present, skipping", type.typeName());
            return;
        }
        IslandBuilder.build(overworld, type);
        overworld.setSpawnLocation(ISLAND_CENTER.posX, ISLAND_CENTER.posY, ISLAND_CENTER.posZ);
        marker.built = true;
        marker.markDirty();
        LOGGER.info("[skyislands] built %s starting island, world spawn set to 8 64 8", type.typeName());
    }

    /** One-time flag: the starting island of this world has been built. */
    public static class Marker extends WorldSavedData {

        private boolean built;

        public Marker(String name) {
            super(name);
        }

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            this.built = tag.getBoolean("built");
        }

        @Override
        public void writeToNBT(NBTTagCompound tag) {
            tag.setBoolean("built", this.built);
        }
    }

    private IslandBootstrap() {
    }
}
