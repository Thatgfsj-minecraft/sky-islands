package dev.skyislands;

import net.minecraft.world.WorldProviderHell;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;

/**
 * Sky Islands for 1.7.10 (Forge/FML). Registers the three world types by
 * constructing them (vanilla keeps them in WorldType.WORLD_TYPES, which both
 * the world creation screen and the dedicated server's level-type parsing
 * consume), re-registers nether provider type -1 for the conditional void
 * nether, and builds the starter island once per world at server start.
 */
@Mod(modid = SkyIslandsMod.MODID, name = SkyIslandsMod.MODNAME, version = SkyIslandsMod.VERSION)
public class SkyIslandsMod {

    public static final String MODID = "skyislands";
    public static final String MODNAME = "Sky Islands";
    public static final String VERSION = "1.5.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Touch the enum so the world types are registered as early as possible.
        IslandType.values();
        installVoidNether();
        IslandBootstrapHolder.register();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        IslandBootstrap.onServerStarted();
    }

    /**
     * Re-registers nether provider type -1 with a provider that swaps the
     * chunk generator only when the overworld is a sky islands world (see
     * VoidWorldProviderHell). Other worlds keep the vanilla hell generator.
     */
    private static void installVoidNether() {
        DimensionManager.registerProviderType(-1, VoidWorldProviderHell.class, false);
    }

    /** Event listener holder (static handlers, registered once). */
    private static final class IslandBootstrapHolder {
        static void register() {
            NetherArrival.register();
        }
    }
}
