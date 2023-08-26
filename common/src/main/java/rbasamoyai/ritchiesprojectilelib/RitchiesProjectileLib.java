package rbasamoyai.ritchiesprojectilelib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RitchiesProjectileLib {
    public static final String MOD_ID = "ritchiesprojectilelib";
    public static final String NAME = "Ritchie's Projectile Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
        LOGGER.info("Loaded.");
    }

}
