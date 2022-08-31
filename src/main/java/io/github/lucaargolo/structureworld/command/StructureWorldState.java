package io.github.lucaargolo.structureworld.command;

import io.github.lucaargolo.structureworld.Mod;
import io.github.lucaargolo.structureworld.error.AlreadyHaveIsland;
import io.github.lucaargolo.structureworld.error.InvalidChunkGenerator;
import io.github.lucaargolo.structureworld.error.NoIslandFound;
import io.github.lucaargolo.structureworld.worldgen.StructureChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.HashMap;
import java.util.UUID;

public class StructureWorldState extends PersistentState {

    private final HashMap<UUID, BlockPos> playerMap = new HashMap<>();
    private int x, y, dx, dy;
    private boolean spawnGenerated;

    public StructureWorldState() {
        this.x = Mod.CONFIG.getPlatformDistanceRadius();
        this.y = 0;
        this.dx = Mod.CONFIG.getPlatformDistanceRadius();
        this.dy = 0;
        this.spawnGenerated = false;
    }

    public static StructureWorldState createFromNbt(NbtCompound tag) {
        StructureWorldState state = new StructureWorldState();
        state.playerMap.clear();
        NbtCompound playerMapTag = tag.getCompound("playerMap");
        playerMapTag.getKeys().forEach(key -> {
            try {
                UUID uuid = UUID.fromString(key);
                BlockPos pos = BlockPos.fromLong(playerMapTag.getLong(key));
                state.playerMap.put(uuid, pos);
            } catch (IllegalArgumentException ignored) {
            }
        });
        state.x = tag.getInt("x");
        state.y = tag.getInt("y");
        state.dx = tag.getInt("dx");
        state.dy = tag.getInt("dy");
        state.spawnGenerated = tag.getBoolean("spawnGenerated");
        return state;
    }

    public static boolean isNotStructureWorld(ServerWorld world) {
        return !(world.getChunkManager().getChunkGenerator() instanceof StructureChunkGenerator);
    }

    public void createIsland(ServerWorld world, ServerPlayerEntity playerEntity) throws InvalidChunkGenerator, AlreadyHaveIsland {
        if (isNotStructureWorld(world)) {
            throw new InvalidChunkGenerator();
        }
        BlockPos islandPos = this.getIsland(playerEntity);
        if (islandPos != null) {
            throw new AlreadyHaveIsland();
        }
        islandPos = this.generateIsland(world, playerEntity);
        playerEntity.teleport(islandPos.getX(), islandPos.getY(), islandPos.getZ());
    }

    public void deleteIsland(ServerPlayerEntity playerEntity) {
        playerMap.remove(playerEntity.getUuid());
    }

    public void deleteIsland(ServerWorld world, ServerPlayerEntity playerEntity) throws InvalidChunkGenerator, NoIslandFound {
        if (isNotStructureWorld(world)) {
            throw new InvalidChunkGenerator();
        }
        BlockPos islandPos = this.getIsland(playerEntity);
        if (islandPos == null) {
            throw new NoIslandFound();
        }
        this.deleteIsland(playerEntity);
        this.teleportToIsland(world, playerEntity, Util.NIL_UUID);
    }

    public BlockPos getSpawnIsland() {
        return getIsland(Util.NIL_UUID);
    }

    public BlockPos getIsland(ServerPlayerEntity playerEntity) {
        return getIsland(playerEntity.getUuid());
    }

    public BlockPos getIsland(UUID id) {
        return playerMap.get(id);
    }

    public BlockPos generateIsland(ServerWorld world) {
        BlockPos islandPos = generateIsland(world, Util.NIL_UUID);
        this.spawnGenerated = true;
        return islandPos;
    }

    public BlockPos generateIsland(ServerWorld world, ServerPlayerEntity playerEntity) {
        return generateIsland(world, playerEntity.getUuid());
    }

    public BlockPos generateIsland(ServerWorld world, UUID uuid) {
        int x = this.x;
        int y = this.y;
        int dx = this.dx;
        int dy = this.dy;
        if (!playerMap.containsKey(uuid)) {
            ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
            if (chunkGenerator instanceof StructureChunkGenerator structureChunkGenerator) {
                StructureTemplate structure = Mod.STRUCTURES.get(structureChunkGenerator.getStructure());
                BlockPos playerSpawnOffset = structureChunkGenerator.getPlayerSpawnOffset();
                BlockPos structureOffset = structureChunkGenerator.getStructureOffset();

                BlockPos origin = new BlockPos(8, 64, 8);
                BlockPos island;

                if (!uuid.equals(Util.NIL_UUID)) {
                    island = origin.add(this.x, 0, this.y);
                    if ((x == y) || (x < 0 && x == -y) || (x > 0 && x == Mod.CONFIG.getPlatformDistanceRadius() - y)) {
                        this.dx = -dy;
                        this.dy = dx;
                    }
                    this.x = x + this.dx;
                    this.y = y + this.dy;
                } else {
                    island = origin;
                }

                if (structure != null && !(uuid.equals(Util.NIL_UUID) && spawnGenerated))
                    structure.place(world, island.add(structureOffset), island.add(structureOffset), new StructurePlacementData(), world.random, Block.NO_REDRAW);
                playerMap.put(uuid, island.add(playerSpawnOffset));
            } else {
                playerMap.put(uuid, BlockPos.ORIGIN);
            }
            markDirty();
        }
        return playerMap.get(uuid);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, player);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, ServerPlayerEntity owner) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, owner.getUuid());
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, UUID uuid) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, uuid, false);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, UUID uuid, Boolean forceTeleport) throws InvalidChunkGenerator, NoIslandFound {
        if (isNotStructureWorld(world) && !uuid.equals(Util.NIL_UUID)) {
            throw new InvalidChunkGenerator();
        }
        BlockPos islandPos = this.getIsland(uuid);
        if (islandPos == null) {
            if (!uuid.equals(Util.NIL_UUID) && !forceTeleport)
                throw new NoIslandFound();
            else {
                BlockPos spawnIsland = this.getSpawnIsland();
                islandPos = spawnIsland != null ? spawnIsland : this.generateIsland(world);
            }
        }

        if (forceTeleport)
            player.setPosition(islandPos.getX(), islandPos.getY(), islandPos.getZ());
        else
            player.teleport(islandPos.getX(), islandPos.getY(), islandPos.getZ());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtCompound playerMapTag = new NbtCompound();
        playerMap.forEach((uuid, blockPos) -> playerMapTag.putLong(uuid.toString(), blockPos.asLong()));
        tag.put("playerMap", playerMapTag);
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("dx", dx);
        tag.putInt("dy", dy);
        tag.putBoolean("spawnGenerated", spawnGenerated);
        return tag;
    }

}