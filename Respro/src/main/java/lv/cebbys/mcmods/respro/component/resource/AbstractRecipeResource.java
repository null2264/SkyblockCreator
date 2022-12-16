package lv.cebbys.mcmods.respro.component.resource;

import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRecipeResource extends AbstractJsonObjectResource
{

    @Override
    public boolean belongsTo(@NotNull ResourceType type) {
        return ResourceType.SERVER_DATA.equals(type);
    }
}