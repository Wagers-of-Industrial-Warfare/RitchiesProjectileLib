package rbasamoyai.ritchiesprojectilelib.network;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.ritchiesprojectilelib.EnvExecute;
import rbasamoyai.ritchiesprojectilelib.projectile_burst.ProjectileBurst;

public record ClientboundSyncBurstSubProjectilesPacket(int entityId, int age, List<ProjectileBurst.SubProjectile> subProjectiles)
	implements RootPacket {

	public static ClientboundSyncBurstSubProjectilesPacket decode(FriendlyByteBuf buf) {
		int entityId = buf.readVarInt();
		int age = buf.readVarInt();
		int sz = buf.readVarInt();
		List<ProjectileBurst.SubProjectile> list = new LinkedList<>();
		for (int i = 0; i < sz; ++i) {
			double[] disp = new double[]{ buf.readFloat(), buf.readFloat(), buf.readFloat() };
			double[] vel = new double[]{ buf.readFloat(), buf.readFloat(), buf.readFloat() };
			list.add(new ProjectileBurst.SubProjectile(disp, vel));
		}
		return new ClientboundSyncBurstSubProjectilesPacket(entityId, age, list);
	}

	@Override
	public void rootEncode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId)
			.writeVarInt(this.age)
			.writeVarInt(this.subProjectiles.size());
		for (ProjectileBurst.SubProjectile subProjectile : this.subProjectiles) {
			double[] disp = subProjectile.displacement();
			double[] vel = subProjectile.velocity();
			buf.writeFloat((float) disp[0]);
			buf.writeFloat((float) disp[1]);
			buf.writeFloat((float) disp[2]);
			buf.writeFloat((float) vel[0]);
			buf.writeFloat((float) vel[1]);
			buf.writeFloat((float) vel[2]);
		}
	}

	@Override
	public void handle(Executor exec, PacketListener listener, @Nullable ServerPlayer sender) {
		EnvExecute.executeOnClient(() -> () -> RPLClientHandlers.addBurstSubProjectileData(this));
	}

}
