package io.github.null2264.skyblockcreator.worldgen;

import io.github.null2264.skyblockcreator.Mod;
import lv.cebbys.mcmods.respro.api.ResproRegistry;
import lv.cebbys.mcmods.respro.constant.ResproConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

import java.util.ArrayList;

import static io.github.null2264.skyblockcreator.Mod.identifierOf;

public class StructureWorldPresets
{
    private static final ArrayList<RegistryKey<WorldPreset>> toBeDisplayed = new ArrayList<>();

    public static void register() {
        ResproRegistry.registerData(data -> {
            data.setDumpMode(FabricLoader.getInstance().isDevelopmentEnvironment());
            data.setPackId(identifierOf("datapack"));
            data.setPackProfile(profile -> {
                profile.setAlwaysEnabled(true);
                profile.setPackName(name -> name.setText("SkyblockCreator Internal Data"));
                profile.setPackIcon(icon -> icon.setFromResources(Mod.class, "assets/skyblockcreator/icon.png"));
                profile.setPackMeta(meta -> {
                    meta.setDescription("SkyblockCreator's Internal Datapack");
                    meta.setFormat(ResproConstants.PACK_FORMAT);
                });
            });
            Mod.CONFIG.getStructureWorldConfigs().forEach(config -> {
                String structure = config.getStructureIdentifier();
                Identifier worldPresetId = identifierOf(structure);
                data.setWorldPreset(worldPresetId, worldPreset -> {
                    worldPreset.setDimensions(dim -> {
                        dim.setFromRegistry(DimensionTypes.OVERWORLD);
                        dim.setChunkGenerator(chunkGenerator -> {
                            chunkGenerator.setFromCodec(StructureChunkGenerator.CODEC);
                            chunkGenerator.setStructureChunkValues(config.getPlayerSpawnOffset(), config.getStructureOffset(), structure, new Identifier(config.getFillmentBlockIdentifier()), config.isTopBedrockEnabled(), config.isBottomBedrockEnabled(), config.isBedrockFlat());
                            chunkGenerator.setBiomeSource(biomeSource -> biomeSource.setFromCodec(FixedBiomeSource.CODEC, new Identifier(config.getBiomeIdentifier())));
                        });
                    });

                    worldPreset.setDimensions(dim -> {
                        dim.setFromRegistry(DimensionTypes.THE_NETHER);
                        dim.setChunkGenerator(chunkGenerator -> {
                            if (config.getTheNetherConfig().isVoidMode()) {
                                chunkGenerator.setFromCodec(StructureChunkGenerator.CODEC);
                                chunkGenerator.setStructureChunkValues(config.getPlayerSpawnOffset(), config.getStructureOffset(), structure, Identifier.of("minecraft", "air"), config.isTopBedrockEnabled(), config.isBottomBedrockEnabled(), config.isBedrockFlat());
                            } else {
                                chunkGenerator.setFromCodec(NoiseChunkGenerator.CODEC, ChunkGeneratorSettings.NETHER);
                            }
                            chunkGenerator.setBiomeSource(biomeSource -> biomeSource.setFromCodec(MultiNoiseBiomeSource.CODEC, MultiNoiseBiomeSourceParameterLists.NETHER));
                        });
                    });

                    worldPreset.setDimensions(dim -> {
                        dim.setFromRegistry(DimensionTypes.THE_END);
                        dim.setChunkGenerator(chunkGenerator -> {
                            if (config.getTheEndConfig().isVoidMode()) {
                                chunkGenerator.setFromCodec(StructureChunkGenerator.CODEC);
                                chunkGenerator.setStructureChunkValues(config.getPlayerSpawnOffset(), config.getStructureOffset(), structure, Identifier.of("minecraft", "air"), config.isTopBedrockEnabled(), config.isBottomBedrockEnabled(), config.isBedrockFlat());
                            } else {
                                chunkGenerator.setFromCodec(NoiseChunkGenerator.CODEC, ChunkGeneratorSettings.END);
                            }
                            chunkGenerator.setBiomeSource(biomeSource -> biomeSource.setFromCodec(TheEndBiomeSource.CODEC));
                        });
                    });
                });
                toBeDisplayed.add(Mod.registryKeyOf(structure));
                if (config.isOverridingDefault()) Mod.OVERRIDED_LEVEL_TYPE = worldPresetId.getPath();
                Mod.LOGGER.info("Successfully registered " + structure + " world type.");
            });
            Mod.TO_BE_DISPLAYED = toBeDisplayed;
        });
    }
}