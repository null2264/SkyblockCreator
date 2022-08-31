package io.github.lucaargolo.structureworld.core;

import io.github.lucaargolo.structureworld.Mod;
import io.github.lucaargolo.structureworld.worldgen.StructureChunkGenerator;
import io.github.lucaargolo.structureworld.mixin.GeneratorTypeAccessor;
import io.github.lucaargolo.structureworld.worldgen.StructureGeneratorType;
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

public class ModClient implements ClientModInitializer {

    public static GeneratorType OVERRIDED_GENERATOR_TYPE = null;

    @Override
    public void onInitializeClient() {

        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            String structure = structureWorldConfig.getStructureIdentifier();
            GeneratorType generatorType = new StructureGeneratorType(structureWorldConfig);

            if (structureWorldConfig.isOverridingDefault()) {
                GeneratorTypeAccessor.getValues().add(0, generatorType);
                OVERRIDED_GENERATOR_TYPE = generatorType;
                Mod.LOGGER.info("Successfully registered " + structure + " generator type. (Overriding default)");
            } else {
                GeneratorTypeAccessor.getValues().add(generatorType);
                Mod.LOGGER.info("Successfully registered " + structure + " generator type.");
            }

        });

    }
}