package rbasamoyai.ritchiesprojectilelib.forge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;

@Mod(RitchiesProjectileLib.MOD_ID)
public class RitchiesProjectileLibForge {

    public RitchiesProjectileLibForge() {
        RitchiesProjectileLib.init();

        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onPlayerLogin);
    }

    public void onPlayerLogin(final PlayerLoggedInEvent evt) {
        if (evt.getPlayer() instanceof ServerPlayer splayer) {
            RitchiesProjectileLib.onPlayerJoin(splayer);
        }
    }

}
