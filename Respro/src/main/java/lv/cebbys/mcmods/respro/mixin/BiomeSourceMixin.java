package lv.cebbys.mcmods.respro.mixin;

import com.mojang.serialization.Codec;
import lv.cebbys.mcmods.respro.utility.access.BiomeSourceAccess;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.source.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(BiomeSource.class)
public abstract class BiomeSourceMixin implements BiomeSourceAccess
{
    @Shadow
    protected abstract Codec<? extends BiomeSource> getCodec();

    // Literally copied and pasted from ChunkGenerator
    public Optional<RegistryKey<Codec<? extends BiomeSource>>> getCodecKey() {
        return Registries.BIOME_SOURCE.getKey(getCodec());
    }
}