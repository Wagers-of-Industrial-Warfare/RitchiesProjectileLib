package rbasamoyai.ritchiesprojectilelib.effects.screen_shake;

public class ScreenShakeEffect {

    public final int duration;
    private int timer;

    public final float yawMagnitude;
    public final float pitchMagnitude;
    public final float rollMagnitude;
    public final float yawJitter;
    public final float pitchJitter;
    public final float rollJitter;

    public ScreenShakeEffect(int duration, float yawMagnitude, float pitchMagnitude, float rollMagnitude,
                             float yawJitter, float pitchJitter, float rollJitter) {
        this.duration = duration;
        this.timer = duration;
        this.yawMagnitude = yawMagnitude;
        this.pitchMagnitude = pitchMagnitude;
        this.rollMagnitude = rollMagnitude;
        this.yawJitter = yawJitter;
        this.pitchJitter = pitchJitter;
        this.rollJitter = rollJitter;
    }

    public ScreenShakeEffect(int duration, float rotationMagnitude, float jitter) {
        this(duration, rotationMagnitude, rotationMagnitude, rotationMagnitude, jitter, jitter, jitter);
    }

    public boolean tick() {
        if (this.duration < 1) return true;
        this.timer--;
        return this.timer < 1;
    }

    public float getProgress(float partialTicks) { return this.timer - partialTicks; }
    public float getProgressNormalized(float partialTicks) { return this.duration < 1 ? 0 : this.getProgress(partialTicks) / this.duration; }

    public ScreenShakeEffect copyWithProgressAndDuration(float yawMagnitude, float pitchMagnitude, float rollMagnitude,
                                                         float yawJitter, float pitchJitter, float rollJitter) {
        ScreenShakeEffect newEffect = new ScreenShakeEffect(this.duration, yawMagnitude, pitchMagnitude, rollMagnitude,
            yawJitter, pitchJitter, rollJitter);
        newEffect.timer = this.timer;
        return newEffect;
    }

}
