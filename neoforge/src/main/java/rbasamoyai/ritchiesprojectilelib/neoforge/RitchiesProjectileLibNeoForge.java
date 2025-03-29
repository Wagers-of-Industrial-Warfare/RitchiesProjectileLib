package rbasamoyai.ritchiesprojectilelib.neoforge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.common.NeoForge;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.config.RPLConfigs;

@Mod(RitchiesProjectileLib.MOD_ID)
public class RitchiesProjectileLibNeoForge {

    public RitchiesProjectileLibNeoForge(IEventBus modBus) {
        RitchiesProjectileLib.init();

        ModLoadingContext mlContext = ModLoadingContext.get();

        RPLConfigs.registerConfigs(mlContext.getActiveContainer()::registerConfig);

        IEventBus forgeBus = NeoForge.EVENT_BUS;
        forgeBus.addListener(this::onPlayerLogin);
        forgeBus.addListener(this::onServerLevelTick);

        if(FMLEnvironment.dist.isClient()) {
            RPLNeoForgeClient.init(modBus, forgeBus);
        }
    }

    public void onPlayerLogin(final PlayerLoggedInEvent evt) {
        if (evt.getEntity() instanceof ServerPlayer splayer) {
            //RitchiesProjectileLib.onPlayerJoin(splayer); //todo: packet not registered at this point for whatever reason
        }
    }

    public void onServerLevelTick(final LevelTickEvent.Post evt) {
        if (evt.getLevel() instanceof ServerLevel slevel) {
            RitchiesProjectileLib.onServerLevelTickEnd(slevel);
        }
    }
}
