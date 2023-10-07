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
	private final Set<Long> toUnload;
	private final Set<Long> iterated = new HashSet<>();
	private final Set<Long> loadedPreviously = new HashSet<>();

	public ChunkManager() {
		this(HashMultimap.create(), new HashSet<>());
	}

	private ChunkManager(SetMultimap<UUID, Long> map, Set<Long> toUnload) {
		this.chunks = map;
		this.toUnload = toUnload;
	}

	public static ChunkManager load(CompoundTag tag) {
		SetMultimap<UUID, Long> chunks = HashMultimap.create();
		ListTag loadedList = tag.getList("LoadedChunks", Tag.TAG_COMPOUND);
		for (int i = 0; i < loadedList.size(); ++i) {
			CompoundTag eTag = loadedList.getCompound(i);
			chunks.put(eTag.getUUID("UUID"), eTag.getLong("ChunkPos"));
		}
		Set<Long> toUnload = new HashSet<>();
		for (long l : tag.getLongArray("ToUnload")) {
			toUnload.add(l);
		}
		return new ChunkManager(chunks, toUnload);
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
		compoundTag.putLongArray("ToUnload", new ArrayList<>(this.toUnload));
		return compoundTag;
	}

	public void trackForcedChunk(Entity entity, ChunkPos pos, boolean loaded) {
		long l = pos.toLong();
		UUID uuid = entity.getUUID();
		if (loaded && !entity.isRemoved() && !this.chunks.containsEntry(uuid, l)) {
			this.chunks.put(uuid, l);
			this.setDirty();
		} else if (!loaded && this.chunks.containsEntry(uuid, l)) {
			this.chunks.remove(uuid, l);
			this.toUnload.add(l);
			this.setDirty();
		}
	}

	public void clearEntity(Entity entity) {
		this.toUnload.addAll(this.chunks.get(entity.getUUID()));
		this.chunks.removeAll(entity.getUUID());
		this.setDirty();
	}

	public void tick(ServerLevel level) {
		Set<Long> badChunks = new HashSet<>();
		ServerChunkCache source = level.getChunkSource();
		LongSet vanillaForcedChunks = level.getForcedChunks();

		for (long l : this.loadedPreviously) {
			if (!vanillaForcedChunks.contains(l)) {
				source.updateChunkForced(new ChunkPos(l), false);
			}
		}
		this.loadedPreviously.clear();

		int MAX_ITER = RPLConfigs.server().maxChunksForceLoaded.get();
		if (MAX_ITER != 0) {
			int p = 0;
			boolean iteratedAll = true;
			for (Map.Entry<UUID, Long> e : this.chunks.entries()) {
				long l = e.getValue();
				if (MAX_ITER != -1) {
					if (this.iterated.contains(l)) continue;
					this.iterated.add(l);
					iteratedAll = false;
				}
				if (vanillaForcedChunks.contains(l) || badChunks.contains(l)) continue;
				if (!loadChunkNoGenerate(level, new ChunkPos(l))) {
					badChunks.add(l);
					continue;
				}
				this.loadedPreviously.add(l);
				if (MAX_ITER != -1 && ++p == MAX_ITER) break;
			}
			if (iteratedAll) this.iterated.clear();
		}
		Set<UUID> keys = new HashSet<>(this.chunks.keySet());
		Set<Long> unloadCopy = new HashSet<>(this.toUnload);
		for (UUID uuid : keys) {
			unloadCopy.removeAll(this.chunks.get(uuid));
			for (long l : badChunks) {
				this.chunks.remove(uuid, l);
			}
		}
		for (long l : unloadCopy) {
			if (!vanillaForcedChunks.contains(l)) {
				source.updateChunkForced(new ChunkPos(l), false);
			}
		}
		this.toUnload.removeAll(unloadCopy);
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
