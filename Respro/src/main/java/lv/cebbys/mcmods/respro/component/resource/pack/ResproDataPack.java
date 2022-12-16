package lv.cebbys.mcmods.respro.component.resource.pack;

import lv.cebbys.mcmods.respro.api.initializer.pack.DataResourcesInitializer;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.WorldPresetsResourceInitializer;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.WorldPresetResourceInitializer;
import lv.cebbys.mcmods.respro.api.pack.Data;
import lv.cebbys.mcmods.respro.api.supplier.DataProvider;
import lv.cebbys.mcmods.respro.component.supplier.SimpleDataProvider;
import lv.cebbys.mcmods.respro.utility.IdentifierUtils;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

public class ResproDataPack extends ResproResourcePack<ResproDataPack, DataProvider> implements DataResourcesInitializer
{

    @Override
    public DataProvider getProvider() {
        return new SimpleDataProvider(this::getInstance, (resources) -> new Data(resources.getId(), resources.getProfile(), ignored -> resources));
    }

    @Override
    protected ResproDataPack getInstance() {
        return this;
    }

    @Override
    public @NotNull("DataResourcesInitializer is null") DataResourcesInitializer setWorldPreset(@NotNull("Identifier provided was null") Identifier id, @NotNull("WorldPresetResourceInitializer consumer was null") Consumer<WorldPresetResourceInitializer> consumer) {
        return setResource(WorldPresetResourceInitializer.class, IdentifierUtils.wrapped("worldgen/world_preset/", id, ".json"), consumer);
    }

    @Override
    public @NotNull("DataResourcesInitializer is null") DataResourcesInitializer setWorldPresetsTag(@NotNull("WorldPresetsResourceInitializer consumer was null") Consumer<WorldPresetsResourceInitializer> consumer) {
        return setResource(WorldPresetsResourceInitializer.class, IdentifierUtils.wrapped("tags/worldgen/world_preset/", Objects.requireNonNull(Identifier.of("minecraft", "normal")), ".json"), consumer);
    }

    @Override
    public @NotNull DataResourcesInitializer setDumpMode(boolean enabledDump) {
        return this.setEnabledDumpMode(enabledDump);
    }
}