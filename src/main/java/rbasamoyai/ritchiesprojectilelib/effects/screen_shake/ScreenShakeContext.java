package rbasamoyai.ritchiesprojectilelib.effects.screen_shake;

public class ScreenShakeContext {
    
    private float deltaYaw = 0;
    private float deltaPitch = 0;
    private float deltaRoll = 0;

    private final float partialTicks;

    public ScreenShakeContext(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float partialTicks() { return this.partialTicks; }
    
    public void setDeltaYaw(float deltaYaw) { this.deltaYaw = deltaYaw; }
    public float getDeltaYaw() { return this.deltaYaw; }
    
    public void setDeltaPitch(float deltaPitch) { this.deltaPitch = deltaPitch; }
    public float getDeltaPitch() { return this.deltaPitch; }
    
    public void setDeltaRoll(float deltaRoll) { this.deltaRoll = deltaRoll; }
    public float getDeltaRoll() { return this.deltaRoll; }

}
