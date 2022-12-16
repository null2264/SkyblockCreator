package lv.cebbys.mcmods.respro.utility.access;

import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.jetbrains.annotations.NotNull;

public interface MultiNoiseBiomeSourcePresetAccess
{
    @NotNull Identifier getPresetId();
}