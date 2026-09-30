package dev.skyislands;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelData;
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
        Marker marker = overworld.getDataStorage().computeIfAbsent(new SavedDataType<>(
                Identifier.fromNamespaceAndPath(SkyIslands.MOD_ID, MARKER_ID),
                Marker::new, Marker.CODEC, DataFixTypes.SAVED_DATA_FORCED_CHUNKS));
        if (marker.built) {
            LOGGER.info("[skyislands] {} island already present, skipping", type.id());
            return;
        }
        IslandBuilder.build(overworld, type);
        // 1.21.11 moved world spawn into a shared RespawnData on the server.
        server.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, ISLAND_CENTER, 0.0F, 0.0F));
        marker.built = true;
        marker.setDirty();
        LOGGER.info("[skyislands] built {} starting island, world spawn set to 8 64 8", type.id());
    }

    /** One-time flag: the starting island of this world has been built. */
    public static class Marker extends SavedData {

        public static final Codec<Marker> CODEC = Codec.BOOL
                .fieldOf("built")
                .xmap(Marker::new, marker -> marker.built)
                .codec();

        private boolean built;

        public Marker() {
            this(false);
        }

        public Marker(boolean built) {
            this.built = built;
        }

        @Override
        public boolean isDirty() {
            return true;
        }
    }

    private IslandBootstrap() {
    }
}
