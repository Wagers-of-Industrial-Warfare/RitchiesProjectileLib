package rbasamoyai.ritchiesprojectilelib;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rbasamoyai.ritchiesprojectilelib.chunkloading.ChunkManager;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

public class RitchiesProjectileLib {
    public static final String MOD_ID = "ritchiesprojectilelib";
    public static final String NAME = "Ritchie's Projectile Lib";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    private static final String CHUNK_MANAGER_ID = MOD_ID + ":chunk_manager";

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
     * Remove or queue a chunk for force loading.
     *
     * @param level
     * @param entity
     * @param chunkX
     * @param chunkZ
     * @param load
     */
    public static void queueForceLoad(ServerLevel level, Entity entity, int chunkX, int chunkZ, boolean load) {
        if (!level.hasChunk(chunkX, chunkZ)) return;
        ChunkManager manager = level.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, CHUNK_MANAGER_ID);
        manager.trackForcedChunk(entity, new ChunkPos(chunkX, chunkZ), load);
    }

    /**
     * Mark all chunks loaded by an entity as no longer force loaded. Use when the entity is removed, e.g. killed, change dimension.
     *
     * @param level
     * @param entity
     */
    public static void removeAllForceLoaded(ServerLevel level, Entity entity) {
        ChunkManager manager = level.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, CHUNK_MANAGER_ID);
        manager.clearEntity(entity);
    }

}
