package lv.cebbys.mcmods.respro.component.resource.pack;

import com.google.gson.JsonObject;
import lv.cebbys.mcmods.respro.Respro;
import lv.cebbys.mcmods.respro.api.ResproBuilders;
import lv.cebbys.mcmods.respro.api.builder.ResourceBuilder;
import lv.cebbys.mcmods.respro.api.initializer.core.PackProfileResourceInitializer;
import lv.cebbys.mcmods.respro.api.initializer.pack.PackResourcesInitializer;
import lv.cebbys.mcmods.respro.api.supplier.DataProvider;
import lv.cebbys.mcmods.respro.component.core.ResproPackDump;
import lv.cebbys.mcmods.respro.component.resource.AbstractResource;
import lv.cebbys.mcmods.respro.component.resource.core.MetaResource;
import lv.cebbys.mcmods.respro.component.resource.pack.profile.PackProfileResource;
import lv.cebbys.mcmods.respro.exception.PackGenerationException;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.input.NullInputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static lv.cebbys.mcmods.respro.constant.ResproConstants.PACK_ICON_PATH;
import static lv.cebbys.mcmods.respro.constant.ResproConstants.PACK_MCMETA_PATH;

public abstract class ResproResourcePack<B extends PackResourcesInitializer<?>, S extends DataProvider> implements ResourcePack
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Respro.class);
    private static final ResproPackDump DUMP = new ResproPackDump();

    private final Map<Identifier, AbstractResource> resources = new HashMap<>();
    private final Set<String> assetNamespaces = new HashSet<>();
    private final Set<String> dataNamespaces = new HashSet<>();
    private Identifier id;
    private PackProfileResource profile = new PackProfileResource();
    private boolean enabledDumpMode;

    protected abstract B getInstance();

    public abstract S getProvider();

    public @NotNull B setEnabledDumpMode(boolean isDumpMode) {
        enabledDumpMode = isDumpMode;
        return getInstance();
    }

    public @NotNull B setPackId(@NotNull Identifier packId) {
        id = packId;
        return getInstance();
    }

    public @NotNull B setPackProfile(@NotNull Consumer<PackProfileResourceInitializer> consumer) {
        profile = new PackProfileResource();
        consumer.accept(profile);
        setResource(new Identifier(id.getNamespace(), PACK_ICON_PATH), profile.getIcon());
        setResource(new Identifier(id.getNamespace(), PACK_MCMETA_PATH), profile.getMeta());
        return getInstance();
    }

    public @NotNull B setResource(@NotNull Identifier id, @NotNull AbstractResource resource) {
        if (resource.belongsTo(ResourceType.CLIENT_RESOURCES)) {
            assetNamespaces.add(id.getNamespace());
        }
        if (resource.belongsTo(ResourceType.SERVER_DATA)) {
            dataNamespaces.add(id.getNamespace());
        }
        resources.put(id, resource);
        return getInstance();
    }

    public @NotNull <I> B setResource(Class<I> initializerClass, Identifier location, Consumer<I> consumer) {
        ResourceBuilder<I, ?> builder = ResproBuilders.supplyBuilder(initializerClass);
        if (builder == null) {
            return getInstance();
        }
        builder.initialize(consumer);
        builder.validate();
        return setResource(location, builder.build());
    }

    public Map<Identifier, AbstractResource> getResources() {
        return resources;
    }

    @Override
    public InputSupplier<InputStream> openRoot(String... pathSegments) {
        String path = String.join("/", pathSegments);
        if (PACK_MCMETA_PATH.equals(path)) {
            return () -> resources.get(new Identifier(id.getNamespace(), PACK_MCMETA_PATH)).getAsStream();
        } else if (PACK_ICON_PATH.equals(path)) {
            return () -> resources.get(new Identifier(id.getNamespace(), PACK_ICON_PATH)).getAsStream();
        }
        return () -> new NullInputStream(0);
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        if (hasResource(type, id)) return () -> resources.get(id).getAsStream();
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer visitor) {
        resources.forEach((key, value) -> {
            if (!(key.getPath().startsWith(prefix) && resources.get(key).belongsTo(type))) return;
            visitor.accept(key, value::getAsStream);
        });
    }

    @Nullable
    @Override
    public <T> T parseMetadata(@NotNull ResourceMetadataReader<T> reader) throws IOException {
        Identifier metaId = new Identifier(id.getNamespace(), PACK_MCMETA_PATH);
        if (!resources.containsKey(metaId))
            throw new FileNotFoundException("Pack mcmeta was not found for pack: " + id);
        if (resources.get(metaId) instanceof MetaResource metaResource) {
            JsonObject meta = metaResource.getAsJsonObject();
            return meta.has(reader.getKey()) ? reader.fromJson(meta.getAsJsonObject(reader.getKey())) : null;
        } else {
            throw new FileNotFoundException("Invalid metadata for pack: " + id);
        }
    }

    public boolean hasResource(@NotNull ResourceType type, @NotNull Identifier id) {
        return resources.containsKey(id) && resources.get(id).belongsTo(type);
    }

    @Override
    public Set<String> getNamespaces(@NotNull ResourceType type) {
        return switch (type) {
            case CLIENT_RESOURCES -> assetNamespaces;
            case SERVER_DATA -> dataNamespaces;
        };
    }

    @Override
    public String getName() {
        return profile.getName().getAsString();
    }

    @Override
    public void close() {

    }

    public void validate() {
        try {
            if (id == null) throw new PackGenerationException("Respro pack id is null");
            if (profile == null) throw new PackGenerationException("Respro pack profile is null");
            profile.validate();
        } catch (Exception e) {
            LOGGER.error("Failed to generate respro pack: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void dump() {
        if (enabledDumpMode) {
            DUMP.dump(this);
        }
    }

    public Identifier getId() {
        return id;
    }

    public PackProfileResource getProfile() {
        return profile;
    }
}