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
	private final LongOpenHashSet inQueue;
	private final Long2IntOpenHashMap loaded = new Long2IntOpenHashMap();

	public ChunkManager() { this(new LongOpenHashSet()); }

    public ChunkManager(LongOpenHashSet chunks) {
        this.chunks = chunks;
		this.inQueue = new LongOpenHashSet(this.chunks);
		for (long packedPos : this.chunks)
			this.queue.enqueue(packedPos);
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

	public void tick(ServerLevel level) {
		LongSet vanillaForcedChunks = level.getForcedChunks();
		int MAX_SIZE = RPLConfigs.server().maxChunksForceLoaded.get();
		int MAX_CHUNKS_PROCESSED = RPLConfigs.server().maxChunksLoadedEachTick.get();
		int DEFAULT_AGE = defaultChunkAge();

		LongOpenHashSet expired = new LongOpenHashSet();
		for (Map.Entry<Long, Integer> entry : this.loaded.long2IntEntrySet()) {
			long packedPos = entry.getKey();
			int newAge = entry.getValue() - 1;
			entry.setValue(newAge);
			if (newAge <= 0)
				expired.add(packedPos);
		}

		int freeSlots = Math.max(0, MAX_SIZE - this.loaded.size());
		int pollCount = Math.min(MAX_CHUNKS_PROCESSED, freeSlots + expired.size());
		pollCount = Math.min(pollCount, this.queue.size());
		for (int i = 0; i < pollCount; ++i) {
			if (this.queue.isEmpty())
				break;
			long packedPos = this.queue.dequeueLong();
			this.inQueue.remove(packedPos);
			ChunkPos chunkPos = new ChunkPos(packedPos);
			if (this.loaded.containsKey(packedPos) && this.loaded.get(packedPos) > -1) {
				this.loaded.put(packedPos, DEFAULT_AGE);
				expired.remove(packedPos);
			} else if (!vanillaForcedChunks.contains(packedPos) && loadChunkNoGenerate(level, chunkPos)) {
				this.loaded.put(packedPos, DEFAULT_AGE);
				level.getChunkSource().updateChunkForced(chunkPos, true);
			} else {
				this.chunks.remove(packedPos);
			}
		}
		for (long packedPos : expired) {
			level.getChunkSource().updateChunkForced(new ChunkPos(packedPos), false);
			this.loaded.remove(packedPos);
			if (!this.inQueue.contains(packedPos))
				this.chunks.remove(packedPos);
		}
		this.loaded.trim();
		this.inQueue.trim();
		this.queue.trim();
		this.chunks.trim();
		this.setDirty();
	}

	// Largely modeled after CraftBukkit World#loadChunk

	private static boolean loadChunkNoGenerate(ServerLevel level, ChunkPos cpos) {
		ServerChunkCache source = level.getChunkSource();
		ChunkAccess immediate = source.getChunkNow(cpos.x, cpos.z);
		if (immediate != null)
			return true;
		ChunkAccess access = source.getChunk(cpos.x, cpos.z, ChunkStatus.EMPTY, true);
		if (access instanceof ProtoChunk) {
			source.removeRegionTicket(TicketType.UNKNOWN, cpos, -11, cpos);
			access = source.getChunk(cpos.x, cpos.z, ChunkStatus.FULL, true);
		}
        return access instanceof LevelChunk;
    }

	private static int defaultChunkAge() {
		return RPLConfigs.server().projectileChunkAge.get();
	}

}
