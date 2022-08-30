package io.github.lucaargolo.structureworld.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;

import static io.github.lucaargolo.structureworld.core.Mod.CONFIG;
import static io.github.lucaargolo.structureworld.core.Mod.LOGGER;

public class ModPreLaunch implements PreLaunchEntrypoint {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void onPreLaunch() {
        File configFolder = Mod.configPath.toFile();
        File configFile = new File(Mod.configPath + File.separator + "structureworld.json");

        LOGGER.info("Trying to read config file...");
        try {
            if (!configFolder.exists()) {
                LOGGER.info("No config folder found, creating a new one...");
                if (!configFolder.mkdirs())
                    throw new Exception("Failed while creating config folder.");
            }
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

    }
}