package rbasamoyai.ritchiesprojectilelib.effects.screen_shake;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public interface ModScreenShakeHandler {

    void tick(Minecraft minecraft);
    void addEffect(ScreenShakeEffect effect);
    void clearEffects();
    void applyEffects(ScreenShakeContext context);

    class Impl implements ModScreenShakeHandler {
        protected final Set<ScreenShakeEffect> delayedShakes = new LinkedHashSet<>();
        protected Vec3 velocity = Vec3.ZERO;
        protected Vec3 acceleration = Vec3.ZERO;
        protected Vec3 displacement = Vec3.ZERO;

        protected final Random random = new Random();

        public Impl() {
        }

        @Override
        public void tick(Minecraft minecraft) {
            for (Iterator<ScreenShakeEffect> iter = this.delayedShakes.iterator(); iter.hasNext(); ) {
                ScreenShakeEffect effect = iter.next();
                if (effect.tick()) {
                    this.immediatelyAddEffect(effect);
                    iter.remove();
                }
            }
            int iterations = 10;
            double dt = (double) 1 / iterations;
            double restitution = Math.max(0.005d, this.getRestitution());
            double drag = Math.max(0.005d, this.getDrag());
            for (int i = 0; i < iterations; ++i) {
                Vec3 newRotationDisplacement = this.displacement
                    .add(this.velocity.scale(dt))
                    .add(this.acceleration.scale(0.5d * dt * dt));
                Vec3 newAccel = this.displacement.scale(-restitution)
                    .add(this.velocity.scale(-drag));
                Vec3 newVel = this.velocity
                    .add(this.acceleration.add(newAccel).scale(0.5d * dt));
                this.displacement = newRotationDisplacement;
                this.velocity = newVel;
                this.acceleration = newAccel;
                this.applyConstraints();
            }
            if (this.displacement.lengthSqr() < 1e-4d
                && this.velocity.lengthSqr() < 1e-4d
                && this.acceleration.lengthSqr() < 1e-4d) {
                this.clearEffects();
            }
        }

        @Override
        public void applyEffects(ScreenShakeContext context) {
            float partialTicks = context.partialTicks();
            Vec3 currentDisp = this.displacement
                .add(this.velocity.scale(partialTicks))
                .add(this.acceleration.scale(partialTicks * partialTicks * 0.5));
            context.setDeltaYaw(context.getDeltaYaw() + (float) currentDisp.x);
            context.setDeltaPitch(context.getDeltaPitch() + (float) currentDisp.y);
            context.setDeltaRoll(context.getDeltaRoll() + (float) currentDisp.z);
        }

        @Override
        public void addEffect(ScreenShakeEffect effect) {
            if (effect.duration <= 0) {
                this.immediatelyAddEffect(effect);
            } else {
                this.delayedShakes.add(effect);
            }
        }

        protected void immediatelyAddEffect(ScreenShakeEffect effect) {
            ScreenShakeEffect modified = this.modifyScreenShake(effect);
            double dy = modified.yawMagnitude * (this.random.nextDouble() + this.random.nextDouble()) * 0.5d;
            double dp = modified.pitchMagnitude * (this.random.nextDouble() + this.random.nextDouble()) * 0.5d;
            double dr = modified.rollMagnitude * (this.random.nextDouble() + this.random.nextDouble()) * 0.5d;
            this.velocity = this.velocity.add(dy, dp, dr);
        }

        @Override
        public void clearEffects() {
            this.displacement = Vec3.ZERO;
            this.velocity = Vec3.ZERO;
            this.acceleration = Vec3.ZERO;
        }

        protected ScreenShakeEffect modifyScreenShake(ScreenShakeEffect effect) {
            return effect;
        }

        protected double getRestitution() { return 0.1; }
        protected double getDrag() { return 0.2d; }

        protected void applyConstraints() {

        }
    }

}
