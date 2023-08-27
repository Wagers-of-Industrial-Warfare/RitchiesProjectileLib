package rbasamoyai.ritchiesprojectilelib.network.fabric;

import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

public class RPLNetworkImpl {

	private static final SimpleChannel NETWORK = new SimpleChannel(RitchiesProjectileLib.resource("network"));

	public static void sidedInit() {
		NETWORK.registerC2SPacket(FabricServerboundPacket.class, 0, FabricServerboundPacket::new);
		NETWORK.registerS2CPacket(FabricClientboundPacket.class, 0, FabricClientboundPacket::new);
	}

	public static void serverInit() {
		NETWORK.initServerListener();
	}

	public static void clientInit() {
		NETWORK.initClientListener();
	}

	public static void sendToServer(RootPacket pkt) { NETWORK.sendToServer(new FabricServerboundPacket(pkt)); }

	public static void sendToClientPlayer(RootPacket pkt, ServerPlayer player) { NETWORK.sendToClient(new FabricClientboundPacket(pkt), player); }
	public static void sendToClientTracking(RootPacket pkt, Entity tracked) { NETWORK.sendToClientsTracking(new FabricClientboundPacket(pkt), tracked); }
	public static void sendToClientAll(RootPacket pkt, MinecraftServer server) { NETWORK.sendToClientsInServer(new FabricClientboundPacket(pkt), server); }

}
