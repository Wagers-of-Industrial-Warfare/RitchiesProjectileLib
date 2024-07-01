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
    public final double posX;
    public final double posY;
    public final double posZ;

    public ScreenShakeEffect(int duration, float yawMagnitude, float pitchMagnitude, float rollMagnitude,
                             float yawJitter, float pitchJitter, float rollJitter, double posX, double posY, double posZ) {
        this.duration = duration;
        this.timer = duration;
        this.yawMagnitude = yawMagnitude;
        this.pitchMagnitude = pitchMagnitude;
        this.rollMagnitude = rollMagnitude;
        this.yawJitter = yawJitter;
        this.pitchJitter = pitchJitter;
        this.rollJitter = rollJitter;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public ScreenShakeEffect(int duration, float rotationMagnitude, float jitter, double posX, double posY, double posZ) {
        this(duration, rotationMagnitude, rotationMagnitude, rotationMagnitude, jitter, jitter, jitter, posZ, posY, posX);
    }

    public boolean tick() {
        if (this.duration < 1) return true;
        this.timer--;
        return this.timer < 1;
    }

    public float getProgress(float partialTicks) { return this.timer + partialTicks; }
    public float getProgressNormalized(float partialTicks) { return this.duration < 1 ? 0 : this.getProgress(partialTicks) / this.duration; }

    public ScreenShakeEffect copyWithProgressAndDuration(float yawMagnitude, float pitchMagnitude, float rollMagnitude,
                                                         float yawJitter, float pitchJitter, float rollJitter, double posX,
                                                         double posY, double posZ) {
        ScreenShakeEffect newEffect = new ScreenShakeEffect(this.duration, yawMagnitude, pitchMagnitude, rollMagnitude,
            yawJitter, pitchJitter, rollJitter, posX, posY, posZ);
        newEffect.timer = this.timer;
        return newEffect;
    }

}
