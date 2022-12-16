package lv.cebbys.mcmods.respro.constant;

import net.minecraft.resource.ResourcePackSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class ResproConstants
{
    public static final String RESPRO = "respro";
    public static final String PACK_MCMETA_PATH = "pack.mcmeta";
    public static final String PACK_ICON_PATH = "pack.png";
    public static final String PACK_SOURCE_STRING_RESPRO = "pack.source.respro";
    public static final ResourcePackSource PACK_SOURCE_RESPRO = ResourcePackSource.create((name) -> {
        Text text = Text.translatable(PACK_SOURCE_STRING_RESPRO);
        return Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
    }, true);
    public static final Identifier RESPRO_PACK_MCMETA_LOCATION = new Identifier(RESPRO, PACK_MCMETA_PATH);
    public static final Identifier RESPRO_PACK_ICON_LOCATION = new Identifier(RESPRO, PACK_ICON_PATH);
}