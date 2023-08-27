package rbasamoyai.ritchiesprojectilelib.network.forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

import java.util.function.Supplier;

public class ForgeServerboundPacket {

	private final RootPacket pkt;

	public ForgeServerboundPacket(RootPacket pkt) { this.pkt = pkt; }

	public ForgeServerboundPacket(FriendlyByteBuf buf) {
		this.pkt = RPLNetwork.constructPacket(buf, buf.readVarInt());
	}

	public void encode(FriendlyByteBuf buf) {
		RPLNetwork.writeToBuf(this.pkt, buf);
	}

	public void handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			ServerPlayer sender = ctx.getSender();
			this.pkt.handle(sender.getServer(), ctx.getNetworkManager().getPacketListener(), sender);
		});
		ctx.setPacketHandled(true);
	}

}
