package rbasamoyai.ritchiesprojectilelib.network;

import java.util.function.Function;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class RPLNetwork {

	private static final Int2ObjectMap<Function<FriendlyByteBuf, ? extends RootPacket>> ID_TO_CONSTRUCTOR = new Int2ObjectOpenHashMap<>();
	private static final Object2IntMap<Class<? extends RootPacket>> TYPE_TO_ID = new Object2IntOpenHashMap<>();

	public static final String VERSION = "3.0.0";

	public static void init() {
		int id = 0;
		addMsg(id++, ClientboundCheckChannelVersionPacket.class, ClientboundCheckChannelVersionPacket::new);
		addMsg(id++, ClientboundPreciseMotionSyncPacket.class, ClientboundPreciseMotionSyncPacket::new);
		addMsg(id++, ClientboundShakeScreenPacket.class, ClientboundShakeScreenPacket::new);
        addMsg(id++, ClientboundSyncBurstSubProjectilesPacket.class, ClientboundSyncBurstSubProjectilesPacket::decode);

		sidedInit();
	}

	@ExpectPlatform public static void sidedInit() { throw new AssertionError(); }

	private static <T extends RootPacket> void addMsg(int id, Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
		TYPE_TO_ID.put(clazz, id);
		ID_TO_CONSTRUCTOR.put(id, decoder);
	}

	public static RootPacket constructPacket(FriendlyByteBuf buf, int id) {
		if (!ID_TO_CONSTRUCTOR.containsKey(id)) throw new IllegalStateException("Attempted to deserialize packet with illegal id: " + id);
		return ID_TO_CONSTRUCTOR.get(id).apply(buf);
	}

	public static void writeToBuf(RootPacket pkt, FriendlyByteBuf buf) {
		int id = TYPE_TO_ID.getOrDefault(pkt.getClass(), -1);
		if (id == -1) throw new IllegalStateException("Attempted to serialize packet with illegal id: " + id);
		buf.writeVarInt(id);
		pkt.rootEncode(buf);
	}

	public static void onPlayerJoin(ServerPlayer player) {
		sendToClientPlayer(new ClientboundCheckChannelVersionPacket(VERSION), player);
	}

	@ExpectPlatform public static void sendToServer(RootPacket pkt) { throw new AssertionError(); }

	@ExpectPlatform public static void sendToClientPlayer(RootPacket pkt, ServerPlayer player) { throw new AssertionError(); }

	@ExpectPlatform public static void sendToClientTracking(RootPacket pkt, Entity tracked) { throw new AssertionError(); }

	@ExpectPlatform public static void sendToClientAll(RootPacket pkt, MinecraftServer server) { throw new AssertionError(); }

}
