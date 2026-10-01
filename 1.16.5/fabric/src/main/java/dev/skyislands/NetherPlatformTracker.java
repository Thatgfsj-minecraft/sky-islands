package dev.skyislands;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * One-time flag for the void nether: the 5x4x3 glowstone arrival platform is
 * generated the first time anyone enters the nether, and never again for the
 * life of the world. Stored in the nether dimension's own saved data.
 */
public final class NetherPlatformTracker extends SavedData {

    private static final String DATA_ID = "skyislands_nether_platform";

    private boolean placed;

    public NetherPlatformTracker() {
        super(DATA_ID);
    }

    public static NetherPlatformTracker get(ServerLevel nether) {
        return nether.getDataStorage().computeIfAbsent(NetherPlatformTracker::new, DATA_ID);
    }

    public boolean placed() {
        return placed;
    }

    public void markPlaced() {
        placed = true;
        setDirty();
    }

    @Override
    public void load(CompoundTag tag) {
        this.placed = tag.getBoolean("placed");
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("placed", this.placed);
        return tag;
    }
}
