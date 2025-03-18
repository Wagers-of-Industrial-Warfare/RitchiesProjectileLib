package rbasamoyai.ritchiesprojectilelib.effects.screen_shake;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;

public class RPLScreenShakeHandlerClient {

    private static final Map<ResourceLocation, ModScreenShakeHandler> HANDLERS = new Object2ReferenceOpenHashMap<>();

    public static final ResourceLocation DEFAULT_HANDLER_ID = RitchiesProjectileLib.resource("shake_handler");
    public static final ModScreenShakeHandler.Impl DEFAULT_HANDLER = registerModScreenShakeHandler(DEFAULT_HANDLER_ID, new ModScreenShakeHandler.Impl());

    public static <T extends ModScreenShakeHandler> T registerModScreenShakeHandler(ResourceLocation id, T handler) {
        if (HANDLERS.containsKey(id))
            throw new IllegalStateException("Ritchie's Projectile Library screen shake handler already has registered handler entry '" + id + "'");
        HANDLERS.put(id, handler);
        return handler;
    }

    public static void tick(Minecraft minecraft) {
        if (minecraft.isPaused())
            return;
        for (ModScreenShakeHandler handler : HANDLERS.values()) {
            handler.tick(minecraft);
        }
    }

    public static ScreenShakeContext getScreenShake(float partialTicks) {
        ScreenShakeContext context = new ScreenShakeContext(partialTicks);
        for (ModScreenShakeHandler handler : HANDLERS.values())
            handler.applyEffects(context);
        return context;
    }

    /**
     * Shake the screen of the <b>client</b>. For shaking the screen from the server side, use {@link
     * RitchiesProjectileLib#shakePlayerScreen(ServerPlayer, ScreenShakeEffect)}.
     * <p>This method uses the <b>default</b> mod screen shake handler.
     *
     * @param effect the screen shake effect
     */
    public static void addShakeEffect(ScreenShakeEffect effect) {
        DEFAULT_HANDLER.addEffect(effect);
    }

    /**
     * Shake the screen of the <b>client</b>. For shaking the screen from the server side, use {@link
     * RitchiesProjectileLib#shakePlayerScreen(ServerPlayer, ScreenShakeEffect)}.
     * <p>If {@code modHandlerId} is invalid, the screen shake handler will not shake the screen. This <b>does not
     * default</b> to the default handler.
     *
     * @param modHandlerId the id of the mod screen shake handler to handle the effect
     * @param effect the screen shake effect
     */
    public static void addShakeEffect(ResourceLocation modHandlerId, ScreenShakeEffect effect) {
        if (!HANDLERS.containsKey(modHandlerId))
            return;
        HANDLERS.get(modHandlerId).addEffect(effect);
    }

    public static void onPlayerLogOut(Player player) {
        for (ModScreenShakeHandler handler : HANDLERS.values()) {
            handler.clearEffects();
        }
    }

}
