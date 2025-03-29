package rbasamoyai.ritchiesprojectilelib.neoforge;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.IEventBus;
import rbasamoyai.ritchiesprojectilelib.RPLClient;
import rbasamoyai.ritchiesprojectilelib.network.neoforge.RPLNetworkImpl;

public class RPLNeoForgeClient {

    public static void init(IEventBus modBus, IEventBus forgeBus) {
        RPLClient.init();

        forgeBus.addListener(RPLNeoForgeClient::onClientGameTick);
        //forgeBus.addListener(RPLForgeClient::onCameraSetup);
        forgeBus.addListener(RPLNeoForgeClient::onPlayerLogOut);
    }

    public static void onClientGameTick(final ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        RPLClient.onEndClientGameTick(minecraft);
    }

    //todo: event is not cancellable
    /*public static void onCameraSetup(final ViewportEvent.ComputeCameraAngles event) {
        if (RPLClient.onCameraSetup(event.getCamera(), (float) event.getPartialTick(), new ForgeCameraModifier(event)) && event.isCancelable()) {
            event.setCanceled(true);
        }
    }*/

    public static void onPlayerLogOut(final PlayerEvent.PlayerLoggedOutEvent event) {
        RPLClient.onPlayerLogout(event.getEntity());
    }

}
