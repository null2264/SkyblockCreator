package lv.cebbys.mcmods.respro.api.initializer.custre;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public interface CustreRecipeResourceInitializer
{
    @NotNull CustreRecipeResourceInitializer setIngredient(@NotNull Identifier input);

    @NotNull CustreRecipeResourceInitializer setResult(@NotNull Identifier output);
}