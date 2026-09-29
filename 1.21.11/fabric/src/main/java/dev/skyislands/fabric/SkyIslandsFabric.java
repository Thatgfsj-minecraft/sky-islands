package dev.skyislands.fabric;

import dev.skyislands.IslandBootstrap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class SkyIslandsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(IslandBootstrap::onServerStarted);
    }
}
