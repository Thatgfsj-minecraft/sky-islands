package dev.skyislands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

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
    public static final BlockPos ISLAND_CENTER = new BlockPos(8, 64, 8);

    public static void onServerStarted(FMLServerStartedEvent event) {
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null) {
            return;
        }
        if (!(overworld.getWorldInfo().getTerrainType() instanceof SkyIslandsWorldType)) {
            return; // not a sky island world
        }
        SkyIslandsWorldType worldType = (SkyIslandsWorldType) overworld.getWorldInfo().getTerrainType();
        IslandType type = worldType.islandType();
        Marker marker = (Marker) overworld.getPerWorldStorage().getOrLoadData(Marker.class, MARKER_ID);
        if (marker == null) {
            marker = new Marker(MARKER_ID);
            overworld.getPerWorldStorage().setData(MARKER_ID, marker);
        }
        if (marker.built) {
            FMLLog.log.info("[skyislands] {} island already present, skipping", type.name_());
            return;
        }
        IslandBuilder.build(overworld, type);
        overworld.setSpawnPoint(ISLAND_CENTER);
        marker.built = true;
        marker.markDirty();
        FMLLog.log.info("[skyislands] built {} starting island, world spawn set to 8 64 8", type.name_());
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
        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setBoolean("built", this.built);
            return tag;
        }
    }

    private IslandBootstrap() {
    }
}
