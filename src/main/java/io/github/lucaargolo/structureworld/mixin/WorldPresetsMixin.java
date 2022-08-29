package io.github.lucaargolo.structureworld.mixin;

import io.github.lucaargolo.structureworld.StructureChunkGenerator;
import io.github.lucaargolo.structureworld.core.Mod;
import io.github.lucaargolo.structureworld.core.ModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(WorldPresets.Registrar.class)
public abstract class WorldPresetsMixin {
    private final ArrayList<RegistryKey<WorldPreset>> toBeDisplayed = new ArrayList<>();

    @Shadow
    protected abstract RegistryEntry<WorldPreset> register(RegistryKey<WorldPreset> key, DimensionOptions dimensionOptions);

    @Shadow
    protected abstract DimensionOptions createOverworldOptions(ChunkGenerator chunkGenerator);

    private void registerPreset(ModConfig.StructureWorldConfig structureWorldConfig) {
        String structure = structureWorldConfig.getStructureIdentifier();
        RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));
        BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState();
        RegistryKey<WorldPreset> worldPresetRegistryKey = Mod.registryKeyOf(structure);
        this.register(
                worldPresetRegistryKey,
                this.createOverworldOptions(
                        new StructureChunkGenerator(
                                BuiltinRegistries.STRUCTURE_SET,
                                new FixedBiomeSource(BuiltinRegistries.BIOME.getOrCreateEntry(biomeKey)),
                                structure,
                                structureWorldConfig.getStructureOffset(),
                                structureWorldConfig.getPlayerSpawnOffset(),
                                fillmentBlockState,
                                structureWorldConfig.isTopBedrockEnabled(),
                                structureWorldConfig.isBottomBedrockEnabled(),
                                structureWorldConfig.isBedrockFlat()
                        )
                )
        );
        toBeDisplayed.add(worldPresetRegistryKey);
        if (structureWorldConfig.isOverridingDefault()) {
            Mod.OVERRIDED_LEVEL_TYPE = worldPresetRegistryKey.getValue().getPath();
        }
    }

    @Inject(method = "initAndGetDefault", at = @At("RETURN"))
    private void managePresetEarly(CallbackInfoReturnable<RegistryEntry<WorldPreset>> cir) {
        Mod.CONFIG.getStructureWorldConfigs().forEach(structureWorldConfig -> {
            registerPreset(structureWorldConfig);
            Mod.LOGGER.info("Successfully registered " + structureWorldConfig.getStructureIdentifier() + " generator type.");
        });
        Mod.TO_BE_DISPLAYED = toBeDisplayed;
    }
}