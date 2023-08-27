package rbasamoyai.ritchiesprojectilelib.network.forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

import java.util.function.Supplier;

public class ForgeClientboundPacket {

	private final RootPacket pkt;

	public ForgeClientboundPacket(RootPacket pkt) { this.pkt = pkt; }

	public ForgeClientboundPacket(FriendlyByteBuf buf) {
		this.pkt = RPLNetwork.constructPacket(buf, buf.readVarInt());
	}

	public void encode(FriendlyByteBuf buf) {
		RPLNetwork.writeToBuf(this.pkt, buf);
	}

	public void handle(Supplier<NetworkEvent.Context> sup) {
		NetworkEvent.Context ctx = sup.get();
		ctx.enqueueWork(() -> {
			this.pkt.handle(null, ctx.getNetworkManager().getPacketListener(), null);
		});
		ctx.setPacketHandled(true);
	}

}
