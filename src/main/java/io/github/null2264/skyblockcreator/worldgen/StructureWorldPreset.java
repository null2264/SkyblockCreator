package io.github.null2264.skyblockcreator.worldgen;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.core.ModConfig;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

import java.util.ArrayList;
import java.util.Map;

public class StructureWorldPreset {
    private static final ArrayList<RegistryKey<WorldPreset>> toBeDisplayed = new ArrayList<>();

    public static void register() {
        Mod.CONFIG.getStructureWorldConfigs().forEach(StructureWorldPreset::registerPreset);
        Mod.TO_BE_DISPLAYED = toBeDisplayed;
    }

    private static void registerPreset(ModConfig.StructureWorldConfig structureWorldConfig) {
        String structure = structureWorldConfig.getStructureIdentifier();
        RegistryKey<WorldPreset> worldPresetRegistryKey = Mod.registryKeyOf(structure);
        BuiltinRegistries.add(BuiltinRegistries.WORLD_PRESET, worldPresetRegistryKey, createOptions(structureWorldConfig));
        toBeDisplayed.add(worldPresetRegistryKey);
        if (structureWorldConfig.isOverridingDefault())
            Mod.OVERRIDED_LEVEL_TYPE = worldPresetRegistryKey.getValue().getPath();
        Mod.LOGGER.info("Successfully registered " + structureWorldConfig.getStructureIdentifier() + " generator type.");
    }

    private static ChunkGenerator getOverworldChunkGenerator(ModConfig.StructureWorldConfig structureWorldConfig) {
        RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));
        return new StructureChunkGenerator(
                BuiltinRegistries.STRUCTURE_SET,
                new FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateEntry(biomeKey)),
                structureWorldConfig.getStructureIdentifier(),
                structureWorldConfig.getStructureOffset(),
                structureWorldConfig.getPlayerSpawnOffset(),
                Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState(),
                structureWorldConfig.isTopBedrockEnabled(),
                structureWorldConfig.isBottomBedrockEnabled(),
                structureWorldConfig.isBedrockFlat()
        );
    }

    private static ChunkGenerator getNetherChunkGenerator(ModConfig.StructureWorldConfig structureWorldConfig) {
        if (structureWorldConfig.getTheNetherConfig().voidMode()) {
            return new StructureChunkGenerator(
                    BuiltinRegistries.STRUCTURE_SET,
                    "nether",
                    MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME),
                    structureWorldConfig.getStructureIdentifier(),
                    structureWorldConfig.getStructureOffset(),
                    structureWorldConfig.getPlayerSpawnOffset(),
                    Registry.BLOCK.get(new Identifier("minecraft", "air")).getDefaultState(),
                    structureWorldConfig.isTopBedrockEnabled(),
                    structureWorldConfig.isBottomBedrockEnabled(),
                    structureWorldConfig.isBedrockFlat()
            );
        }
        return new NoiseChunkGenerator(
                BuiltinRegistries.STRUCTURE_SET,
                BuiltinRegistries.NOISE_PARAMETERS,
                MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(BuiltinRegistries.BIOME),
                BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.NETHER)
        );
    }

    private static ChunkGenerator getEndChunkGenerator(ModConfig.StructureWorldConfig structureWorldConfig) {
        if (structureWorldConfig.getTheEndConfig().voidMode()) {
            return new StructureChunkGenerator(
                    BuiltinRegistries.STRUCTURE_SET,
                    "the_end",
                    new TheEndBiomeSource(BuiltinRegistries.BIOME),
                    structureWorldConfig.getStructureIdentifier(),
                    structureWorldConfig.getStructureOffset(),
                    structureWorldConfig.getPlayerSpawnOffset(),
                    Registry.BLOCK.get(new Identifier("minecraft", "air")).getDefaultState(),
                    structureWorldConfig.isTopBedrockEnabled(),
                    structureWorldConfig.isBottomBedrockEnabled(),
                    structureWorldConfig.isBedrockFlat()
            );
        }
        return new NoiseChunkGenerator(
                BuiltinRegistries.STRUCTURE_SET,
                BuiltinRegistries.NOISE_PARAMETERS,
                new TheEndBiomeSource(BuiltinRegistries.BIOME),
                BuiltinRegistries.CHUNK_GENERATOR_SETTINGS.getOrCreateEntry(ChunkGeneratorSettings.END)
        );
    }

    private static WorldPreset createOptions(ModConfig.StructureWorldConfig structureWorldConfig) {
        return new WorldPreset(
                Map.of(
                        DimensionOptions.OVERWORLD,
                        new DimensionOptions(
                                BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.OVERWORLD),
                                getOverworldChunkGenerator(structureWorldConfig)
                        ),
                        DimensionOptions.NETHER,
                        new DimensionOptions(
                                BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.THE_NETHER),
                                getNetherChunkGenerator(structureWorldConfig)
                        ),
                        DimensionOptions.END,
                        new DimensionOptions(
                                BuiltinRegistries.DIMENSION_TYPE.getOrCreateEntry(DimensionTypes.THE_END),
                                getEndChunkGenerator(structureWorldConfig)
                        )
                )
        );
    }
}