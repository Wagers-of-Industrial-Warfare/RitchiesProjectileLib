package rbasamoyai.ritchiesprojectilelib.fabric;

import net.fabricmc.api.ModInitializer;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;

public class RitchiesProjectileLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RitchiesProjectileLib.init();
    }
}
