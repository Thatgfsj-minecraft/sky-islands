package dev.skyislands;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

/**
 * Nether arrival safety: the sky island nether has no terrain, and vanilla
 * portal creation only builds the obsidian frame, never ground. So every time
 * a player arrives in the void nether, a 5x4x3 glowstone platform is ensured
 * right below their feet (top layer at feet-1). Only air/replaceable blocks
 * are filled, so the portal frame and player builds are never damaged, and
 * the build is skipped when the feet column already sits on glowstone.
 */
public final class NetherArrival {

    private NetherArrival() {
    }

    public static void onArrival(ServerPlayer player, ServerLevel level) {
        if (level.dimension() != Level.NETHER) {
            return;
        }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        if (!(generator instanceof NoiseBasedChunkGenerator noise) || !noise.stable(SkyIslands.netherKey())) {
            return;
        }
        ensurePlatform(level, player.blockPosition());
    }

    private static void ensurePlatform(ServerLevel level, BlockPos feet) {
        if (level.getBlockState(feet.below()).is(Blocks.GLOWSTONE)) {
            return; // platform already in place
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 1; dz++) {
                for (int layer = 1; layer <= 3; layer++) {
                    BlockPos pos = feet.offset(dx, -layer, dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir() || state.canBeReplaced()) {
                        level.setBlock(pos, Blocks.GLOWSTONE.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
}
