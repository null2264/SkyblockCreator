package io.github.lucaargolo.structureworld.worldgen;

import io.github.lucaargolo.structureworld.Mod;
import io.github.lucaargolo.structureworld.core.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.GeneratorType;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Environment(EnvType.CLIENT)
public class StructureGeneratorType extends GeneratorType {
    ModConfig.StructureWorldConfig structureWorldConfig;

    public StructureGeneratorType(ModConfig.StructureWorldConfig structureWorldConfig) {
        super(Mod.MOD_ID + "." + structureWorldConfig.getStructureIdentifier());
        this.structureWorldConfig = structureWorldConfig;
    }

    @Override
    protected ChunkGenerator getChunkGenerator(DynamicRegistryManager registryManager, long seed) {
        RegistryKey<Biome> biomeKey = RegistryKey.of(Registry.BIOME_KEY, new Identifier(structureWorldConfig.getBiomeIdentifier()));
        Registry<StructureSet> structureSetRegistry = registryManager.get(Registry.STRUCTURE_SET_KEY);
        Registry<Biome> biomeRegistry = registryManager.get(Registry.BIOME_KEY);
        BlockState fillmentBlockState = Registry.BLOCK.get(new Identifier(structureWorldConfig.getFillmentBlockIdentifier())).getDefaultState();
        return new StructureChunkGenerator(structureSetRegistry, new FixedBiomeSource(biomeRegistry.getOrCreateEntry(biomeKey)), structureWorldConfig.getStructureIdentifier(), structureWorldConfig.getStructureOffset(), structureWorldConfig.getPlayerSpawnOffset(), fillmentBlockState, structureWorldConfig.isTopBedrockEnabled(), structureWorldConfig.isBottomBedrockEnabled(), structureWorldConfig.isBedrockFlat());
    }
}