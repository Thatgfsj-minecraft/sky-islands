package dev.skyislands;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.MapStorage;

/**
 * One-time flag for the void nether: the 5x4x3 glowstone arrival platform is
 * generated the first time anyone enters the nether, and never again for the
 * life of the world. Stored in the nether dimension's own per-world saved
 * data (DIM-1/data), matching the modern NetherPlatformTracker.
 */
public final class NetherPlatformTracker extends WorldSavedData {

    private static final String DATA_ID = "skyislands_nether_platform";

    private boolean placed;

    public NetherPlatformTracker(String name) {
        super(name);
    }

    public static NetherPlatformTracker get(WorldServer nether) {
        MapStorage storage = nether.perWorldStorage;
        NetherPlatformTracker tracker = (NetherPlatformTracker) storage.loadData(NetherPlatformTracker.class, DATA_ID);
        if (tracker == null) {
            tracker = new NetherPlatformTracker(DATA_ID);
            storage.setData(DATA_ID, tracker);
        }
        return tracker;
    }

    public boolean placed() {
        return placed;
    }

    public void markPlaced() {
        placed = true;
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.placed = tag.getBoolean("placed");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("placed", this.placed);
    }
}
