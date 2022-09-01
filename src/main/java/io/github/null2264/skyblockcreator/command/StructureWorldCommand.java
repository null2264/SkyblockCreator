package io.github.null2264.skyblockcreator.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.error.AlreadyHaveIsland;
import io.github.null2264.skyblockcreator.error.InvalidChunkGenerator;
import io.github.null2264.skyblockcreator.error.NoIslandFound;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

public class StructureWorldCommand {
    // TODO: Add team support

    private static final SimpleCommandExceptionType INVALID_CHUNK_GENERATOR = new SimpleCommandExceptionType(new TranslatableText("commands.skyblockcreator.invalid_chunk_generator"));
    private static final SimpleCommandExceptionType ISLAND_FOR_UUID_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslatableText("commands.skyblockcreator.island_for_uuid_already_exists"));
    private static final SimpleCommandExceptionType NO_ISLAND_FOR_UUID = new SimpleCommandExceptionType(new TranslatableText("commands.skyblockcreator.no_island_for_uuid"));

    private static final LiteralArgumentBuilder<ServerCommandSource> create = CommandManager
            .literal("create")
            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(Mod.CONFIG.getCreatePlatformPermissionLevel()))
            .executes(context -> {
                ServerPlayerEntity playerEntity = context.getSource().getPlayer();
                StructureWorldState structureWorldState = context.getSource().getWorld().getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
                try {
                    structureWorldState.createIsland(context.getSource().getWorld(), playerEntity);
                } catch (InvalidChunkGenerator e) {
                    throw INVALID_CHUNK_GENERATOR.create();
                } catch (AlreadyHaveIsland e) {
                    throw ISLAND_FOR_UUID_ALREADY_EXISTS.create();
                }
                context.getSource().sendFeedback(new TranslatableText("commands.skyblockcreator.created_island", playerEntity.getDisplayName()), false);
                return 1;
            })
            .then(CommandManager.argument("player", EntityArgumentType.player())
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .executes(context -> {
                        ServerPlayerEntity playerEntity = EntityArgumentType.getPlayer(context, "player");
                        StructureWorldState structureWorldState = context.getSource().getWorld().getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
                        try {
                            structureWorldState.createIsland(context.getSource().getWorld(), playerEntity);
                        } catch (InvalidChunkGenerator e) {
                            throw INVALID_CHUNK_GENERATOR.create();
                        } catch (AlreadyHaveIsland e) {
                            throw ISLAND_FOR_UUID_ALREADY_EXISTS.create();
                        }
                        context.getSource().sendFeedback(new TranslatableText("commands.skyblockcreator.created_island", playerEntity.getDisplayName()), true);
                        return 1;
                    })
            );

    private static final LiteralArgumentBuilder<ServerCommandSource> delete = CommandManager
            .literal("delete")
            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(Mod.CONFIG.getCreatePlatformPermissionLevel()))
            .executes(context -> {
                ServerPlayerEntity playerEntity = context.getSource().getPlayer();
                StructureWorldState structureWorldState = context.getSource().getWorld().getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
                try {
                    structureWorldState.deleteIsland(context.getSource().getWorld(), playerEntity);
                } catch (InvalidChunkGenerator e) {
                    throw INVALID_CHUNK_GENERATOR.create();
                } catch (NoIslandFound e) {
                    throw NO_ISLAND_FOR_UUID.create();
                }
                context.getSource().sendFeedback(new TranslatableText("commands.skyblockcreator.deleted_island", playerEntity.getDisplayName()), false);
                return 1;
            })
            .then(CommandManager.argument("player", EntityArgumentType.player())
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .executes(context -> {
                        ServerPlayerEntity playerEntity = EntityArgumentType.getPlayer(context, "player");
                        StructureWorldState structureWorldState = context.getSource().getWorld().getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
                        try {
                            structureWorldState.deleteIsland(context.getSource().getWorld(), playerEntity);
                        } catch (InvalidChunkGenerator e) {
                            throw INVALID_CHUNK_GENERATOR.create();
                        } catch (NoIslandFound e) {
                            throw NO_ISLAND_FOR_UUID.create();
                        }
                        context.getSource().sendFeedback(new TranslatableText("commands.skyblockcreator.deleted_island", playerEntity.getDisplayName()), true);
                        return 1;
                    })
            );

    private static final LiteralArgumentBuilder<ServerCommandSource> teleport = CommandManager
            .literal("teleport")
            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(Mod.CONFIG.getTeleportToPlatformPermissionLevel()))
            .executes(context -> {
                StructureWorldState structureWorldState = context.getSource().getWorld().getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
                ServerPlayerEntity player = context.getSource().getPlayer();

                try {
                    structureWorldState.teleportToIsland(context.getSource().getWorld(), player);
                } catch (InvalidChunkGenerator invalidChunk) {
                    throw INVALID_CHUNK_GENERATOR.create();
                } catch (NoIslandFound noIsland) {
                    throw NO_ISLAND_FOR_UUID.create();
                }
                context.getSource().sendFeedback(new TranslatableText("commands.skyblockcreator.teleported_to_island", player.getDisplayName()), false);
                return 1;
            })
            .then(CommandManager.argument("player", EntityArgumentType.player())
                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                    .executes(context -> {
                        StructureWorldState structureWorldState = context.getSource().getWorld().getPersistentStateManager().getOrCreate(StructureWorldState::createFromNbt, StructureWorldState::new, "structureIslands");
                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");

                        try {
                            structureWorldState.teleportToIsland(context.getSource().getWorld(), context.getSource().getPlayer(), player);
                        } catch (InvalidChunkGenerator invalidChunk) {
                            throw INVALID_CHUNK_GENERATOR.create();
                        } catch (NoIslandFound noIsland) {
                            throw NO_ISLAND_FOR_UUID.create();
                        }
                        context.getSource().sendFeedback(new TranslatableText("commands.skyblockcreator.teleported_to_island", player.getDisplayName()), true);
                        return 1;
                    })
            );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> mainNode = dispatcher.register(
                CommandManager.literal(Mod.CONFIG.getCommandName())
                        .then(create)
                        .then(delete)
                        .then(teleport)
        );
        dispatcher.register(
                CommandManager.literal(Mod.CONFIG.getCommandAlias()).redirect(mainNode)
        );
    }

}