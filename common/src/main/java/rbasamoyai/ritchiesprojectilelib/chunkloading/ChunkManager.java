package rbasamoyai.ritchiesprojectilelib.chunkloading;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

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
		if (loaded && !this.chunks.containsEntry(uuid, l)) {
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
	}

	public void tick(ServerLevel level) {
		Set<UUID> badEntities = new HashSet<>();
		Set<Long> badChunks = new HashSet<>();

		for (long l : this.loadedPreviously) {
			ChunkPos cpos = new ChunkPos(l);
			if (level.hasChunk(cpos.x, cpos.z)) {
				level.setChunkForced(cpos.x, cpos.z, false);
			}
		}
		this.loadedPreviously.clear();

		int MAX_ITER = 64; // TODO: config
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
				UUID uuid = e.getKey();
				if (badEntities.contains(uuid)) continue;
				if (level.getEntity(uuid) == null) {
					badEntities.add(uuid);
					continue;
				}
				ChunkPos cpos = new ChunkPos(l);
				if (badChunks.contains(l)) continue;
				if (!level.hasChunk(cpos.x, cpos.z)) {
					badChunks.add(l);
					continue;
				}
				level.setChunkForced(cpos.x, cpos.z, true);
				this.loadedPreviously.add(l);
				if (MAX_ITER != -1 && ++p == MAX_ITER) break;
			}
			if (iteratedAll) this.iterated.clear();
		}
		for (UUID uuid : badEntities) {
			this.chunks.removeAll(uuid);
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
			ChunkPos cpos = new ChunkPos(l);
			if (level.hasChunk(cpos.x, cpos.z)) {
				level.setChunkForced(cpos.x, cpos.z, false);
			}
		}
		this.toUnload.removeAll(unloadCopy);
	}

}
