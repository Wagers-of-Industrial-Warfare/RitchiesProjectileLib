package rbasamoyai.ritchiesprojectilelib.projectile_burst;

import static net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.ritchiesprojectilelib.network.ClientboundSyncBurstSubProjectilesPacket;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

public abstract class ProjectileBurst extends Projectile {

	protected int age = 0;
	protected final List<SubProjectile> subProjectiles = new LinkedList<>();

	protected ProjectileBurst(EntityType<? extends Projectile> entityType, Level level) { super(entityType, level); }

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		ListTag list = new ListTag();
		for (SubProjectile subProjectile : this.subProjectiles)
			list.add(subProjectile.toTag());
		tag.put("SubProjectiles", list);
		tag.putInt("Age", this.age);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.subProjectiles.clear();
		ListTag list = tag.getList("SubProjectiles", Tag.TAG_COMPOUND);
		int sz = list.size();
		for (int i = 0; i < sz; ++i)
			this.subProjectiles.add(SubProjectile.fromTag(list.getCompound(i)));
		this.age = tag.getInt("Age");
	}

	@Override
	protected void defineSynchedData() {
	}

	protected void syncAllDataToServer() {
		RPLNetwork.sendToClientTracking(new ClientboundSyncBurstSubProjectilesPacket(this.getId(), this.age,
            new LinkedList<>(this.subProjectiles)), this);
	}

	public void updateClientData(int age, List<SubProjectile> subProjectiles) {
		this.age = age;
		this.subProjectiles.clear();
		this.subProjectiles.addAll(subProjectiles);
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide && (this.tickCount % 10 == 1))
			this.syncAllDataToServer();
		for (Iterator<SubProjectile> iter = this.subProjectiles.iterator(); iter.hasNext(); ) {
			SubProjectile subProjectile = iter.next();
			HitResult result = this.clipAndDamage(subProjectile);
			if (result.getType() != HitResult.Type.MISS) {
				this.onSubProjectileHit(result, subProjectile);
				iter.remove();
				continue;
			}
			subProjectile.displacement[0] += subProjectile.velocity[0];
			subProjectile.displacement[1] += subProjectile.velocity[1];
			subProjectile.displacement[2] += subProjectile.velocity[2];
            this.applyForces(subProjectile.velocity);
		}
		super.tick();
		if (++this.age >= this.getLifetime())
			this.discard();
	}

    protected abstract int getLifetime();
    protected abstract void applyForces(double[] velocity);

	protected HitResult clipAndDamage(SubProjectile info) {
		Vec3 vel = new Vec3(info.velocity[0], info.velocity[1], info.velocity[2]);
		Vec3 start = new Vec3(info.displacement[0] + this.getX(), info.displacement[1] + this.getY(), info.displacement[2] + this.getZ());
		Vec3 end = start.add(vel);
		double halfHeight = this.getSubProjectileHeight() / 2d;
		double halfWidth = this.getSubProjectileWidth() / 2d;
		HitResult hitResult = this.level.clip(new ProjectileBurstClipContext(start, end, ClipContext.Block.COLLIDER,
			ClipContext.Fluid.NONE, this, start.y - halfHeight));
		if (hitResult.getType() != HitResult.Type.MISS)
			end = hitResult.getLocation();
		AABB aabb = new AABB(start.x - halfWidth, start.y - halfHeight, start.z - halfWidth, start.x + halfWidth,
			start.y + halfHeight, start.z + halfWidth);
		HitResult hitResult2 = getEntityHitResult(this.level, this, start, end, aabb.expandTowards(vel).inflate(1.0), this::canHitEntity);
		if (hitResult2 != null)
			hitResult = hitResult2;
		return hitResult;
	}

	public void addSubProjectile(double dispX, double dispY, double dispZ, double velX, double velY, double velZ) {
		this.subProjectiles.add(new SubProjectile(new double[]{ dispX, dispY, dispZ },
			new double[]{ velX, velY, velZ }));
	}

	public int getSubProjectileCount() { return this.subProjectiles.size(); }
	public List<SubProjectile> getSubProjectiles() { return this.subProjectiles; }

	public abstract double getSubProjectileWidth();
	public abstract double getSubProjectileHeight();

	@Override public boolean isPickable() { return false; }

	@Override protected boolean canHitEntity(Entity target) { return super.canHitEntity(target) && !target.noPhysics; }

	protected void onSubProjectileHit(HitResult result, SubProjectile subProjectile) {
		HitResult.Type type = result.getType();
		if (type == HitResult.Type.ENTITY) {
			this.onSubProjectileHitEntity((EntityHitResult) result, subProjectile);
		} else if (type == HitResult.Type.BLOCK) {
			this.onSubProjectileHitBlock((BlockHitResult) result, subProjectile);
		}
		if (type != HitResult.Type.MISS) {
			this.gameEvent(GameEvent.PROJECTILE_LAND, null, new BlockPos(subProjectile.displacement[0] + this.getX(),
				subProjectile.displacement[1] + this.getY(), subProjectile.displacement[2] + this.getZ()));
		}
	}

	protected void onSubProjectileHitEntity(EntityHitResult result, SubProjectile subProjectile) {
	}

	protected void onSubProjectileHitBlock(BlockHitResult result, SubProjectile subProjectile) {
        BlockState blockState = this.level.getBlockState(result.getBlockPos());
        blockState.onProjectileHit(this.level, blockState, result, this);
	}

	protected abstract DamageSource getDamageSource();

	public record SubProjectile(double[] displacement, double[] velocity) {
		public CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			ListTag posTag = new ListTag();
			posTag.add(DoubleTag.valueOf(this.displacement[0]));
			posTag.add(DoubleTag.valueOf(this.displacement[1]));
			posTag.add(DoubleTag.valueOf(this.displacement[2]));
			tag.put("Pos", posTag);
			ListTag velTag = new ListTag();
			velTag.add(DoubleTag.valueOf(this.velocity[0]));
			velTag.add(DoubleTag.valueOf(this.velocity[1]));
			velTag.add(DoubleTag.valueOf(this.velocity[2]));
			tag.put("Vel", velTag);
			return tag;
		}

		public static SubProjectile fromTag(CompoundTag tag) {
			ListTag posTag = tag.getList("Pos", Tag.TAG_DOUBLE);
			ListTag velTag = tag.getList("Vel", Tag.TAG_DOUBLE);
			return new SubProjectile(new double[]{ posTag.getDouble(0), posTag.getDouble(1), posTag.getDouble(2) },
				new double[]{ velTag.getDouble(0), velTag.getDouble(1), velTag.getDouble(2) });
		}
	}

}
