package rbasamoyai.ritchiesprojectilelib.chunkloading;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.saveddata.SavedData;
import rbasamoyai.ritchiesprojectilelib.config.RPLConfigs;

import java.util.Map;

public class ChunkManager extends SavedData {

	private final LongOpenHashSet chunks;
	private final LongArrayFIFOQueue queue = new LongArrayFIFOQueue();
	private final LongOpenHashSet inQueue = new LongOpenHashSet();
	private final Long2IntOpenHashMap loaded = new Long2IntOpenHashMap();

	public ChunkManager() { this(new LongOpenHashSet()); }

    public ChunkManager(LongOpenHashSet chunks) {
        this.chunks = chunks;
    }

    public static ChunkManager load(CompoundTag tag) {
		long[] arr = tag.getLongArray("LoadedChunks");
		LongOpenHashSet chunks = new LongOpenHashSet(arr);
		return new ChunkManager(chunks);
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		compoundTag.putLongArray("LoadedChunks", this.inQueue.toLongArray());
		return compoundTag;
	}

	/**
	 * Use {@link rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib#queueForceLoad(ServerLevel, int, int)}.
	 */
	@Deprecated
	public void queueForceLoad(ChunkPos pos) {
		long packedPos = pos.toLong();
		if (this.inQueue.add(packedPos)) {
			this.queue.enqueue(packedPos);
			this.chunks.add(packedPos);
			this.setDirty();
		}
	}

	/**
	 * Internal use only.
	 *
	 * @param level
	 * @param cpos
	 */
	@Deprecated
	public void expireChunkIfNecessary(ServerLevel level, ChunkPos cpos) {
		long packedPos = cpos.toLong();
		if (!this.loaded.containsKey(packedPos) || level.getForcedChunks().contains(packedPos))
			return;
		int tick = this.loaded.get(packedPos);
		tick = tick <= -1 ? defaultChunkAge() : tick - 1;
		if (tick == 0)
			level.getChunkSource().updateChunkForced(cpos, false);
		this.loaded.put(packedPos, tick);
	}

	public void tick(ServerLevel level) {
		LongSet vanillaForcedChunks = level.getForcedChunks();
		int MAX_SIZE = RPLConfigs.server().maxChunksForceLoaded.get();
		int MAX_CHUNKS_PROCESSED = 32; // TODO: Config?
		int DEFAULT_AGE = defaultChunkAge();

		LongOpenHashSet expired = new LongOpenHashSet();
		for (Map.Entry<Long, Integer> entry : this.loaded.long2IntEntrySet()) {
			if (entry.getValue() == 0)
				expired.add(entry.getKey().longValue());
		}
		int freeSlots = Math.max(0, MAX_SIZE - this.loaded.size());
		int pollCount = Math.min(MAX_CHUNKS_PROCESSED, freeSlots + expired.size());
		for (int i = 0; i < pollCount; ++i) {
			long packedPos = this.queue.dequeueLong();
			this.inQueue.remove(packedPos);
			if (this.loaded.containsKey(packedPos) && this.loaded.get(packedPos) > -1) {
				this.loaded.put(packedPos, DEFAULT_AGE);
				expired.remove(packedPos);
			} else if (!vanillaForcedChunks.contains(packedPos) && loadChunkNoGenerate(level, new ChunkPos(packedPos))) {
				this.loaded.put(packedPos, -1);
			} else {
				this.chunks.remove(packedPos);
			}
		}
		this.queue.trim();

		for (long packedPos : expired) {
			if (!this.inQueue.contains(packedPos))
				this.chunks.remove(packedPos);
		}
		this.chunks.trim();

		this.setDirty();
	}

	// Largely modeled after CraftBukkit World#loadChunk

	private static boolean loadChunkNoGenerate(ServerLevel level, ChunkPos cpos) {
		ServerChunkCache source = level.getChunkSource();
		ChunkAccess access = source.getChunk(cpos.x, cpos.z, ChunkStatus.EMPTY, true);
		if (access instanceof ProtoChunk) {
			source.removeRegionTicket(TicketType.UNKNOWN, cpos, -11, cpos);
			access = source.getChunk(cpos.x, cpos.z, ChunkStatus.FULL, true);
		}
		if (access instanceof LevelChunk) {
			source.updateChunkForced(cpos, true);
			return true;
		}
		return false;
	}

	private static int defaultChunkAge() {
		return 3;
	}

}
