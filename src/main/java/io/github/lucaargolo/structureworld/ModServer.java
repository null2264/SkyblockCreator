package io.github.lucaargolo.structureworld;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ModServer implements DedicatedServerModInitializer {

    public static String OVERRIDED_LEVEL_TYPE = null;

    @Override
    public void onInitializeServer() {

        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            Identifier structureIdentifier = new Identifier(structureWorldConfig.getStructureIdentifier());

            if(structureWorldConfig.isOverridingDefault()) {
                OVERRIDED_LEVEL_TYPE = "structure_"+structureIdentifier.getPath();
                Mod.LOGGER.info("Overridden default level-type to "+OVERRIDED_LEVEL_TYPE+" generator type.");
            }

        });

    }

    public static void fromPropertiesHook(DynamicRegistryManager dynamicRegistryManager, ServerPropertiesHandler.WorldGenProperties properties, CallbackInfoReturnable<GeneratorOptions> info) {
        String levelType;
        if(properties.levelType() == null && OVERRIDED_LEVEL_TYPE != null) {
            levelType = OVERRIDED_LEVEL_TYPE;
        }else{
            levelType = properties.levelType();
        }
        if(levelType.startsWith("structure_")) {
            Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
                String structure = structureWorldConfig.getStructureIdentifier();
                RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));

                if(levelType.equals("structure_"+structure)) {
                    Registry<DimensionType> dimensionTypeRegistry = dynamicRegistryManager.get(Registry.DIMENSION_TYPE_KEY);
                    Registry<Biome> biomeRegistry = dynamicRegistryManager.get(Registry.BIOME_KEY);
                    Registry<DimensionOptions> simpleRegistry = DimensionType.createDefaultDimensionOptions(dynamicRegistryManager, 0L);

                    BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState();
                    Registry<StructureSet> structureSetRegistry = dynamicRegistryManager.get(Registry.STRUCTURE_SET_KEY);
                    StructureChunkGenerator structureChunkGenerator = new StructureChunkGenerator(structureSetRegistry, new FixedBiomeSource(biomeRegistry.getOrCreateEntry(biomeKey)), structure, structureWorldConfig.getStructureOffset(), structureWorldConfig.getPlayerSpawnOffset(), fillmentBlockState, structureWorldConfig.isTopBedrockEnabled(), structureWorldConfig.isBottomBedrockEnabled(), structureWorldConfig.isBedrockFlat());
                    Registry<DimensionOptions> finalRegistry = GeneratorOptions.getRegistryWithReplacedOverworldGenerator(dimensionTypeRegistry, simpleRegistry, structureChunkGenerator);
                    info.setReturnValue(new GeneratorOptions(0L, false, false, finalRegistry));
                }
            });
        }
    }
}