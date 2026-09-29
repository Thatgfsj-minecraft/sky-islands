package dev.skyislands.neoforge;

import dev.skyislands.IslandBootstrap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod("skyislands")
public class SkyIslandsNeoForge {

    public SkyIslandsNeoForge() {
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        IslandBootstrap.onServerStarted(event.getServer());
    }
}
