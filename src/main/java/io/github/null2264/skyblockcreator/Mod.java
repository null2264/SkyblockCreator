package io.github.null2264.skyblockcreator;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.null2264.skyblockcreator.command.StructureWorldCommand;
import io.github.null2264.skyblockcreator.core.ModConfig;
import io.github.null2264.skyblockcreator.worldgen.StructureChunkGenerator;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.structure.Structure;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class Mod implements ModInitializer {

    public static final String MOD_ID = "skyblockcreator";
    public static final Logger LOGGER = LogManager.getLogger("Skyblock Creator");
    public static final HashMap<String, Structure> STRUCTURES = Maps.newHashMap();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static ModConfig CONFIG;

    @Override
    public void onInitialize() {
        Registry.register(Registry.CHUNK_GENERATOR, new Identifier(MOD_ID, "structure_chunk_generator"), StructureChunkGenerator.CODEC);

        Path configPath = FabricLoader.getInstance().getConfigDir();
        File structuresFolder = new File(configPath + File.separator + Mod.MOD_ID + File.separator + "structures");
        File configFile = new File(configPath + File.separator + Mod.MOD_ID + File.separator + Mod.MOD_ID + ".json");

        LOGGER.info("Trying to read structures folder...");
        try {
            if (!structuresFolder.exists()) {
                LOGGER.info("No structures folder found, creating a new one...");
                if (structuresFolder.mkdirs()) {
                    Path builtinStructuresFolderPath = FabricLoader.getInstance().getModContainer(Mod.MOD_ID).orElseThrow(() -> new Exception("Couldn't find ModContainer")).findPath("structures").orElseThrow();
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
                File[] files = structuresFolder.listFiles(pathname -> pathname.exists() && pathname.isFile() && pathname.getName().endsWith(".nbt"));
                for (File file : files) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                    NbtCompound structure = NbtIo.readCompressed(dataInputStream);
                    Structure loadedStructure = new Structure();
                    loadedStructure.readNbt(structure);
                    STRUCTURES.put(file.getName().replace(".nbt", ""), loadedStructure);
                }
                LOGGER.info("Successfully loaded structures folder with " + STRUCTURES.size() + " structure.");
            }

        } catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the structures folder!", exception);
        }
        LOGGER.info("Trying to read config file...");
        try {
            if (configFile.createNewFile()) {
                LOGGER.info("No config file found, creating a new one...");
                String json = gson.toJson(JsonParser.parseString(gson.toJson(new ModConfig())));
                try (PrintWriter out = new PrintWriter(configFile)) {
                    out.println(json);
                }
                CONFIG = new ModConfig();
                LOGGER.info("Successfully created default config file with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
            } else {
                LOGGER.info("A config file was found, loading it..");
                CONFIG = gson.fromJson(new String(Files.readAllBytes(configFile.toPath())), ModConfig.class);
                if (CONFIG == null) {
                    throw new NullPointerException("The config file was empty.");
                } else {
                    LOGGER.info("Successfully loaded config file with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
                }
            }
        } catch (Exception exception) {
            LOGGER.error("There was an error creating/loading the config file!", exception);
            CONFIG = new ModConfig();
            LOGGER.warn("Defaulting to original config with " + CONFIG.getStructureWorldConfigs().size() + " custom structure worlds.");
        }

        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> StructureWorldCommand.register(dispatcher)));

    }

}