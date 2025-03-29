package rbasamoyai.ritchiesprojectilelib.network.neoforge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.jetbrains.annotations.NotNull;

import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;
import rbasamoyai.ritchiesprojectilelib.network.RootPacket;

import static rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib.resource;

public record NeoForgePacket(RootPacket pkt) implements CustomPacketPayload {
    public static final Type<NeoForgePacket> TYPE = new Type<>(resource("NeoForgePacket"));

    public static final StreamCodec<FriendlyByteBuf, NeoForgePacket> STREAM_CODEC = new RPLCodec();

    public static void handle(final NeoForgePacket pkt, final IPayloadContext context) {
        pkt.pkt().handle();
    }

    @NotNull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
