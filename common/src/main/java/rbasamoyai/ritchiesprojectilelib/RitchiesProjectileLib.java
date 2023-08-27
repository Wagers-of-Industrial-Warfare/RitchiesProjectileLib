package rbasamoyai.ritchiesprojectilelib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

public class RitchiesProjectileLib {
    public static final String MOD_ID = "ritchiesprojectilelib";
    public static final String NAME = "Ritchie's Projectile Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
        RPLNetwork.init();
    }

    public static ResourceLocation resource(String path) { return new ResourceLocation(MOD_ID, path); }

    public static void onPlayerJoin(ServerPlayer player) {
        RPLNetwork.onPlayerJoin(player);
    }

}
