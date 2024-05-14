package rbasamoyai.ritchiesprojectilelib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rbasamoyai.ritchiesprojectilelib.chunkloading.ChunkManager;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

public class RitchiesProjectileLib {
    public static final String MOD_ID = "ritchiesprojectilelib";
    public static final String NAME = "Ritchie's Projectile Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final String CHUNK_MANAGER_ID = MOD_ID + "_chunk_manager";

    public static void init() {
        RPLNetwork.init();
    }

    public static ResourceLocation resource(String path) { return new ResourceLocation(MOD_ID, path); }

    public static void onPlayerJoin(ServerPlayer player) {
        RPLNetwork.onPlayerJoin(player);
    }

    public static void onServerLevelTickEnd(ServerLevel level) {
        ChunkManager manager = level.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, CHUNK_MANAGER_ID);
        manager.tick(level);
    }

    /**
     * Queue a chunk for force loading.
     *
     * @param level
     * @param chunkX
     * @param chunkZ
     */
    public static void queueForceLoad(ServerLevel level, int chunkX, int chunkZ) {
        ChunkManager manager = level.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, CHUNK_MANAGER_ID);
        manager.queueForceLoad(new ChunkPos(chunkX, chunkZ));
    }

}
