package io.github.null2264.skyblockcreator.skyblock;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.UUID;

public class Island {
    private UUID id;
    private BlockPos position;
    private final HashSet<UUID> players = new HashSet<>();

    public Island() {
    }

    public Island(UUID id, BlockPos pos) {
        this.id = id;
        this.position = pos;
    }

    public static Island fromNbt(NbtCompound nbtCompound, UUID id) {
        Island island = new Island();
        island.id = id;
        island.position = BlockPos.fromLong(nbtCompound.getLong("position"));
        NbtList playersNbt = nbtCompound.getList("players", NbtElement.COMPOUND_TYPE);
        playersNbt.forEach(playerNbt -> {
            UUID playerUuid = ((NbtCompound) playerNbt).getUuid("player");
            island.players.add(playerUuid);
        });
        return island;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("position", position.asLong());
        NbtList playersNbt = new NbtList();
        players.forEach(player -> {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putUuid("player", player);
            playersNbt.add(playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }

    public boolean addPlayer(UUID uuid) {
        return players.add(uuid);
    }

    public UUID getId() {
        return id;
    }

    public BlockPos getPosition() {
        return position;
    }

    public HashSet<UUID> getPlayers() {
        return players;
    }
}