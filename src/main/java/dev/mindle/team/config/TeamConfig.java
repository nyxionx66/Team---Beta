package dev.mindle.team.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import dev.mindle.team.Team;
import net.fabricmc.loader.api.FabricLoader;

public class TeamConfig {
    private static final String CONFIG_FILE = "team.json";
    private final File configFile;
    private final Gson gson;
    private Map<String, Object> config;

    public TeamConfig() {
        this.configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_FILE);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.config = new HashMap<>();
        
        // Set default values
        setDefaults();
        
        // Load existing config
        load();
    }

    private void setDefaults() {
        config.put("hud", true);
        config.put("debug", false);
        config.put("notifications", true);
        config.put("autocomplete", true);
        config.put("commandPrefix", ".");
        config.put("maxChatHistory", 100);
        config.put("renderDistance", 16);
        config.put("showCoordinates", true);
        config.put("showFPS", false);
    }

    public void load() {
        if (!configFile.exists()) {
            save();
            return;
        }

        try (FileReader reader = new FileReader(configFile)) {
            Map<String, Object> loadedConfig = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
            if (loadedConfig != null) {
                // Merge with defaults to ensure all keys exist
                for (Map.Entry<String, Object> entry : loadedConfig.entrySet()) {
                    config.put(entry.getKey(), entry.getValue());
                }
            }
            Team.LOGGER.info("Configuration loaded from {}", configFile.getName());
        } catch (IOException e) {
            Team.LOGGER.error("Failed to load configuration", e);
        }
    }

    public void save() {
        try {
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(configFile)) {
                gson.toJson(config, writer);
            }
            Team.LOGGER.debug("Configuration saved to {}", configFile.getName());
        } catch (IOException e) {
            Team.LOGGER.error("Failed to save configuration", e);
        }
    }

    public Object get(String key) {
        return config.get(key);
    }

    public String getString(String key) {
        Object value = config.get(key);
        return value != null ? value.toString() : null;
    }

    public boolean getBoolean(String key) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    public int getInt(String key) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    public double getDouble(String key) {
        Object value = config.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    public void setString(String key, String value) {
        config.put(key, value);
        save();
    }

    public void setBoolean(String key, boolean value) {
        config.put(key, value);
        save();
    }

    public void setInt(String key, int value) {
        config.put(key, value);
        save();
    }

    public void setDouble(String key, double value) {
        config.put(key, value);
        save();
    }

    public Set<String> getKeys() {
        return config.keySet();
    }

    public void reset() {
        config.clear();
        setDefaults();
        save();
    }

    // Additional methods that might be called by other parts of the system
    public String getBooleanString(String key) {
        boolean value = getBoolean(key);
        return value ? "true" : "false";
    }

    public void set(String key, Object value) {
        config.put(key, value);
        save();
    }

    public boolean hasKey(String key) {
        return config.containsKey(key);
    }

    public void removeKey(String key) {
        config.remove(key);
        save();
    }
}