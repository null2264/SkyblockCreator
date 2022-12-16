package lv.cebbys.mcmods.respro.api.initializer.worldgen;

import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.DimensionResourceInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface WorldPresetsResourceInitializer
{
    @NotNull WorldPresetsResourceInitializer addWorldPreset(Identifier worldPresetId);
}