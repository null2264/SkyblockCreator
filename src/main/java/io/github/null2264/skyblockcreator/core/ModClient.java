package io.github.null2264.skyblockcreator.core;

import io.github.null2264.skyblockcreator.worldgen.StructureWorldType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.world.GeneratorType;

public class ModClient implements ClientModInitializer {

    public static GeneratorType OVERRIDED_GENERATOR_TYPE = null;

    @Override
    public void onInitializeClient() {
        StructureWorldType.register();
    }
}