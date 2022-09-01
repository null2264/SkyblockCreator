package io.github.null2264.skyblockcreator.worldgen;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.core.ModClient;
import io.github.null2264.skyblockcreator.core.ModConfig;
import io.github.null2264.skyblockcreator.mixin.GeneratorTypeAccessor;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class StructureGeneratorType extends GeneratorType {
    StructureGeneratorOptions structureGeneratorOptions;

    public StructureGeneratorType(ModConfig.StructureWorldConfig structureWorldConfig) {
        super(Mod.MOD_ID + "." + structureWorldConfig.getStructureIdentifier());
        this.structureGeneratorOptions = new StructureGeneratorOptions(structureWorldConfig);
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

    @Override
    public GeneratorOptions createDefaultOptions(DynamicRegistryManager registryManager, long seed, boolean generateStructures, boolean bonusChest) {
        return structureGeneratorOptions.createDefaultOptions(registryManager, seed, generateStructures, bonusChest);
    }

    @Override
    protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
        return structureGeneratorOptions.getChunkGenerator(registryManager, seed);
    }
}