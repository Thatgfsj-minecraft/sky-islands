package dev.skyislands;

import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Void nether provider for 1.7.10. The nether ignores WorldType, so (like
 * the era's skyblock packs) we re-register provider type -1 with this
 * provider via DimensionManager.registerProviderType. The swap is
 * conditional on the overworld being a sky islands world: every other world
 * keeps the vanilla hell generator unchanged.
 */
public final class VoidWorldProviderHell extends WorldProviderHell {

    @Override
    public IChunkProvider createChunkGenerator() {
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld != null
                && overworld.getWorldInfo().getTerrainType() instanceof SkyIslandsWorldType) {
            return new VoidChunkProvider(this.worldObj);
        }
        return super.createChunkGenerator();
    }
}
