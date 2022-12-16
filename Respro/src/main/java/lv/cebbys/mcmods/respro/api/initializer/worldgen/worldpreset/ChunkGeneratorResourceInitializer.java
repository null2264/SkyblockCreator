package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ChunkGeneratorResourceInitializer
{
    @NotNull ChunkGeneratorResourceInitializer setBiomeSource(Consumer<BiomeSourceResourceInitializer> biomeSourceConsumer);

    @NotNull ChunkGeneratorResourceInitializer setStructureChunkValues(BlockPos playerSpawnOffset, BlockPos structureOffset, String structure, Identifier fillmentBlock, boolean enableTopBedrock, boolean enableBottomBedrock, boolean isBedrockFlat);

    @NotNull ChunkGeneratorResourceInitializer setFromCodec(Codec<? extends ChunkGenerator> chunkGeneratorCodec);

    @NotNull ChunkGeneratorResourceInitializer setFromCodec(Codec<? extends ChunkGenerator> chunkGeneratorCodec, RegistryKey<ChunkGeneratorSettings> settings);
}