package lv.cebbys.mcmods.respro.component.core;

import com.mojang.serialization.Lifecycle;
import lv.cebbys.mcmods.respro.Respro;
import lv.cebbys.mcmods.respro.api.pack.Data;
import lv.cebbys.mcmods.respro.api.supplier.DataListProvider;
import lv.cebbys.mcmods.respro.api.supplier.DataProvider;
import lv.cebbys.mcmods.respro.api.supplier.PackListProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static lv.cebbys.mcmods.respro.constant.ResproConstants.RESPRO;

public class PackProviders
{
    public static final Registry<DataProvider> DATA_PROFILE_PROVIDER;
    public static final PackListProvider<Data> RESPRO_DATA_PROVIDER;

    static {
        DATA_PROFILE_PROVIDER = new SimpleRegistry<>(RegistryKey.ofRegistry(new Identifier(Respro.MODID, "data_profile_suppliers")), Lifecycle.stable(), false);
        RESPRO_DATA_PROVIDER = new DataListProvider()
        {
            private final Identifier id = new Identifier(RESPRO, "respro_data_supplier");

            @Override
            public @NotNull Identifier getId() {
                return id;
            }

            @Override
            public @NotNull List<Data> getPacks() {
                return PackProviders.DATA_PROFILE_PROVIDER.stream().map(DataProvider::getPack).toList();
            }
        };
    }
}