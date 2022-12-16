package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets={"net.minecraft.server.dedicated.ServerPropertiesHandler$WorldGenProperties"})
public abstract class ServerPropertiesMixin {
    @Shadow @Final private String levelType;

    @Redirect(
            method = "createDimensionsRegistryHolder(Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/world/dimension/DimensionOptionsRegistryHolder;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/dedicated/ServerPropertiesHandler$WorldGenProperties;levelType:Ljava/lang/String;", opcode = Opcodes.GETFIELD)
    )
    public String handleLevelType(@Coerce Object instance) {
        String currentType = levelType;
        if (currentType.equals("default") && Mod.OVERRIDED_LEVEL_TYPE != null) {
            Mod.LOGGER.debug("Redirecting level_type to '" + Mod.OVERRIDED_LEVEL_TYPE + "'");
            return Mod.OVERRIDED_LEVEL_TYPE;
        }
        return currentType;
    }
}