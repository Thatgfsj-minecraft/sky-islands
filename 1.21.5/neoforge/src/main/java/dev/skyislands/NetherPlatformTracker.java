package dev.skyislands;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

/**
 * One-time flag for the void nether: the 5x4x3 glowstone arrival platform is
 * generated the first time anyone enters the nether (the moment the dimension
 * initializes), and never again for the life of the world. Stored in the
 * nether dimension's own saved data.
 */
public final class NetherPlatformTracker extends SavedData {

    private static final String DATA_ID = "skyislands_nether_platform";

    public static final Codec<NetherPlatformTracker> CODEC = Codec.BOOL
            .fieldOf("placed")
            .xmap(NetherPlatformTracker::new, tracker -> tracker.placed)
            .codec();

    private static final SavedDataType<NetherPlatformTracker> TYPE = new SavedDataType<>(
            DATA_ID, NetherPlatformTracker::new, CODEC, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);

    private boolean placed;

    public NetherPlatformTracker() {
        this(false);
    }

    public NetherPlatformTracker(boolean placed) {
        this.placed = placed;
    }

    public static NetherPlatformTracker get(ServerLevel nether) {
        return nether.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean placed() {
        return placed;
    }

    public void markPlaced() {
        placed = true;
        setDirty();
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
