package io.github.lucaargolo.structureworld;

import io.github.lucaargolo.structureworld.mixin.GeneratorTypeAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;

public class ModClient implements ClientModInitializer {

    public static GeneratorType OVERRIDED_GENERATOR_TYPE = null;

    @Override
    public void onInitializeClient() {

        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            String structure = structureWorldConfig.getStructureIdentifier();
            RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));
            GeneratorType generatorType = new GeneratorType(structure) {
                @Override
                protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
                    Registry<StructureSet> structureSetRegistry = registryManager.get(Registry.STRUCTURE_SET_KEY);
                    Registry<Biome> biomeRegistry = registryManager.get(Registry.BIOME_KEY);
                    BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState();
                    return new StructureChunkGenerator(structureSetRegistry, new FixedBiomeSource(biomeRegistry.getOrCreateEntry(biomeKey)), structure, structureWorldConfig.getStructureOffset(), structureWorldConfig.getPlayerSpawnOffset(), fillmentBlockState, structureWorldConfig.isTopBedrockEnabled(), structureWorldConfig.isBottomBedrockEnabled(), structureWorldConfig.isBedrockFlat());
                }
            };

            if(structureWorldConfig.isOverridingDefault()) {
                GeneratorTypeAccessor.getValues().add(0, generatorType);
                OVERRIDED_GENERATOR_TYPE = generatorType;
                Mod.LOGGER.info("Successfully registered "+structure+" generator type. (Overriding default)");
            }else{
                GeneratorTypeAccessor.getValues().add(generatorType);
                Mod.LOGGER.info("Successfully registered "+structure+" generator type.");
            }

        });

    }
}