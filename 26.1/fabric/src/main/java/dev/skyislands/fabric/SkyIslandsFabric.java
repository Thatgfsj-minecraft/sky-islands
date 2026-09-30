package dev.skyislands.fabric;

import dev.skyislands.IslandBootstrap;
import dev.skyislands.NetherArrival;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class SkyIslandsFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(IslandBootstrap::onServerStarted);
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(
                (player, origin, destination) -> NetherArrival.onArrival(player, destination));
    }
}
