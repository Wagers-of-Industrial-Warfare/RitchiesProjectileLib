package rbasamoyai.ritchiesprojectilelib.projectile_burst;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public record ProjectileBurstCollisionContext(double y) implements CollisionContext {

	@Override public boolean isDescending() { return false; }

	@Override
	public boolean isAbove(VoxelShape shape, BlockPos pos, boolean canAscend) {
		return this.y > (double) pos.getY() + shape.max(Direction.Axis.Y) - 1.0E-5F;
	}

	@Override public boolean isHoldingItem(Item item) { return false; }
	@Override public boolean canStandOnFluid(FluidState state, FluidState fluidState) { return false; }

}
