package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import io.github.null2264.skyblockcreator.core.ModServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GeneratorOptions.class, priority = -693)
public abstract class GeneratorOptionsMixin {

    @Inject(at = @At("HEAD"), method = "fromProperties", cancellable = true)
    private static void fromProperties(DynamicRegistryManager dynamicRegistryManager, ServerPropertiesHandler.WorldGenProperties properties, CallbackInfoReturnable<GeneratorOptions> info) {
        ModServer.fromPropertiesHook(dynamicRegistryManager, properties, info);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/ServerPropertiesHandler$WorldGenProperties;levelType()Ljava/lang/String;"), method = "fromProperties")
    private static String onDefaultLevelType(ServerPropertiesHandler.WorldGenProperties instance) {
        if (instance.levelType().equals("default") && ModServer.OVERRIDED_LEVEL_TYPE != null)
            return ModServer.OVERRIDED_LEVEL_TYPE;

        // Backwards compat
        if (instance.levelType().startsWith("structure_")) {
            String newType = instance.levelType().replace("structure_", Mod.MOD_ID + ":");
            Mod.LOGGER.warn("The usage of \"structure_\" is deprecated in version 1.2.4, please use \"" + Mod.MOD_ID + ":\" instead! (" + newType + ")");
            return newType;
        }

        return instance.levelType();
    }

}