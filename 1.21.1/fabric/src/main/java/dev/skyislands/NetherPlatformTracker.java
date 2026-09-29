package dev.skyislands;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * One-time flag for the void nether: the 5x4x3 glowstone arrival platform is
 * generated the first time anyone enters the nether (the moment the dimension
 * initializes), and never again for the life of the world. Stored in the
 * nether dimension's own saved data.
 */
public final class NetherPlatformTracker extends SavedData {

    private static final String DATA_ID = "skyislands_nether_platform";

    private boolean placed;

    public static NetherPlatformTracker get(ServerLevel nether) {
        return nether.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(NetherPlatformTracker::new, NetherPlatformTracker::load,
                        DataFixTypes.SAVED_DATA_FORCED_CHUNKS),
                DATA_ID);
    }

    public boolean placed() {
        return placed;
    }

    public void markPlaced() {
        placed = true;
        setDirty();
    }

    public static NetherPlatformTracker load(CompoundTag tag, HolderLookup.Provider provider) {
        NetherPlatformTracker tracker = new NetherPlatformTracker();
        tracker.placed = tag.getBoolean("placed");
        return tracker;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("placed", placed);
        return tag;
    }
}
