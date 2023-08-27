package rbasamoyai.ritchiesprojectilelib;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Supplier;

public class EnvExecute {

	@ExpectPlatform public static void executeOnClient(Supplier<Runnable> sup) { throw new AssertionError(); }

}
