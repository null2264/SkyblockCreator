package io.github.null2264.skyblockcreator.core;

import io.github.null2264.skyblockcreator.Mod;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.util.Identifier;

import static io.github.null2264.skyblockcreator.Mod.OVERRIDED_LEVEL_TYPE;

public class ModServer implements DedicatedServerModInitializer
{

    @Override
    public void onInitializeServer() {
        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            Identifier structureIdentifier = new Identifier(Mod.MOD_ID, structureWorldConfig.getStructureIdentifier());

            if (structureWorldConfig.isOverridingDefault()) {
                OVERRIDED_LEVEL_TYPE = structureIdentifier.toString();
                Mod.LOGGER.info("Overridden default level-type to " + OVERRIDED_LEVEL_TYPE + " generator type.");
            }
        });
    }
}