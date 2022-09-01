package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.tag.TagKey;
import net.minecraft.tag.WorldPresetTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(MoreOptionsDialog.class)
public abstract class MoreOptionsMixin {

    @Shadow
    private Optional<RegistryEntry<WorldPreset>> presetEntry;

    @Shadow
    private static Optional<List<RegistryEntry<WorldPreset>>> collectPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
        return Optional.empty();
    }

    @Shadow
    abstract void apply(GeneratorOptionsHolder.Modifier modifier);

    @Redirect(
            method = "init(Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/font/TextRenderer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;collectPresets(Lnet/minecraft/util/registry/Registry;Lnet/minecraft/tag/TagKey;)Ljava/util/Optional;")
    )
    public Optional<List<RegistryEntry<WorldPreset>>> interceptPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
        Optional<List<RegistryEntry<WorldPreset>>> presets = collectPresets(presetRegistry, tag);
        if (tag.equals(WorldPresetTags.EXTENDED) || presets.isEmpty())
            return presets;

        ArrayList<RegistryEntry<WorldPreset>> mutablePresets = new ArrayList<>(presets.get());
        Mod.TO_BE_DISPLAYED.forEach(worldTypeKey -> {
            RegistryEntry<WorldPreset> worldPreset = presetRegistry.getOrCreateEntry(worldTypeKey);
            if (worldTypeKey.getValue().getPath().equals(Mod.OVERRIDED_LEVEL_TYPE)) {
                mutablePresets.add(0, worldPreset);
                this.presetEntry = Optional.of(worldPreset);
                this.apply((generatorOptions) -> worldPreset.value().createGeneratorOptions(generatorOptions));
            } else
                mutablePresets.add(worldPreset);
        });

        return Optional.of(mutablePresets);
    }
}