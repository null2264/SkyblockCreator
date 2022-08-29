package io.github.lucaargolo.structureworld.core;

import com.google.common.collect.Maps;
import io.github.lucaargolo.structureworld.StructureChunkGenerator;
import io.github.lucaargolo.structureworld.command.StructureWorldCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.WorldPreset;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Mod implements ModInitializer {

    public static final String MOD_ID = "structureworld";
    public static final Logger LOGGER = LogManager.getLogger("Structure World");
    public static final HashMap<String, StructureTemplate> STRUCTURES = Maps.newHashMap();
    public static ModConfig CONFIG;
    public static List<RegistryKey<WorldPreset>> TO_BE_DISPLAYED = null;
    public static String OVERRIDED_LEVEL_TYPE = null;

    public static RegistryKey<WorldPreset> registryKeyOf(String id) {
        return RegistryKey.of(Registry.WORLD_PRESET_KEY, new Identifier(id));
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(MOD_ID, "structure_chunk_generator"), StructureChunkGenerator.CODEC);
        Path configPath = FabricLoader.getInstance().getConfigDir();
        File structuresFolder = new File(configPath + File.separator + "structureworld" + File.separator + "structures");

        LOGGER.info("Trying to read structures folder...");
        try {
            if (!structuresFolder.exists()) {
                LOGGER.info("No structures folder found, creating a new one...");
                if (structuresFolder.mkdirs()) {
                    Path builtinStructuresFolderPath = FabricLoader.getInstance().getModContainer("structureworld").orElseThrow(() -> new Exception("Couldn't find ModContainer")).findPath("structures").orElseThrow();
                    List<Path> builtinStructuresPath = Files.walk(builtinStructuresFolderPath).filter(Files::isRegularFile).toList();
                    for (Path builtinStructurePath : builtinStructuresPath) {
                        InputStream builtinStructureInputStream = Files.newInputStream(builtinStructurePath);
                        File outputFile = new File(structuresFolder, builtinStructurePath.getFileName().toString());
                        if (outputFile.createNewFile()) {
                            FileOutputStream structureOutputStream = new FileOutputStream(outputFile);
                            IOUtils.copy(builtinStructureInputStream, structureOutputStream);
                        }
                    }
                    LOGGER.info("Successfully created structures folder.");
                } else {
                    throw new Exception("Failed while creating structures folder.");
                }
            }
            if (structuresFolder.exists()) {
                LOGGER.info("Found structures folder, loading structures...");
                List<File> files = Arrays.stream(Objects.requireNonNull(structuresFolder.listFiles(pathname -> pathname.exists() && pathname.isFile() && pathname.getName().endsWith(".nbt")))).toList();
                for (File file : files) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    NbtCompound structure = NbtIo.readCompressed(dataInputStream);
                    StructureTemplate loadedStructure = new StructureTemplate();
                    loadedStructure.readNbt(structure);
                    STRUCTURES.put(file.getName().replace(".nbt", ""), loadedStructure);
                }
                LOGGER.info("Successfully loaded structures folder with " + STRUCTURES.size() + " structure.");
            }

        } catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the structures folder!", exception);
        }

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, env) -> StructureWorldCommand.register(dispatcher)));

    }

}