package lv.cebbys.mcmods.respro.mixin;

import lv.cebbys.mcmods.respro.utility.access.MultiNoiseBiomeSourceAccess;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin implements MultiNoiseBiomeSourceAccess
{
    @Shadow
    protected abstract Optional<MultiNoiseBiomeSource.Instance> getInstance();

    @Override
    public @NotNull MultiNoiseBiomeSource.Preset getPreset() {
        return (getInstance().orElseThrow()).preset();
    }
}