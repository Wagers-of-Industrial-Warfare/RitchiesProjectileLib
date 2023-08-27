package rbasamoyai.ritchiesprojectilelib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.ritchiesprojectilelib.EnvExecute;

import java.util.concurrent.Executor;

public record ClientboundCheckChannelVersionPacket(String serverVersion) implements RootPacket {

	public ClientboundCheckChannelVersionPacket(FriendlyByteBuf buf) {
		this(buf.readUtf());
	}


	@Override
	public void rootEncode(FriendlyByteBuf buf) {
		buf.writeUtf(this.serverVersion);
	}

	@Override
	public void handle(Executor exec, PacketListener listener, ServerPlayer sender) {
		EnvExecute.executeOnClient(() -> () -> RPLClientHandlers.checkVersion(this));
	}

}
