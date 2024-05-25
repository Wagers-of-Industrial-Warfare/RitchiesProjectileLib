package rbasamoyai.ritchiesprojectilelib.effects.screen_shake;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public interface ModScreenShakeHandler {

    void tick(Minecraft minecraft);
    void addEffect(ScreenShakeEffect effect);
    void clearEffects();
    void applyEffects(ScreenShakeContext context);

    class Impl implements ModScreenShakeHandler {
        private final Map<ScreenShakeEffect, Integer> activeEffects = new LinkedHashMap<>();

        protected final PerlinSimplexNoise yawNoise;
        protected final PerlinSimplexNoise pitchNoise;
        protected final PerlinSimplexNoise rollNoise;
        private final Random seedGenerator = new Random();

        public Impl() {
            long seed = this.seedGenerator.nextLong();
            this.yawNoise = new PerlinSimplexNoise(new LegacyRandomSource(seed), ImmutableList.of(-2, -1, 0));
            this.pitchNoise = new PerlinSimplexNoise(new LegacyRandomSource(seed + 1), ImmutableList.of(-2, -1, 0));
            this.rollNoise = new PerlinSimplexNoise(new LegacyRandomSource(seed + 2), ImmutableList.of(-2, -1, 0));
        }

        @Override
        public void tick(Minecraft minecraft) {
            this.activeEffects.entrySet().removeIf(entry -> entry.getKey().tick());
        }

        @Override
        public void applyEffects(ScreenShakeContext context) {
            float partialTicks = context.partialTicks();
            float deltaYaw = 0;
            float deltaPitch = 0;
            float deltaRoll = 0;
            for (Map.Entry<ScreenShakeEffect, Integer> effect : this.activeEffects.entrySet()) {
                ScreenShakeEffect modified = this.modifyScreenShake(effect.getKey());
                float f = modified.getProgressNormalized(partialTicks);
                float decay = f * f;
                double base = effect.getValue();
                double offset = modified.getProgress(partialTicks);
                deltaYaw += modified.yawMagnitude * decay * (float) this.yawNoise.getValue(0, offset * modified.yawJitter + base, false);
                deltaPitch += modified.pitchMagnitude * decay * (float) this.pitchNoise.getValue(0, offset * modified.pitchJitter + base, false);
                deltaRoll += modified.rollMagnitude * decay * (float) this.rollNoise.getValue(0, offset * modified.rollJitter + base, false);
            }
            context.setDeltaYaw(context.getDeltaYaw() + deltaYaw);
            context.setDeltaPitch(context.getDeltaPitch() + deltaPitch);
            context.setDeltaRoll(context.getDeltaRoll() + deltaRoll);
        }

        @Override public void addEffect(ScreenShakeEffect effect) { this.activeEffects.put(effect, this.seedGenerator.nextInt(65536)); }
        @Override public void clearEffects() { this.activeEffects.clear(); }

        public ScreenShakeEffect modifyScreenShake(ScreenShakeEffect effect) {
            return effect;
        }
    }

}
