package lv.cebbys.mcmods.respro.utility.access;

import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.jetbrains.annotations.NotNull;

public interface MultiNoiseBiomeSourceAccess
{
    @NotNull MultiNoiseBiomeSource.Preset getPreset();
}