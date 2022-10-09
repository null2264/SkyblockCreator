package io.github.null2264.skyblockcreator.worldgen;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.core.ModClient;
import io.github.null2264.skyblockcreator.core.ModConfig;
import io.github.null2264.skyblockcreator.mixin.GeneratorTypeAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Environment(EnvType.CLIENT)
public class StructureWorldTypeClient extends StructureWorldType
{
    public StructureWorldTypeClient(ModConfig.StructureWorldConfig structureWorldConfig) {
        super(structureWorldConfig);
    }

    public static void register() {
        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            String structure = structureWorldConfig.getStructureIdentifier();
            StructureWorldTypeClient worldType = new StructureWorldTypeClient(structureWorldConfig);
            GeneratorType generatorType = new GeneratorType(Mod.MOD_ID + "." + structureWorldConfig.getStructureIdentifier()) {
                @Override
                public GeneratorOptions createDefaultOptions(DynamicRegistryManager registryManager, long seed, boolean generateStructures, boolean bonusChest) {
                    return worldType.createDefaultOptions(registryManager, seed, generateStructures, bonusChest);
                }

                @Override
                protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
                    return worldType.getOverworldChunkGenerator(registryManager);
                }
            };

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