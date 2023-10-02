package rbasamoyai.ritchiesprojectilelib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraftforge.api.ModLoadingContext;
import net.minecraftforge.api.fml.event.config.ModConfigEvents;
import net.minecraftforge.fml.config.ModConfig;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.config.RPLConfigs;
import rbasamoyai.ritchiesprojectilelib.network.fabric.RPLNetworkImpl;

public class RitchiesProjectileLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        RitchiesProjectileLib.init();
        RPLNetworkImpl.serverInit();
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerTickEvents.END_WORLD_TICK.register(this::onServerLevelTickEnd);

        RPLConfigs.registerConfigs((t, c) -> ModLoadingContext.registerConfig(RitchiesProjectileLib.MOD_ID, t, c));

        ModConfigEvents.loading(RitchiesProjectileLib.MOD_ID).register(this::onModConfigLoad);
        ModConfigEvents.reloading(RitchiesProjectileLib.MOD_ID).register(this::onModConfigReload);
    }

    public void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        RitchiesProjectileLib.onPlayerJoin(handler.getPlayer());
    }

    public void onServerLevelTickEnd(ServerLevel level) {
        RitchiesProjectileLib.onServerLevelTickEnd(level);
    }

    public void onModConfigLoad(ModConfig config) {
        RPLConfigs.onModConfigLoad(config);
    }

    public void onModConfigReload(ModConfig config) {
        RPLConfigs.onModConfigReload(config);
    }

}
