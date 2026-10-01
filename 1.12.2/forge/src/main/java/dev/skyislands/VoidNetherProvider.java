package dev.skyislands;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;

/**
 * Void nether provider for 1.12.2. The nether ignores WorldType, so (like
 * the era's skyblock packs) we re-register dimension -1 with a custom
 * DimensionType whose provider swaps the chunk generator. The swap is
 * conditional on the overworld being a sky islands world: every other world
 * keeps the vanilla hell generator unchanged.
 */
public final class VoidNetherProvider extends WorldProviderHell {

    @Override
    public IChunkGenerator createChunkGenerator() {
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld != null
                && overworld.getWorldInfo().getTerrainType() instanceof SkyIslandsWorldType) {
            return new VoidChunkGenerator(this.world);
        }
        return super.createChunkGenerator();
    }

    /**
     * Report the vanilla NETHER type so all vanilla dimension checks
     * (portals, respawn logic) keep working; only the generator differs.
     */
    @Override
    public DimensionType getDimensionType() {
        return DimensionType.NETHER;
    }
}
