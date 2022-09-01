package io.github.null2264.skyblockcreator.core;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.worldgen.StructureGeneratorOptions;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ModServer implements DedicatedServerModInitializer {

    public static String OVERRIDED_LEVEL_TYPE = null;

    public static void fromPropertiesHook(DynamicRegistryManager dynamicRegistryManager, ServerPropertiesHandler.WorldGenProperties properties, CallbackInfoReturnable<GeneratorOptions> info) {
        String levelType = properties.levelType();
        if (levelType.equals("default") && ModServer.OVERRIDED_LEVEL_TYPE != null)
            levelType = ModServer.OVERRIDED_LEVEL_TYPE;

        // Backwards compat
        if (levelType.startsWith("structure_")) {
            levelType = levelType.replace("structure_", Mod.MOD_ID + ":");
            Mod.LOGGER.warn("The usage of \"structure_\" is deprecated in version 1.3.0, please use \"" + Mod.MOD_ID + ":\" instead! (" + levelType + ")");
        }

        if (levelType.startsWith(Mod.MOD_ID + ":")) {
            String finalLevelType = levelType;
            Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
                String structure = structureWorldConfig.getStructureIdentifier();

                if (finalLevelType.equals(Mod.MOD_ID + ":" + structure)) {
                    long seed;
                    if (!properties.levelSeed().isEmpty())
                        try {
                            seed = Long.parseLong(properties.levelSeed());
                        } catch (Exception e) {
                            seed = properties.levelSeed().hashCode();
                        }
                    else
                        seed = 0L;
                    info.setReturnValue(new StructureGeneratorOptions(structureWorldConfig).createDefaultOptions(dynamicRegistryManager, seed, properties.generateStructures(), false));
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