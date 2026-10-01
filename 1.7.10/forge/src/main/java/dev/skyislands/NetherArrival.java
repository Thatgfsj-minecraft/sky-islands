package dev.skyislands;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/**
 * Nether initialization flow (1.7.10). The era has no player-changed-
 * dimension event, so the first nether entry is detected by a server-tick
 * poll — the first time anyone stands in the void nether, a 5x4x3 glowstone
 * platform is generated under their feet, exactly once for the life of the
 * world (remembered in the nether's per-world saved data).
 */
public final class NetherArrival {

    /**
     * Registered as an INSTANCE on purpose: the 1.7.10 EventBus lacks the
     * static-class branch later generations have, so register(Class) scans
     * java.lang.Class itself and silently registers zero listeners.
     */
    public static final NetherArrival INSTANCE = new NetherArrival();

    private NetherArrival() {
    }

    public static void register() {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof EntityPlayerMP)) {
            return;
        }
        EntityPlayer player = event.player;
        if (player.dimension != -1) {
            return;
        }
        if (!(player.worldObj instanceof WorldServer)) {
            return;
        }
        WorldServer nether = (WorldServer) player.worldObj;
        if (!(nether.provider instanceof VoidWorldProviderHell)) {
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
        int feetX = MathHelper.floor_double(player.posX);
        int feetY = MathHelper.floor_double(player.posY);
        int feetZ = MathHelper.floor_double(player.posZ);
        ensurePlatform(nether, feetX, feetY, feetZ);
        tracker.markPlaced();
    }

    private static void ensurePlatform(WorldServer level, int feetX, int feetY, int feetZ) {
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 1; dz++) {
                for (int layer = 1; layer <= 3; layer++) {
                    int x = feetX + dx;
                    int y = feetY - layer;
                    int z = feetZ + dz;
                    if (!level.blockExists(x, y, z)) {
                        continue; // never force chunk loads during arrival
                    }
                    Block block = level.getBlock(x, y, z);
                    if (block.isAir(level, x, y, z) || block.getMaterial().isReplaceable()) {
                        level.setBlock(x, y, z, Blocks.glowstone, 0, 3);
                    }
                }
            }
        }
    }
}
