package lv.cebbys.mcmods.respro.mixin;

import lv.cebbys.mcmods.respro.utility.access.MultiNoiseBiomeSourcePresetAccess;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MultiNoiseBiomeSource.Preset.class)
public abstract class MultiNoiseBiomeSourcePresetMixin implements MultiNoiseBiomeSourcePresetAccess
{
    @Shadow
    @Final
    Identifier id;

    @Override
    public @NotNull Identifier getPresetId() {
        return id;
    }
}