package rbasamoyai.ritchiesprojectilelib.network;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import rbasamoyai.ritchiesprojectilelib.EnvExecute;
import rbasamoyai.ritchiesprojectilelib.effects.screen_shake.ScreenShakeEffect;

public record ClientboundShakeScreenPacket(@Nullable ResourceLocation modHandlerId, ScreenShakeEffect effect) implements RootPacket {

    public ClientboundShakeScreenPacket(FriendlyByteBuf buf) {
        this(buf.readBoolean() ? buf.readResourceLocation() : null, new ScreenShakeEffect(buf.readVarInt(), buf.readFloat(),
            buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));
    }

    public ClientboundShakeScreenPacket(ScreenShakeEffect effect) {
        this(null, effect);
    }

    @Override
    public void rootEncode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.modHandlerId != null);
        if (this.modHandlerId != null)
            buf.writeResourceLocation(this.modHandlerId);
        buf.writeVarInt(this.effect.duration)
            .writeFloat(this.effect.yawMagnitude)
            .writeFloat(this.effect.pitchMagnitude)
            .writeFloat(this.effect.rollMagnitude)
            .writeFloat(this.effect.yawJitter)
            .writeFloat(this.effect.pitchJitter)
            .writeFloat(this.effect.rollJitter);
    }

    @Override
    public void handle(Executor exec, PacketListener listener, ServerPlayer sender) {
        EnvExecute.executeOnClient(() -> () -> RPLClientHandlers.shakeScreen(this));
    }

}
