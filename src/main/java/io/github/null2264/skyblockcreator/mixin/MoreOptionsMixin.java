package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.WorldPresetTags;
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
    abstract void apply(GeneratorOptionsHolder.RegistryAwareModifier modifier);

    @Redirect(
            method = "init(Lnet/minecraft/client/gui/screen/world/CreateWorldScreen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/font/TextRenderer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/MoreOptionsDialog;collectPresets(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/tag/TagKey;)Ljava/util/Optional;")
    )
    public Optional<List<RegistryEntry<WorldPreset>>> interceptPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
        Optional<List<RegistryEntry<WorldPreset>>> presets = collectPresets(presetRegistry, tag);
        Mod.LOGGER.info(presets.toString());
        if (tag.equals(WorldPresetTags.EXTENDED) || presets.isEmpty())
            return presets;

        ArrayList<RegistryEntry<WorldPreset>> mutablePresets = new ArrayList<>(presets.get());
        Mod.LOGGER.info(Mod.TO_BE_DISPLAYED.toString());
        Mod.TO_BE_DISPLAYED.forEach(worldTypeKey -> {
            RegistryEntry<WorldPreset> worldPreset = presetRegistry.getEntry(worldTypeKey).orElseThrow();
            if (worldTypeKey.getValue().getPath().equals(Mod.OVERRIDED_LEVEL_TYPE)) {
                mutablePresets.add(0, worldPreset);
                this.presetEntry = Optional.of(worldPreset);
                this.apply((dynamicRegistryManager, dimensionsRegistryHolder) -> worldPreset.value().createDimensionsRegistryHolder());
            } else
                mutablePresets.add(worldPreset);
        });

        return Optional.of(mutablePresets);
    }
}