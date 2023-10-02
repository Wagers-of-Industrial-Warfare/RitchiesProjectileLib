package rbasamoyai.ritchiesprojectilelib.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class RPLClientHandlers {

	public static void checkVersion(ClientboundCheckChannelVersionPacket pkt) {
		if (RPLNetwork.VERSION.equals(pkt.serverVersion())) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.getConnection() != null)
			mc.getConnection().onDisconnect(Component.literal("Ritchie's Projectile Library on the client uses a different network format than the server.")
					.append(" Please use a matching format."));
	}

	public static void syncPreciseMotion(ClientboundPreciseMotionSyncPacket pkt) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) return;
		Entity entity = mc.level.getEntity(pkt.entityId());
		if (entity == null) return;

		int lerpSteps = pkt.lerpSteps();
		if (lerpSteps < 1) lerpSteps = 3;
		entity.lerpTo(pkt.x(), pkt.y(), pkt.z(), pkt.yRot(), pkt.xRot(), lerpSteps, false);
		entity.setDeltaMovement(pkt.dx(), pkt.dy(), pkt.dz());
		entity.setOnGround(pkt.onGround());
	}

}
