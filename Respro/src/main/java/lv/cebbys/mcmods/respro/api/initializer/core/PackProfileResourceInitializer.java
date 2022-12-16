package lv.cebbys.mcmods.respro.api.initializer.core;

import net.minecraft.resource.ResourcePackProfile;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface PackProfileResourceInitializer
{
    @NotNull PackProfileResourceInitializer setPackIcon(@NotNull Consumer<ImageResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackName(@NotNull Consumer<StringResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackSource(@NotNull Consumer<StringResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackMeta(@NotNull Consumer<MetaResourceInitializer> consumer);

    @NotNull PackProfileResourceInitializer setPackInsertionPosition(@NotNull ResourcePackProfile.InsertionPosition insertionPosition);

    @NotNull PackProfileResourceInitializer setAlwaysEnabled(boolean packAlwaysEnabled);

    @NotNull PackProfileResourceInitializer setPinned(boolean packPinned);
}