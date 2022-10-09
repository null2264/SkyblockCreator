package io.github.null2264.skyblockcreator.worldgen;

import com.mojang.serialization.Lifecycle;
import io.github.null2264.skyblockcreator.core.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class StructureWorldType {
    ModConfig.StructureWorldConfig structureWorldConfig;

    public StructureWorldType(ModConfig.StructureWorldConfig structureWorldConfig) {
        this.structureWorldConfig = structureWorldConfig;
    }

    public GeneratorOptions createDefaultOptions(DynamicRegistryManager registryManager, long seed, boolean generateStructures, boolean bonusChest) {
        Registry<Biome> biome = registryManager.get(Registry.BIOME_KEY);
        Registry<StructureSet> structureSets = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<ChunkGeneratorSettings> chunkGeneratorSettings = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
        Registry<DoublePerlinNoiseSampler.NoiseParameters> worldGenNoise = registryManager.get(Registry.NOISE_WORLDGEN);
        Registry<DimensionType> dimensionRegistry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        SimpleRegistry<DimensionOptions> registry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.stable(), null);

        registry.add(DimensionOptions.OVERWORLD, new DimensionOptions(
                        dimensionRegistry.getOrCreateEntry(DimensionType.OVERWORLD_REGISTRY_KEY),
                        getOverworldChunkGenerator(registryManager)
                ),
                Lifecycle.stable()
        );

        registry.add(DimensionOptions.NETHER, new DimensionOptions(
                        dimensionRegistry.getOrCreateEntry(DimensionType.THE_NETHER_REGISTRY_KEY),
                        getNetherChunkGenerator(biome, structureSets, chunkGeneratorSettings, worldGenNoise, seed)
                ),
                Lifecycle.stable()
        );

        registry.add(DimensionOptions.END, new DimensionOptions(
                        dimensionRegistry.getOrCreateEntry(DimensionType.THE_END_REGISTRY_KEY),
                        getEndChunkGenerator(biome, structureSets, chunkGeneratorSettings, worldGenNoise, seed)
                ),
                Lifecycle.stable()
        );

        return new GeneratorOptions(seed, generateStructures, bonusChest, registry);
    }

    public ChunkGenerator getOverworldChunkGenerator(DynamicRegistryManager registryManager) {
        RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));
        Registry<StructureSet> structureSetRegistry = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<Biome> biomeRegistry = registryManager.get(Registry.BIOME_KEY);
        BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState();
        return new StructureChunkGenerator(
                structureSetRegistry,
                new FixedBiomeSource(biomeRegistry.getOrCreateEntry(biomeKey)),
                structureWorldConfig.getStructureIdentifier(),
                structureWorldConfig.getStructureOffset(),
                structureWorldConfig.getPlayerSpawnOffset(),
                fillmentBlockState,
                structureWorldConfig.isTopBedrockEnabled(),
                structureWorldConfig.isBottomBedrockEnabled(),
                structureWorldConfig.isBedrockFlat()
        );
    }

    private ChunkGenerator getNetherChunkGenerator(
            Registry<Biome> biome,
            Registry<StructureSet> structureSets,
            Registry<ChunkGeneratorSettings> chunkGeneratorSettings,
            Registry<DoublePerlinNoiseSampler.NoiseParameters> worldGenNoise,
            Long seed
    ) {
        if (structureWorldConfig.getTheNetherConfig().isVoidMode()) {
            BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier("minecraft", "air")).getDefaultState();
            return new StructureChunkGenerator(
                    structureSets,
                    "nether",
                    MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(biome, true),
                    structureWorldConfig.getStructureIdentifier(),
                    structureWorldConfig.getStructureOffset(),
                    structureWorldConfig.getPlayerSpawnOffset(),
                    fillmentBlockState,
                    structureWorldConfig.isTopBedrockEnabled(),
                    structureWorldConfig.isBottomBedrockEnabled(),
                    structureWorldConfig.isBedrockFlat()
            );
        }
        return new NoiseChunkGenerator(
                structureSets,
                worldGenNoise,
                MultiNoiseBiomeSource.Preset.NETHER.getBiomeSource(biome, true),
                seed,
                chunkGeneratorSettings.getOrCreateEntry(ChunkGeneratorSettings.NETHER)
        );
    }

    private ChunkGenerator getEndChunkGenerator(
            Registry<Biome> biome,
            Registry<StructureSet> structureSets,
            Registry<ChunkGeneratorSettings> chunkGeneratorSettings,
            Registry<DoublePerlinNoiseSampler.NoiseParameters> worldGenNoise,
            Long seed
    ) {
        if (structureWorldConfig.getTheEndConfig().isVoidMode()) {
            BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier("minecraft", "air")).getDefaultState();
            return new StructureChunkGenerator(
                    structureSets,
                    "the_end",
                    new TheEndBiomeSource(biome, seed),
                    structureWorldConfig.getStructureIdentifier(),
                    structureWorldConfig.getStructureOffset(),
                    structureWorldConfig.getPlayerSpawnOffset(),
                    fillmentBlockState,
                    structureWorldConfig.isTopBedrockEnabled(),
                    structureWorldConfig.isBottomBedrockEnabled(),
                    structureWorldConfig.isBedrockFlat()
            );
        }
        return new NoiseChunkGenerator(
                structureSets,
                worldGenNoise,
                new TheEndBiomeSource(biome, seed),
                seed,
                chunkGeneratorSettings.getOrCreateEntry(ChunkGeneratorSettings.END)
        );
    }
}