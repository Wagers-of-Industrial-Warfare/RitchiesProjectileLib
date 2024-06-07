package rbasamoyai.ritchiesprojectilelib.projectile_burst;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public abstract class ProjectileBurstRenderer<T extends ProjectileBurst> extends EntityRenderer<T> {

	protected ProjectileBurstRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
		for (ProjectileBurst.SubProjectile subProjectile : entity.getSubProjectiles()) {
			poseStack.pushPose();
			double[] disp = subProjectile.displacement();
			double[] vel = subProjectile.velocity();
			poseStack.translate(disp[0] + vel[0] * partialTick, disp[1] + vel[1] * partialTick, disp[2] + vel[2] * partialTick);
			this.renderSubProjectile(subProjectile, partialTick, poseStack, buffers, packedLight);
			poseStack.popPose();
		}
		super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
	}

	protected void renderSubProjectile(ProjectileBurst.SubProjectile subProjectile, float partialTick, PoseStack poseStack,
									   MultiBufferSource buffer, int packedLight) {
	}

}
