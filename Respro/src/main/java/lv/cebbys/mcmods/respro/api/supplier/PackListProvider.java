package lv.cebbys.mcmods.respro.api.supplier;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public interface PackListProvider<T extends ResourcePackProfile> extends ResourcePackProvider
{
    @NotNull Identifier getId();

    @NotNull List<T> getPacks();

    @Override
    default void register(Consumer<ResourcePackProfile> consumer) {
        getPacks().forEach(consumer);
    }
}