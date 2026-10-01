package dev.skyislands.mixin;

import dev.skyislands.IslandType;
import dev.skyislands.SkyIslandsWorldGen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Properties;

/**
 * 1.16.5's dedicated-server level-type switch (vanilla WorldGenSettings#create)
 * only knows flat/debug/amplified/largebiomes and silently falls back to
 * "default" for anything else. Route our level-type ids to the sky islands
 * world settings instead — this is the server-side entry for custom world
 * types on 1.16.5, and it also installs the void nether stem.
 */
@Mixin(WorldGenSettings.class)
public abstract class WorldGenSettingsMixin {

    @Inject(
        method = "create(Lnet/minecraft/core/RegistryAccess;Ljava/util/Properties;)Lnet/minecraft/world/level/levelgen/WorldGenSettings;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void skyislands$create(RegistryAccess access, Properties properties,
            CallbackInfoReturnable<WorldGenSettings> cir) {
        Object levelType = properties.get("level-type");
        IslandType type = levelType == null ? null : IslandType.fromLevelType(levelType.toString());
        if (type != null) {
            cir.setReturnValue(SkyIslandsWorldGen.createFromProperties(access, properties, type));
        }
    }
}
