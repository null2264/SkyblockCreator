package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface DimensionResourceInitializer
{
    @NotNull DimensionResourceInitializer setChunkGenerator(Consumer<ChunkGeneratorResourceInitializer> chunkGeneratorConsumer);

    @NotNull DimensionResourceInitializer setFromRegistry(RegistryKey<DimensionType> dimensionType);
}