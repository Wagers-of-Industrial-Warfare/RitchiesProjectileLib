package rbasamoyai.ritchiesprojectilelib.network.neoforge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import rbasamoyai.ritchiesprojectilelib.network.RPLNetwork;

public class RPLCodec implements StreamCodec<FriendlyByteBuf, NeoForgePacket> {
    @Override
    public NeoForgePacket decode(FriendlyByteBuf buf) {
        return new NeoForgePacket(RPLNetwork.constructPacket(buf, buf.readVarInt()));
    }

    @Override
    public void encode(FriendlyByteBuf buf, NeoForgePacket pkt) {
        RPLNetwork.writeToBuf(pkt.pkt(), buf);
    }
}
