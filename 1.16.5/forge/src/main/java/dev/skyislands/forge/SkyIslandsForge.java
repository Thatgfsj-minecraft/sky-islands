package dev.skyislands.forge;

import dev.skyislands.IslandBootstrap;
import dev.skyislands.IslandType;
import dev.skyislands.NetherArrival;
import dev.skyislands.SkyIslandsWorldGen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("skyislands")
public class SkyIslandsForge {

    public SkyIslandsForge() {
        SkyIslandsWorldGen.register();
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * The WORLD_TYPES forge registry freezes right after the Register
     * registry-events, so the types are registered there (MOD event bus).
     */
    @Mod.EventBusSubscriber(modid = "skyislands", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class Setup {
        @SubscribeEvent
        public static void onRegisterWorldType(RegistryEvent.Register<ForgeWorldType> event) {
            for (IslandType type : IslandType.values()) {
                event.getRegistry().register(
                        new SkyIslandsForgeWorldType(type).setRegistryName(
                                new ResourceLocation(SkyIslandsWorldGen.MOD_ID, type.id())));
            }
        }
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        IslandBootstrap.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            if (player.level instanceof ServerWorld) {
                NetherArrival.onArrival(player, (ServerWorld) player.level);
            }
        }
    }
}
