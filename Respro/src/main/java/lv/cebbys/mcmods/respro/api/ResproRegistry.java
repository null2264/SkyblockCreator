package lv.cebbys.mcmods.respro.api;

import lv.cebbys.mcmods.respro.api.initializer.pack.DataResourcesInitializer;
import lv.cebbys.mcmods.respro.api.supplier.DataProvider;
import lv.cebbys.mcmods.respro.component.core.PackProviders;
import lv.cebbys.mcmods.respro.component.resource.pack.ResproDataPack;
import net.minecraft.registry.Registry;

import java.util.function.Consumer;

import static lv.cebbys.mcmods.respro.Respro.LOGGER;

public class ResproRegistry
{

    public static void registerData(DataProvider provider) {
        LOGGER.info(provider.getId().toString());
        Registry.register(PackProviders.DATA_PROFILE_PROVIDER, provider.getId(), provider);
    }

    public static void registerData(Consumer<DataResourcesInitializer> builderConsumer) {
        ResproDataPack builder = new ResproDataPack();
        builderConsumer.accept(builder);
        builder.validate();
        builder.dump();
        registerData(builder.getProvider());
    }
}