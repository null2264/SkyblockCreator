package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPropertiesHandler.WorldGenProperties.class)
public abstract class ServerPropertiesMixin {
    @Redirect(
            method = "createGeneratorOptions(Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/world/gen/GeneratorOptions;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/dedicated/ServerPropertiesHandler$WorldGenProperties;levelType:Ljava/lang/String;", opcode = Opcodes.GETFIELD)
    )
    public String handleLevelType(ServerPropertiesHandler.WorldGenProperties instance) {
        String currentType = instance.levelType();
        if (currentType.equals("default") && Mod.OVERRIDED_LEVEL_TYPE != null)
            return Mod.OVERRIDED_LEVEL_TYPE;

        // Backwards compat
        if (currentType.startsWith("structure_")) {
            String newType = currentType.replace("structure_", Mod.MOD_ID + ":");
            Mod.LOGGER.warn("The usage of \"structure_\" is deprecated in version 1.3.0, please use \"" + Mod.MOD_ID + ":\" instead! (" + newType + ")");
            return newType;
        }
        return currentType;
    }
}