package io.github.lucaargolo.structureworld.mixin;

import io.github.lucaargolo.structureworld.command.StructureWorldState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "moveToSpawn", at = @At("TAIL"))
    private void doSpawn(ServerWorld world, CallbackInfo ci) {
        if (StructureWorldState.isNotStructureWorld(world)) {
            return;
        }

        StructureWorldState structureWorldState = world.getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
        try {
            structureWorldState.teleportToIsland(world, (ServerPlayerEntity) (Object) this, Util.NIL_UUID, true);
        } catch (Exception ignored) {
        }
    }
}