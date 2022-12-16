package lv.cebbys.mcmods.respro.api.initializer.worldgen;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface WorldPresetsResourceInitializer
{
    @NotNull WorldPresetsResourceInitializer addWorldPreset(Identifier worldPresetId);
}