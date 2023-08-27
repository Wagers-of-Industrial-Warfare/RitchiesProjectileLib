package rbasamoyai.ritchiesprojectilelib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.ritchiesprojectilelib.EnvExecute;

import java.util.concurrent.Executor;

public record ClientboundPreciseMotionSyncPacket(int entityId, double x, double y, double z, double dx, double dy, double dz,
												 float yRot, float xRot, boolean onGround, int lerpSteps) implements RootPacket {

	public ClientboundPreciseMotionSyncPacket(FriendlyByteBuf buf) {
		this(buf.readVarInt(), // id
				buf.readDouble(), // x
				buf.readDouble(), // y
				buf.readDouble(), // z
				buf.readDouble(), // dx
				buf.readDouble(), // dy
				buf.readDouble(), // dz
				buf.readFloat(), // yRot
				buf.readFloat(), // xRot
				buf.readBoolean(), // onGround
				buf.readVarInt()); // lerpSteps
	}

	@Override
	public void rootEncode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId)
		.writeDouble(this.x)
		.writeDouble(this.y)
		.writeDouble(this.z)
		.writeDouble(this.dx)
		.writeDouble(this.dy)
		.writeDouble(this.dz)
		.writeFloat(this.yRot)
		.writeFloat(this.xRot)
		.writeBoolean(this.onGround);
		buf.writeVarInt(this.lerpSteps);
	}

	@Override
	public void handle(Executor exec, PacketListener listener, ServerPlayer sender) {
		EnvExecute.executeOnClient(() -> () -> RPLClientHandlers.syncPreciseMotion(this));
	}

}
