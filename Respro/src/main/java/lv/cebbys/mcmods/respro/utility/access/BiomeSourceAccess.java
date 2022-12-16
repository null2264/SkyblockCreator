package lv.cebbys.mcmods.respro.utility.access;

import com.mojang.serialization.Codec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.Optional;

public interface BiomeSourceAccess
{
    Optional<RegistryKey<Codec<? extends BiomeSource>>> getCodecKey();
}