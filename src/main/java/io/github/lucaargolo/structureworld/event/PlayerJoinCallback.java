package io.github.lucaargolo.structureworld.event;

import io.github.lucaargolo.structureworld.IEntityPersistentData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import static io.github.lucaargolo.structureworld.Mod.MOD_ID;

public class PlayerJoinCallback {
    private static String SPAWN_TAG = MOD_ID + ":alreadySpawned";

    public static void onPlayerJoin(PlayerEntity player, ServerWorld world) {
        IEntityPersistentData playerData = (IEntityPersistentData) player;
        if (playerData.getPersistentData().getBoolean(SPAWN_TAG)) {
            return;
        }

        BlockPos spawnPos = world.getSpawnPos();

        player.teleport(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

        playerData.getPersistentData().putBoolean(SPAWN_TAG, true);
    }
}