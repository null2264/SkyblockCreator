package lv.cebbys.mcmods.respro.api.pack;

import lv.cebbys.mcmods.respro.component.resource.pack.profile.PackProfileResource;
import net.minecraft.resource.ResourcePackCompatibility;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Data extends ResourcePackProfile
{
    Identifier location;

    public Data(@NotNull Identifier packLocation, @NotNull PackProfileResource packProfile, @NotNull ResourcePackProfile.PackFactory supplier) {
        super(packLocation.toString(), packProfile.isAlwaysEnabled(), supplier, Text.literal(packProfile.getName().getAsString()), Objects.requireNonNull(loadMetadata(packLocation.toString(), supplier)), ResourcePackCompatibility.from(packProfile.getMeta().getFormat(), ResourceType.SERVER_DATA), packProfile.getPosition(), packProfile.isPinned(), ResourcePackSource.create((name) -> {
            Text text = Text.literal(packProfile.getSource().getAsString());
            return Text.translatable("pack.nameAndSource", name, text).formatted(Formatting.GRAY);
        }, true));
        location = packLocation;
    }
}