package dev.skyislands.neoforge;

import dev.skyislands.IslandBootstrap;
import dev.skyislands.NetherArrival;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod("skyislands")
public class SkyIslandsNeoForge {

    public SkyIslandsNeoForge() {
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        IslandBootstrap.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
            NetherArrival.onArrival(player, level);
        }
    }
}
