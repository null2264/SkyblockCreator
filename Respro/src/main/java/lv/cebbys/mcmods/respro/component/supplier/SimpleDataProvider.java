package lv.cebbys.mcmods.respro.component.supplier;

import lv.cebbys.mcmods.respro.api.pack.Data;
import lv.cebbys.mcmods.respro.api.supplier.DataProvider;
import lv.cebbys.mcmods.respro.component.resource.pack.ResproResourcePack;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleDataProvider extends SimplePackProvider<Data> implements DataProvider
{
    public SimpleDataProvider(Supplier<ResproResourcePack<?, ?>> packResources, Function<ResproResourcePack<?, ?>, Data> packCreator) {
        super(packResources, packCreator);
    }
}