package lv.cebbys.mcmods.respro.component.resource.core;

import lv.cebbys.mcmods.respro.api.initializer.core.StringResourceInitializer;
import lv.cebbys.mcmods.respro.component.resource.AbstractStringResource;
import lv.cebbys.mcmods.respro.exception.ResourceValidationException;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.NotNull;

public final class StringResource extends AbstractStringResource implements StringResourceInitializer
{
    private String content;

    public StringResource(@NotNull String textContent) {
        content = textContent;
    }

    public StringResource() {
        content = null;
    }

    @Override
    public @NotNull StringResourceInitializer setTranslatableText(@NotNull String translationKey) {
        content = translationKey;
        return this;
    }

    @Override
    public @NotNull StringResourceInitializer setText(@NotNull String text) {
        content = text;
        return this;
    }

    @Override
    public @NotNull("StringResource content is null") String getAsString() {
        return content;
    }

    @SuppressWarnings("all")
    @Override
    public void validate() throws ResourceValidationException {
        try {
            getAsString();
        } catch (Exception e) {
            throw new InvalidIdentifierException("TextResource content failed validation", e);
        }
    }

    @Override
    public boolean belongsTo(@NotNull ResourceType type) {
        return true;
    }
}