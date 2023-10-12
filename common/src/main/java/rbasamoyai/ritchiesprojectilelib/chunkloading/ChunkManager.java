package rbasamoyai.ritchiesprojectilelib.chunkloading;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.saveddata.SavedData;
import rbasamoyai.ritchiesprojectilelib.config.RPLConfigs;

import java.util.*;

public class ChunkManager extends SavedData {

	private final SetMultimap<UUID, Long> chunks;
	private final LinkedList<Long> iterated = new LinkedList<>();
	private final Set<Long> currentlyLoaded = new HashSet<>();

	public ChunkManager() {
		this(HashMultimap.create());
	}

	private ChunkManager(SetMultimap<UUID, Long> map) {
		this.chunks = map;
		this.iterated.addAll(this.chunks.values());
	}

	public static ChunkManager load(CompoundTag tag) {
		SetMultimap<UUID, Long> chunks = HashMultimap.create();
		ListTag loadedList = tag.getList("LoadedChunks", Tag.TAG_COMPOUND);
		for (int i = 0; i < loadedList.size(); ++i) {
			CompoundTag eTag = loadedList.getCompound(i);
			chunks.put(eTag.getUUID("UUID"), eTag.getLong("ChunkPos"));
		}
		return new ChunkManager(chunks);
	}

	@Override
	public CompoundTag save(CompoundTag compoundTag) {
		ListTag loadedList = new ListTag();
		for (Map.Entry<UUID, Long> e : this.chunks.entries()) {
			CompoundTag eTag = new CompoundTag();
			eTag.putUUID("UUID", e.getKey());
			eTag.putLong("ChunkPos", e.getValue());
			loadedList.add(eTag);
		}
		compoundTag.put("LoadedChunks", loadedList);
		return compoundTag;
	}

	public void trackForcedChunk(Entity entity, ChunkPos pos, boolean loaded) {
		long l = pos.toLong();
		UUID uuid = entity.getUUID();
		if (loaded && !entity.isRemoved() && !this.chunks.containsEntry(uuid, l)) {
			if (!this.chunks.containsValue(l)) this.iterated.add(l);
			this.chunks.put(uuid, l);
			this.setDirty();
		} else if (!loaded && this.chunks.containsEntry(uuid, l)) {
			this.chunks.remove(uuid, l);
			this.setDirty();
		}
	}

	public void clearEntity(Entity entity) {
		this.chunks.removeAll(entity.getUUID());
		this.setDirty();
	}

	public void expireChunkIfNecessary(ServerLevel level, ChunkPos cpos) {
		long l = cpos.toLong();
		if (level.getForcedChunks().contains(l)) return;
		if (!this.chunks.containsValue(l)
			|| this.iterated.size() > this.currentlyLoaded.size() && this.currentlyLoaded.size() >= RPLConfigs.server().maxChunksForceLoaded.get()) {
			this.currentlyLoaded.remove(l);
			level.getChunkSource().updateChunkForced(cpos, false);
		}
	}

	public void tick(ServerLevel level) {
		LongSet vanillaForcedChunks = level.getForcedChunks();
		Set<Long> badChunks = new HashSet<>();

		int MAX_SIZE = RPLConfigs.server().maxChunksForceLoaded.get();
		int MAX_ITER = 64;
		if (MAX_SIZE != 0) {
			int p = 0;
			int q = 0;
			LinkedList<Long> tempBuf = new LinkedList<>();
			while ((MAX_SIZE == -1 || p < MAX_SIZE) && !this.iterated.isEmpty() && (MAX_SIZE == -1 || this.currentlyLoaded.size() < MAX_SIZE) && q++ < MAX_ITER) {
				long l = this.iterated.poll();
				if (!this.chunks.containsValue(l)) continue;
				if (!vanillaForcedChunks.contains(l) && !this.currentlyLoaded.contains(l)) {
					if (!loadChunkNoGenerate(level, new ChunkPos(l))) {
						badChunks.add(l);
						continue;
					}
					this.currentlyLoaded.add(l);
				}
				tempBuf.add(l);
				++p;
			}
			this.iterated.addAll(tempBuf);
		}

		for (UUID uuid : this.chunks.keySet()) {
			for (long l : badChunks) {
				this.chunks.remove(uuid, l);
			}
		}

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

}
