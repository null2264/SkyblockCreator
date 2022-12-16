package lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.jetbrains.annotations.NotNull;

public interface BiomeSourceResourceInitializer
{
    @NotNull BiomeSourceResourceInitializer setValues(Identifier type, Identifier preset);

    @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec);

    @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, Identifier biomeId);

    @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, MultiNoiseBiomeSource.Preset preset);
}