package lv.cebbys.mcmods.respro.component.resource;

import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.resource.ResourceType;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public abstract class AbstractResource
{
    public abstract @NotNull("AbstractResource stream is null") InputStream getAsStream();

    public abstract boolean belongsTo(@NotNull("Provided ResourceType is null") ResourceType type);

    public abstract void validate() throws ResourceValidationException;
}