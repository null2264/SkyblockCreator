package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface WorldPresetResourceInitializer
{
    @NotNull WorldPresetResourceInitializer setDimensions(Consumer<DimensionResourceInitializer> dimensionConsumer);
}