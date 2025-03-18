package rbasamoyai.ritchiesprojectilelib.forge;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.config.RPLConfigs;

@Mod(RitchiesProjectileLib.MOD_ID)
public class RitchiesProjectileLibForge {

    public RitchiesProjectileLibForge() {
        RitchiesProjectileLib.init();

        ModLoadingContext mlContext = ModLoadingContext.get();
        RPLConfigs.registerConfigs(mlContext::registerConfig);

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(this::onPlayerLogin);
        forgeBus.addListener(this::onServerLevelTick);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RPLForgeClient.init(modBus, forgeBus));
    }

    public void onPlayerLogin(final PlayerLoggedInEvent evt) {
        if (evt.getEntity() instanceof ServerPlayer splayer) {
            RitchiesProjectileLib.onPlayerJoin(splayer);
        }
    }

    public void onServerLevelTick(final TickEvent.LevelTickEvent evt) {
        if (evt.level instanceof ServerLevel slevel) {
            if (evt.phase == TickEvent.Phase.END) {
                RitchiesProjectileLib.onServerLevelTickEnd(slevel);
            }
        }
    }

}
