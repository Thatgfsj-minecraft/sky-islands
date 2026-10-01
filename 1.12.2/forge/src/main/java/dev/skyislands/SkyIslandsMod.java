package dev.skyislands;

import net.minecraftforge.common.DimensionManager;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;

/**
 * Sky Islands for 1.12.2 (Forge/FML). Registers the three world types by
 * constructing them (vanilla keeps them in WorldType.WORLD_TYPES, which both
 * the world creation screen and the dedicated server's level-type parsing
 * consume), re-registers the nether provider for the conditional void
 * nether, and builds the starter island once per world at server start.
 */
@Mod(modid = SkyIslandsMod.MODID, name = SkyIslandsMod.MODNAME, version = SkyIslandsMod.VERSION,
        acceptedMinecraftVersions = "[1.12.2]", dependencies = "after:forge")
public class SkyIslandsMod {

    public static final String MODID = "skyislands";
    public static final String MODNAME = "Sky Islands";
    public static final String VERSION = "1.5.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Touch the enum so the world types are registered as early as possible.
        IslandType.values();
        installVoidNether();
        MinecraftForge.EVENT_BUS.register(IslandBootstrap.class);
        MinecraftForge.EVENT_BUS.register(NetherArrival.class);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        IslandBootstrap.onServerStarted(event);
    }

    /**
     * Re-registers dimension -1 with a provider that swaps the chunk
     * generator only when the overworld is a sky islands world (see
     * VoidNetherProvider). The vanilla NETHER DimensionType instance stays
     * in place; only the id -> provider mapping changes, with the same
     * "_nether" folder suffix so saves stay compatible.
     */
    private static void installVoidNether() {
        if (!DimensionManager.isDimensionRegistered(-1)) {
            return;
        }
        DimensionType voidNether = DimensionType.register(
                "skyislands_void_nether", "_nether", -1, VoidNetherProvider.class, false);
        DimensionManager.unregisterDimension(-1);
        DimensionManager.registerDimension(-1, voidNether);
    }
}
