package rbasamoyai.ritchiesprojectilelib.neoforge;


import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class EnvExecuteImpl {

	public static void executeOnClient(Supplier<Runnable> sup) {
        if (FMLEnvironment.dist.isClient()) {
            sup.get().run();
        }
    }

}
