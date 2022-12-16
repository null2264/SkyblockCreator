package lv.cebbys.mcmods.respro.api.initializer.pack;

import lv.cebbys.mcmods.respro.api.initializer.worldgen.WorldPresetsResourceInitializer;
import lv.cebbys.mcmods.respro.api.initializer.worldgen.worldpreset.WorldPresetResourceInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface DataResourcesInitializer extends PackResourcesInitializer<DataResourcesInitializer>
{
    @NotNull("DataResourcesInitializer is null") DataResourcesInitializer setWorldPreset(@NotNull("Identifier provided was null") Identifier id, @NotNull("WorldPresetResourceInitializer consumer was null") Consumer<WorldPresetResourceInitializer> consumer);

    @NotNull("DataResourcesInitializer is null") DataResourcesInitializer setWorldPresetsTag(@NotNull("WorldPresetsResourceInitializer consumer was null") Consumer<WorldPresetsResourceInitializer> consumer);
}