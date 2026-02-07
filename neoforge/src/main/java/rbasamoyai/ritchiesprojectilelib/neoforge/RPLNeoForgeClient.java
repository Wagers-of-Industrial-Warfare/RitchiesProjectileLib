package rbasamoyai.ritchiesprojectilelib.neoforge;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import rbasamoyai.ritchiesprojectilelib.RPLClient;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;

@EventBusSubscriber(modid = RitchiesProjectileLib.MOD_ID)
public class RPLNeoForgeClient {

    @SubscribeEvent
    public static void onClientGameTick(final ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        RPLClient.onEndClientGameTick(minecraft);
    }

    @SubscribeEvent
    public static void onCameraSetup(final ViewportEvent.ComputeCameraAngles event) {
        RPLClient.onCameraSetup(event.getCamera(), (float) event.getPartialTick(), new NeoForgeCameraModifier(event));
    }

    @SubscribeEvent
    public static void onPlayerLogOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        RPLClient.onPlayerLogout(event.getEntity());
    }

}
