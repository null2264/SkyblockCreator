package io.github.lucaargolo.structureworld.core;

import io.github.lucaargolo.structureworld.Mod;
import io.github.lucaargolo.structureworld.worldgen.StructureChunkGenerator;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.block.BlockState;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ModServer implements DedicatedServerModInitializer {

    public static String OVERRIDED_LEVEL_TYPE = null;

    public static void fromPropertiesHook(DynamicRegistryManager dynamicRegistryManager, ServerPropertiesHandler.WorldGenProperties properties, CallbackInfoReturnable<GeneratorOptions> info) {
        String levelType = (properties.levelType() == null && OVERRIDED_LEVEL_TYPE != null)
                ? OVERRIDED_LEVEL_TYPE
                : properties.levelType();

        if (levelType.startsWith(Mod.MOD_ID + ":")) {
            Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
                String structure = structureWorldConfig.getStructureIdentifier();
                RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));

                if (levelType.equals(Mod.MOD_ID + ":" + structure)) {
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

    @Override
    public void onInitializeServer() {

        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            Identifier structureIdentifier = new Identifier(Mod.MOD_ID, structureWorldConfig.getStructureIdentifier());

            if (structureWorldConfig.isOverridingDefault()) {
                OVERRIDED_LEVEL_TYPE = structureIdentifier.getPath();
                Mod.LOGGER.info("Overridden default level-type to " + OVERRIDED_LEVEL_TYPE + " generator type.");
            }

        });

    }
}