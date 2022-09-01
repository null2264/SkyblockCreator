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
}