package io.github.null2264.skyblockcreator.skyblock;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.error.AlreadyHaveIsland;
import io.github.null2264.skyblockcreator.error.InvalidChunkGenerator;
import io.github.null2264.skyblockcreator.error.NoIslandFound;
import io.github.null2264.skyblockcreator.worldgen.StructureChunkGenerator;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import java.util.HashMap;
import java.util.UUID;

public class SkyblockWorldState extends PersistentState {

    private final HashMap<UUID, Island> islandMap = new HashMap<>();
    private int x, y, dx, dy;
    private boolean spawnGenerated;

    public SkyblockWorldState() {
        this.x = Mod.CONFIG.getPlatformDistanceRadius();
        this.y = 0;
        this.dx = Mod.CONFIG.getPlatformDistanceRadius();
        this.dy = 0;
        this.spawnGenerated = false;
    }

    public static SkyblockWorldState createFromNbt(NbtCompound tag) {
        SkyblockWorldState state = new SkyblockWorldState();
        state.islandMap.clear();
        NbtCompound islandsNbt = tag.getCompound("islands");
        NbtCompound playerMapTag = tag.getCompound("playerMap");
        if (!playerMapTag.isEmpty() && islandsNbt.isEmpty()) {
            // Backwards compat
            playerMapTag.getKeys().forEach(key -> {
                try {
                    UUID uuid = UUID.fromString(key);
                    BlockPos pos = BlockPos.fromLong(playerMapTag.getLong(key));
                    state.islandMap.put(uuid, new Island(uuid, pos));
                } catch (IllegalArgumentException ignored) {
                }
            });
        }
        islandsNbt.getKeys().forEach(key -> {
            try {
                UUID uuid = UUID.fromString(key);
                state.islandMap.put(uuid, Island.fromNbt(islandsNbt.getCompound(key), uuid));
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
        Island island = this.getIsland(playerEntity);
        if (island != null) {
            throw new AlreadyHaveIsland();
        }
        island = this.generateIsland(world, playerEntity);
        BlockPos islandPos = island.getPosition();
        playerEntity.teleport(islandPos.getX(), islandPos.getY(), islandPos.getZ());
    }

    public void deleteIsland(ServerPlayerEntity playerEntity) {
        islandMap.remove(playerEntity.getUuid());
    }

    public void deleteIsland(ServerWorld world, ServerPlayerEntity playerEntity) throws InvalidChunkGenerator, NoIslandFound {
        if (isNotStructureWorld(world)) {
            throw new InvalidChunkGenerator();
        }
        Island island = this.getIsland(playerEntity);
        if (island == null) {
            throw new NoIslandFound();
        }
        this.deleteIsland(playerEntity);
        this.teleportToIsland(world, playerEntity, Util.NIL_UUID);
    }

    public Island getSpawnIslandOrGenerate(ServerWorld world) {
        Island island = getIsland(Util.NIL_UUID);
        return island != null ? island : generateSpawnIsland(world);
    }

    public Island getIsland(ServerPlayerEntity playerEntity) {
        return getIsland(playerEntity.getUuid());
    }

    public Island getIsland(UUID id) {
        return islandMap.get(id);
    }

    public Island generateSpawnIsland(ServerWorld world) {
        return generateIsland(world, Util.NIL_UUID);
    }

    public Island generateIsland(ServerWorld world, ServerPlayerEntity playerEntity) {
        return generateIsland(world, playerEntity.getUuid());
    }

    public Island generateIsland(ServerWorld world, UUID islandId) {
        int x = this.x;
        int y = this.y;
        int dx = this.dx;
        int dy = this.dy;
        if (!islandMap.containsKey(islandId)) {
            ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
            if (chunkGenerator instanceof StructureChunkGenerator structureChunkGenerator) {
                Structure structure = Mod.STRUCTURES.get(structureChunkGenerator.getStructure());
                BlockPos playerSpawnOffset = structureChunkGenerator.getPlayerSpawnOffset();
                BlockPos structureOffset = structureChunkGenerator.getStructureOffset();

                BlockPos origin = new BlockPos(8, 64, 8);
                BlockPos islandPos;

                if (!islandId.equals(Util.NIL_UUID)) {
                    islandPos = origin.add(this.x, 0, this.y);
                    if ((x == y) || (x < 0 && x == -y) || (x > 0 && x == Mod.CONFIG.getPlatformDistanceRadius() - y)) {
                        this.dx = -dy;
                        this.dy = dx;
                    }
                    this.x = x + this.dx;
                    this.y = y + this.dy;
                } else {
                    islandPos = origin;
                }

                if (structure != null)
                    structure.place(world, islandPos.add(structureOffset), islandPos.add(structureOffset), new StructurePlacementData(), world.random, Block.NO_REDRAW);

                Island newIsland = new Island(islandId, islandPos.add(playerSpawnOffset));
                islandMap.put(islandId, newIsland);

            } else {
                islandMap.put(islandId, new Island(islandId, BlockPos.ORIGIN));
            }
            markDirty();
        }
        return islandMap.get(islandId);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, player, false);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, ServerPlayerEntity owner) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, owner, false);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, ServerPlayerEntity owner, Boolean forceTeleport) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, owner.getUuid(), forceTeleport);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, UUID uuid) throws InvalidChunkGenerator, NoIslandFound {
        teleportToIsland(world, player, uuid, false);
    }

    public void teleportToIsland(ServerWorld world, ServerPlayerEntity player, UUID uuid, Boolean forceTeleport) throws InvalidChunkGenerator, NoIslandFound {
        if (isNotStructureWorld(world) && !uuid.equals(Util.NIL_UUID)) {
            throw new InvalidChunkGenerator();
        }

        Island island = this.getIsland(uuid);
        if (island == null) {
            if (!uuid.equals(Util.NIL_UUID) && !forceTeleport)
                throw new NoIslandFound();
            else {
                island = this.getSpawnIslandOrGenerate(world);
            }
        }

        BlockPos islandPos = island.getPosition();
        player.requestTeleport(islandPos.getX(), islandPos.getY(), islandPos.getZ());
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtCompound islandsNbt = new NbtCompound();
        islandMap.forEach((uuid, island) -> islandsNbt.put(uuid.toString(), island.toNbt()));
        tag.put("islands", islandsNbt);
        tag.putInt("x", x);
        tag.putInt("y", y);
        tag.putInt("dx", dx);
        tag.putInt("dy", dy);
        tag.putBoolean("spawnGenerated", spawnGenerated);
        return tag;
    }

}