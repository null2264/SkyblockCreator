package io.github.null2264.skyblockcreator.worldgen;

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
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Environment(EnvType.CLIENT)
public class StructureGeneratorType extends GeneratorType {
    ModConfig.StructureWorldConfig structureWorldConfig;

    protected StructureGeneratorType(ModConfig.StructureWorldConfig structureWorldConfig) {
        super(Mod.MOD_ID + "." + structureWorldConfig.getStructureIdentifier());
        this.structureWorldConfig = structureWorldConfig;
    }

    @Override
    protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
        RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));
        Registry<StructureSet> structureSetRegistry = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<Biome> biomeRegistry = registryManager.get(Registry.BIOME_KEY);
        BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState();
        return new StructureChunkGenerator(structureSetRegistry, new FixedBiomeSource(biomeRegistry.getOrCreateEntry(biomeKey)), structureWorldConfig.getStructureIdentifier(), structureWorldConfig.getStructureOffset(), structureWorldConfig.getPlayerSpawnOffset(), fillmentBlockState, structureWorldConfig.isTopBedrockEnabled(), structureWorldConfig.isBottomBedrockEnabled(), structureWorldConfig.isBedrockFlat());
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