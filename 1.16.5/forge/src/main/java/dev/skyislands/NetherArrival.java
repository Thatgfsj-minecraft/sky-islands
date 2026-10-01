package dev.skyislands;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.block.BlockState;

/**
 * Nether initialization flow: the first time anyone enters the void nether,
 * a 5x4x3 glowstone platform is generated under their feet — exactly once
 * for the life of the world, remembered in saved data. Every later entry,
 * anywhere and by any means, never places a single block.
 */
public final class NetherArrival {

    private NetherArrival() {
    }

    public static void onArrival(ServerPlayerEntity player, ServerWorld level) {
        if (level.dimension() != World.NETHER) {
            return;
        }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (!SkyIslandsWorldGen.isNetherVoid(generator)) {
            return;
        }
        NetherPlatformTracker tracker = NetherPlatformTracker.get(level);
        if (tracker.placed()) {
            return;
        }
        ensurePlatform(level, player.blockPosition());
        tracker.markPlaced();
    }

    private static void ensurePlatform(ServerWorld level, BlockPos feet) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 1; dz++) {
                for (int layer = 1; layer <= 3; layer++) {
                    BlockPos pos = feet.offset(dx, -layer, dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir() || state.getMaterial().isReplaceable()) {
                        level.setBlock(pos, net.minecraft.block.Blocks.GLOWSTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
