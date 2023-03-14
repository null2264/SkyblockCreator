package io.github.null2264.skyblockcreator.mixin;

import io.github.null2264.skyblockcreator.Mod;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.client.gui.screen.world.WorldCreator.WorldType;
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

@Mixin(WorldCreator.class)
public abstract class MoreOptionsMixin
{

    @Shadow private WorldType worldType;

    @Redirect(method = "updateWorldTypeLists()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/world/WorldCreator;getWorldPresetList(Lnet/minecraft/registry/Registry;Lnet/minecraft/registry/tag/TagKey;)Ljava/util/Optional;"))
    public Optional<List<WorldType>> interceptPresets(Registry<WorldPreset> presetRegistry, TagKey<WorldPreset> tag) {
        Optional<List<WorldType>> presets = WorldCreator.getWorldPresetList(presetRegistry, tag);
        Mod.LOGGER.info(presets.toString());
        if (tag.equals(WorldPresetTags.EXTENDED) || presets.isEmpty()) return presets;

        ArrayList<WorldType> mutablePresets = new ArrayList<>(presets.get());
        Mod.LOGGER.info(Mod.TO_BE_DISPLAYED.toString());
        Mod.TO_BE_DISPLAYED.forEach(worldTypeKey -> {
            WorldType worldPreset = new WorldType(presetRegistry.getEntry(worldTypeKey).orElseThrow());
            if (worldTypeKey.getValue().getPath().equals(Mod.OVERRIDED_LEVEL_TYPE)) {
                mutablePresets.add(0, worldPreset);
                this.worldType = worldPreset;
                // this.apply((dynamicRegistryManager, dimensionsRegistryHolder) -> worldPreset.preset().value().createDimensionsRegistryHolder());
            } else mutablePresets.add(worldPreset);
        });

        return Optional.of(mutablePresets);
    }
}