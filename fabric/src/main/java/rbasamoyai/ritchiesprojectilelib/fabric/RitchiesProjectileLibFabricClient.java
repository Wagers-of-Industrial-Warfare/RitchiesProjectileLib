package rbasamoyai.ritchiesprojectilelib.fabric;

import net.fabricmc.api.ClientModInitializer;
import rbasamoyai.ritchiesprojectilelib.network.fabric.RPLNetworkImpl;

public class RitchiesProjectileLibFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		RPLNetworkImpl.clientInit();
	}

}
