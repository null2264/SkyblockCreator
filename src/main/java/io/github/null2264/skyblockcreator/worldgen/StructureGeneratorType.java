package io.github.null2264.skyblockcreator.worldgen;

import com.mojang.serialization.Lifecycle;
import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.core.ModClient;
import io.github.null2264.skyblockcreator.core.ModConfig;
import io.github.null2264.skyblockcreator.mixin.GeneratorTypeAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.*;
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

@Environment(EnvType.CLIENT)
public class StructureGeneratorType extends GeneratorType {
    ModConfig.StructureWorldConfig structureWorldConfig;

    protected StructureGeneratorType(ModConfig.StructureWorldConfig structureWorldConfig) {
        super(Mod.MOD_ID + "." + structureWorldConfig.getStructureIdentifier());
        this.structureWorldConfig = structureWorldConfig;
    }

    @Override
    public GeneratorOptions createDefaultOptions(DynamicRegistryManager registryManager, long seed, boolean generateStructures, boolean bonusChest) {
        Registry<Biome> biome = registryManager.get(Registry.BIOME_KEY);
        Registry<StructureSet> structureSets = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<ChunkGeneratorSettings> chunkGeneratorSettings = registryManager.get(Registry.CHUNK_GENERATOR_SETTINGS_KEY);
        Registry<DoublePerlinNoiseSampler.NoiseParameters> worldGenNoise = registryManager.get(Registry.NOISE_WORLDGEN);
        Registry<DimensionType> dimensionRegistry = registryManager.get(Registry.DIMENSION_TYPE_KEY);
        SimpleRegistry<DimensionOptions> registry = new SimpleRegistry<>(Registry.DIMENSION_KEY, Lifecycle.experimental(), null);

        registry.add(DimensionOptions.OVERWORLD, new DimensionOptions(
                dimensionRegistry.getOrCreateEntry(DimensionType.OVERWORLD_REGISTRY_KEY),
                this.getChunkGenerator(registryManager, seed)
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

    @Override
    protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
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
        if (structureWorldConfig.getTheEndConfig().isVoidMode()) {
            BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier("minecraft", "air")).getDefaultState();
            return new StructureChunkGenerator(
                    structureSets,
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

    public static void register() {
        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            String structure = structureWorldConfig.getStructureIdentifier();
            GeneratorType generatorType = new StructureGeneratorType(structureWorldConfig);

            if (structureWorldConfig.isOverridingDefault()) {
                GeneratorTypeAccessor.getValues().add(0, generatorType);
                ModClient.OVERRIDED_GENERATOR_TYPE = generatorType;
                Mod.LOGGER.info("Successfully registered " + structure + " generator type. (Overriding default)");
            } else {
                GeneratorTypeAccessor.getValues().add(generatorType);
                Mod.LOGGER.info("Successfully registered " + structure + " generator type.");
            }

        });
    }
}