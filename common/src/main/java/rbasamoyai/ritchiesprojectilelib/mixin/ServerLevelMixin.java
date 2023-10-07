package rbasamoyai.ritchiesprojectilelib.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rbasamoyai.ritchiesprojectilelib.RitchiesProjectileLib;
import rbasamoyai.ritchiesprojectilelib.chunkloading.ChunkManager;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

	@Unique private final ServerLevel self = (ServerLevel) (Object) this;

	@Inject(method = "tickChunk", at = @At("TAIL"))
	private void ritchiesprojectilelib$tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
		ChunkManager manager = this.self.getDataStorage().computeIfAbsent(ChunkManager::load, ChunkManager::new, RitchiesProjectileLib.CHUNK_MANAGER_ID);
		manager.expireChunkIfNecessary(this.self, chunk.getPos());
	}

}
