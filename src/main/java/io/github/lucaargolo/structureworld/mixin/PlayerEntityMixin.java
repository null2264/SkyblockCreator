package io.github.lucaargolo.structureworld.mixin;

import io.github.lucaargolo.structureworld.StructureChunkGenerator;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "moveToSpawn", at = @At("TAIL"))
    private void doSpawn(ServerWorld world, CallbackInfo ci) {
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        if (!(chunkGenerator instanceof StructureChunkGenerator)) {
            return;
        }

        BlockPos spawnPos = world.getSpawnPos();

        ((ServerPlayerEntity)(Object)this).setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
    }
}