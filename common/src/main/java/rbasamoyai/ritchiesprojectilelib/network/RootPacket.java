package rbasamoyai.ritchiesprojectilelib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.Executor;

public interface RootPacket {

	void rootEncode(FriendlyByteBuf buf);
	void handle(Executor exec, PacketListener listener, ServerPlayer sender);

}
