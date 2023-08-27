package rbasamoyai.ritchiesprojectilelib.network.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

import static rbasamoyai.ritchiesprojectilelib.network.RPLNetwork.VERSION;

public class RPLNetworkImpl {

	private static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(RitchiesProjectileLib.resource("network"))
			.clientAcceptedVersions(VERSION::equals)
			.serverAcceptedVersions(VERSION::equals)
			.simpleChannel();

	public static void sidedInit() {
		int id = 0;
		NETWORK.messageBuilder(ForgeServerboundPacket.class, id++)
				.encoder(ForgeServerboundPacket::encode)
				.decoder(ForgeServerboundPacket::new)
				.consumer(ForgeServerboundPacket::handle)
				.add();

		NETWORK.messageBuilder(ForgeClientboundPacket.class, id++)
				.encoder(ForgeClientboundPacket::encode)
				.decoder(ForgeClientboundPacket::new)
				.consumer(ForgeClientboundPacket::handle)
				.add();
	}

	public static void sendToServer(RootPacket pkt) {
		NETWORK.sendToServer(new ForgeServerboundPacket(pkt));
	}

	public static void sendToClientPlayer(RootPacket pkt, ServerPlayer player) {
		NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new ForgeClientboundPacket(pkt));
	}

	public static void sendToClientTracking(RootPacket pkt, Entity tracked) {
		NETWORK.send(PacketDistributor.TRACKING_ENTITY.with(() -> tracked), new ForgeClientboundPacket(pkt));
	}

	public static void sendToClientAll(RootPacket pkt, MinecraftServer server) {
		NETWORK.send(PacketDistributor.ALL.noArg(), new ForgeClientboundPacket(pkt));
	}

}
