package dev.skyislands;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

/**
 * Nether initialization flow: the first time anyone enters the void nether,
 * a 5x4x3 glowstone platform is generated under their feet — exactly once
 * for the life of the world, remembered in saved data. Every later entry,
 * anywhere and by any means, never places a single block.
 */
public final class NetherArrival {

    private NetherArrival() {
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.toDim != -1) {
            return;
        }
        EntityPlayer player = event.player;
        if (player.world == null || !(player.world instanceof WorldServer)) {
            return;
        }
        WorldServer nether = (WorldServer) player.world;
        if (!(nether.provider instanceof VoidNetherProvider)) {
            return; // not our void nether
        }
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null
                || !(overworld.getWorldInfo().getTerrainType() instanceof SkyIslandsWorldType)) {
            return;
        }
        NetherPlatformTracker tracker = NetherPlatformTracker.get(nether);
        if (tracker.placed()) {
            return;
        }
        BlockPos feet = new BlockPos(player.posX, player.posY, player.posZ);
        ensurePlatform(nether, feet);
        tracker.markPlaced();
    }

    private static void ensurePlatform(WorldServer level, BlockPos feet) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 1; dz++) {
                for (int layer = 1; layer <= 3; layer++) {
                    BlockPos pos = feet.add(dx, -layer, dz);
                    if (!level.isBlockLoaded(pos)) {
                        continue; // never sync-load chunks during arrival
                    }
                    IBlockState state = level.getBlockState(pos);
                    if (state.getMaterial().isReplaceable()) {
                        level.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState(), 3);
                    }
                }
            }
        }
    }
}
