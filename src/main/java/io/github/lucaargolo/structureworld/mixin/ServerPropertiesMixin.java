package io.github.lucaargolo.structureworld.mixin;

import io.github.lucaargolo.structureworld.core.ModServer;
import net.minecraft.server.dedicated.ServerPropertiesHandler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPropertiesHandler.WorldGenProperties.class)
public abstract class ServerPropertiesMixin {

    @Shadow
    @Final
    private String levelType;

    @Shadow
    public abstract String levelSeed();

    @Inject(
            method = "createGeneratorOptions(Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/world/gen/GeneratorOptions;",
            at = @At("HEAD"),
            cancellable = true
    )
    public void interceptGenerator(DynamicRegistryManager dynamicRegistryManager, CallbackInfoReturnable<GeneratorOptions> cir) {
        ModServer.fromPropertiesHook(dynamicRegistryManager, (ServerPropertiesHandler.WorldGenProperties) (Object) this, cir);
    }

    @Inject(at = @At(value = "HEAD"), method = "levelType()Ljava/lang/String;", cancellable = true)
    private void onDefaultLevelType(CallbackInfoReturnable<String> cir) {
        if (this.levelSeed().equals("default") && ModServer.OVERRIDED_LEVEL_TYPE != null) {
            cir.setReturnValue(ModServer.OVERRIDED_LEVEL_TYPE);
        }
    }
}