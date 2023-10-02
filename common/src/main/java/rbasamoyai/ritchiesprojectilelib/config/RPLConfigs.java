package rbasamoyai.ritchiesprojectilelib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;

public class RPLConfigs {

	public static class Server {
		public final ForgeConfigSpec.IntValue maxChunksForceLoaded;

		Server(ForgeConfigSpec.Builder builder) {
			builder.comment("Server configuration settings for Ritchie's Projectile Library").push("server");

			maxChunksForceLoaded = builder
					.comment("The maximum amount of chunks forced to load during a single tick. Set to -1 to force load unlimited chunks.")
					.translation("ritchiesprojectilelib.configgui.maxChunksForceLoaded")
					.worldRestart()
					.defineInRange("maxChunksForceLoaded", 64, -1, Integer.MAX_VALUE);

			builder.pop();
		}
	}

	private static final ForgeConfigSpec serverSpec;
	private static final Server SERVER;
	static {
		final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
		serverSpec = specPair.getRight();
		SERVER = specPair.getLeft();
	}
	public static Server server() { return SERVER; }
	public static void registerConfigs(BiConsumer<ModConfig.Type, ForgeConfigSpec> cons) {
		cons.accept(ModConfig.Type.SERVER, serverSpec);
	}

	public static void onModConfigLoad(ModConfig modConfig) {
	}

	public static void onModConfigReload(ModConfig modConfig) {
	}

}
