package dev.skyislands;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

/**
 * One-time flag for the void nether: the 5x4x3 glowstone arrival platform is
 * generated the first time anyone enters the nether, and never again for the
 * life of the world. Stored in the nether dimension's own saved data.
 */
public final class NetherPlatformTracker extends WorldSavedData {

    private static final String DATA_ID = "skyislands_nether_platform";

    private boolean placed;

    public NetherPlatformTracker() {
        super(DATA_ID);
    }

    public static NetherPlatformTracker get(ServerWorld nether) {
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
    public void load(CompoundNBT tag) {
        this.placed = tag.getBoolean("placed");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putBoolean("placed", this.placed);
        return tag;
    }
}
