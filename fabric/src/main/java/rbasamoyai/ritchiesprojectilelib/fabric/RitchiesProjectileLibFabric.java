package rbasamoyai.ritchiesprojectilelib.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.network.fabric.RPLNetworkImpl;

public class RitchiesProjectileLibFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        RitchiesProjectileLib.init();
        RPLNetworkImpl.serverInit();
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
    }

    public void onPlayerJoin(ServerGamePacketListenerImpl handler, PacketSender sender, MinecraftServer server) {
        RitchiesProjectileLib.onPlayerJoin(handler.getPlayer());
    }

}
