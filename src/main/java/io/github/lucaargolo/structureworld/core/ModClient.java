package io.github.lucaargolo.structureworld.core;

import io.github.lucaargolo.structureworld.Mod;
import io.github.lucaargolo.structureworld.mixin.GeneratorTypeAccessor;
import io.github.lucaargolo.structureworld.worldgen.StructureGeneratorType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.world.GeneratorType;

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