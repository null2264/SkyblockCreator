package lv.cebbys.mcmods.respro.component.resource.string.worldgen.worldpreset;

import com.mojang.serialization.Codec;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.BiomeSourceResourceInitializer;
import lv.cebbys.mcmods.respro.component.mapper.JsonPart;
import lv.cebbys.mcmods.respro.component.resource.AbstractJsonObjectResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import lv.cebbys.mcmods.respro.utility.access.MultiNoiseBiomeSourcePresetAccess;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.MultiNoiseBiomeSource;
import org.jetbrains.annotations.NotNull;

public class BiomeSourceResource extends AbstractJsonObjectResource implements BiomeSourceResourceInitializer
{
    @JsonPart("type")
    protected Identifier type;
    @JsonPart("preset")
    protected Identifier preset;
    @JsonPart("biome")
    protected Identifier biome;

    @Override
    public @NotNull BiomeSourceResourceInitializer setValues(Identifier type, Identifier preset) {
        this.type = type;
        this.preset = preset;
        return this;
    }

    public @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec) {
        this.type = Registries.BIOME_SOURCE.getKey(biomeSourceCodec).orElseThrow().getValue();
        return this;
    }

    public @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, Identifier biomeId) {
        this.biome = biomeId;
        return setFromCodec(biomeSourceCodec);
    }

    public @NotNull BiomeSourceResourceInitializer setFromCodec(Codec<? extends BiomeSource> biomeSourceCodec, MultiNoiseBiomeSource.Preset preset) {
        this.preset = ((MultiNoiseBiomeSourcePresetAccess) preset).getPresetId();
        return setFromCodec(biomeSourceCodec);
    }

    @Override
    public boolean belongsTo(@NotNull("Provided ResourceType is null") ResourceType type) {
        return ResourceType.SERVER_DATA.equals(type);
    }

    @Override
    public void validate() throws ResourceValidationException {

    }
}