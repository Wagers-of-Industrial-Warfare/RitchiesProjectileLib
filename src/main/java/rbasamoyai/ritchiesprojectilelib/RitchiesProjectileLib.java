package rbasamoyai.ritchiesprojectilelib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import rbasamoyai.ritchiesprojectilelib.chunkloading.ChunkManager;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect;
import rbasamoyai.ritchiesprojectilelib.network.ClientboundShakeScreenPacket;
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
     * Queue a chunk for temporary force loading.
     *
     * @param level the level which to force load the chunk
     * @param chunkX the X component of the chunk coordinate
     * @param chunkZ the Z component of the chunk coordinate
     */
    public static void queueForceLoad(ServerLevel level, int chunkX, int chunkZ) {
        ChunkManager manager = level.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, CHUNK_MANAGER_ID);
        manager.queueForceLoad(new ChunkPos(chunkX, chunkZ));
    }

    /**
     * Shake the screen of a player from the <b>server side</b>. For client side shake effects, use {@link
     * rbasamoyai.ritchiesprojectilelib.effects.screen_shake.RPLScreenShakeHandlerClient#addShakeEffect(ScreenShakeEffect)}
     * or {@link rbasamoyai.ritchiesprojectilelib.effects.screen_shake.RPLScreenShakeHandlerClient#addShakeEffect(ResourceLocation, ScreenShakeEffect)}.
     * <p>If {@code modHandlerId} is invalid on client side, the screen shake handler will not shake the screen. This
     * <b>does not default</b> to the default handler.
     *
     * @param player the server side player targeted by the effect
     * @param modHandlerId the id of the mod screen shake handler to handle the effect
     * @param effect the screen shake effect
     */
    public static void shakePlayerScreen(ServerPlayer player, ResourceLocation modHandlerId, ScreenShakeEffect effect) {
        RPLNetwork.sendToClientPlayer(new ClientboundShakeScreenPacket(modHandlerId, effect), player);
    }

    /**
     * Shake the screen of a player from the <b>server side</b>. For client side shake effects, use {@link
     * rbasamoyai.ritchiesprojectilelib.effects.screen_shake.RPLScreenShakeHandlerClient#addShakeEffect(ScreenShakeEffect)}
     * or {@link rbasamoyai.ritchiesprojectilelib.effects.screen_shake.RPLScreenShakeHandlerClient#addShakeEffect(ResourceLocation, ScreenShakeEffect)}.
     * <p>This is equivalent to {@code shakePlayerScreen(ServerPlayer, new ResourceLocation("ritchiesprojectilelib:shake_handler"), ScreenShakeEffect)},
     * guaranteeing that the default RPL mod screen shake handler applies the screen shake effect.
     *
     * @param player the server side player targeted by the effect
     * @param effect the screen shake effect
     */
    public static void shakePlayerScreen(ServerPlayer player, ScreenShakeEffect effect) {
        RPLNetwork.sendToClientPlayer(new ClientboundShakeScreenPacket(effect), player);
    }

}
