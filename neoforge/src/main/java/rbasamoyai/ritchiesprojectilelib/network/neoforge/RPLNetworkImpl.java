package rbasamoyai.ritchiesprojectilelib.network.neoforge;

import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import org.jetbrains.annotations.NotNull;

import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

import java.util.concurrent.Executor;

import static rbasamoyai.ritchiesprojectilelib.network.RPLNetwork.VERSION;

public class RPLNetworkImpl {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playBidirectional(NeoForgePacket.TYPE, NeoForgePacket.STREAM_CODEC, new DirectionalPayloadHandler<>(NeoForgePacket::handle, NeoForgePacket::handle));
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
