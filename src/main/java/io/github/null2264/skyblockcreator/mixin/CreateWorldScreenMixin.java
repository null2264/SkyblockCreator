package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.core.ModClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.OptionalLong;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {

    @Inject(at = @At("HEAD"), method = "create*", cancellable = true)
    private static void onCreate(Screen parent, CallbackInfoReturnable<CreateWorldScreen> callbackInfoReturnable) {
        if (ModClient.OVERRIDED_GENERATOR_TYPE != null) {
            DynamicRegistryManager.Mutable dynamicRegistryManager = DynamicRegistryManager.createAndLoad();
            GeneratorOptions hackedGeneratorOptions = ModClient.OVERRIDED_GENERATOR_TYPE.createDefaultOptions(dynamicRegistryManager, 0L, true, false);
            MoreOptionsDialog hackedOptionsDialog = new MoreOptionsDialog(dynamicRegistryManager.toImmutable(), hackedGeneratorOptions, Optional.of(ModClient.OVERRIDED_GENERATOR_TYPE), OptionalLong.empty());
            CreateWorldScreen hackedWorldScreen = new CreateWorldScreen(parent, DataPackSettings.SAFE_MODE, hackedOptionsDialog);
            callbackInfoReturnable.setReturnValue(hackedWorldScreen);
        }
    }

}