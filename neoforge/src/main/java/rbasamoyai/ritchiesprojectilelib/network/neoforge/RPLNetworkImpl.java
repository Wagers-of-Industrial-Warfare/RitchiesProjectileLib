package rbasamoyai.ritchiesprojectilelib.network.neoforge;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

import static rbasamoyai.ritchiesprojectilelib.network.RPLNetwork.VERSION;

public class RPLNetworkImpl {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
		registrar.commonBidirectional(NeoForgePacket.TYPE, NeoForgePacket.STREAM_CODEC, new DirectionalPayloadHandler<>(NeoForgePacket::handle, NeoForgePacket::handle));
    }

    public static void sidedInit() {
	}

	public static void sendToServer(RootPacket pkt) {
		PacketDistributor.sendToServer(new NeoForgePacket(pkt));
	}

	public static void sendToClientPlayer(RootPacket pkt, ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new NeoForgePacket(pkt));
	}

	public static void sendToClientTracking(RootPacket pkt, Entity tracked) {
		PacketDistributor.sendToPlayersTrackingEntity(tracked, new NeoForgePacket(pkt));
	}

	public static void sendToClientAll(RootPacket pkt, MinecraftServer server) {
		PacketDistributor.sendToAllPlayers(new NeoForgePacket(pkt));
	}

}
