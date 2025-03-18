package rbasamoyai.ritchiesprojectilelib;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import rbasamoyai.ritchiesprojectilelib.effects.CameraModifier;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.RPLScreenShakeHandlerClient;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeContext;

public class RPLClient {

    public static void init() {

    }

    public static void onEndClientGameTick(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null)
            return;
        RPLScreenShakeHandlerClient.tick(minecraft);
    }

    public static boolean onCameraSetup(Camera camera, float partialTicks, CameraModifier modifier) {
        ScreenShakeContext screenShake = RPLScreenShakeHandlerClient.getScreenShake(partialTicks);
        modifier.setYaw(modifier.getYaw() + screenShake.getDeltaYaw());
        modifier.setPitch(modifier.getPitch() + screenShake.getDeltaPitch());
        modifier.setRoll(modifier.getRoll() + screenShake.getDeltaRoll());
        return false;
    }

    public static void onPlayerLogout(Player player) {
        RPLScreenShakeHandlerClient.onPlayerLogOut(player);
    }

}
