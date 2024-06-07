package rbasamoyai.ritchiesprojectilelib.projectile_burst;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ProjectileBurstClipContext extends ClipContext {

	private final Block block;
	private final CollisionContext context;

	public ProjectileBurstClipContext(Vec3 from, Vec3 to, Block block, Fluid fluid, Entity entity, double y) {
		super(from, to, block, fluid, entity);
		this.block = block;
		this.context = new ProjectileBurstCollisionContext(y);
	}

	@Override
	public VoxelShape getBlockShape(BlockState blockState, BlockGetter level, BlockPos pos) {
		return this.block.get(blockState, level, pos, this.context);
	}

}
