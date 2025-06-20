package dev.mindle.team.module.keybind;

import dev.mindle.team.Team;
import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;

public class KeybindManager {
    private final Map<String, Keybind> keybinds = new ConcurrentHashMap<>();
    private final List<Keybind> updateList = new CopyOnWriteArrayList<>();

    public KeybindManager() {
        Team.LOGGER.info("Initializing KeybindManager");
        
        // Register this manager to receive update events
        if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
            Team.getInstance().getEventBus().register(this);
        }
    }

    public Keybind registerKeybind(String name, int defaultKey, KeybindType type, Runnable onPress) {
        return registerKeybind(name, defaultKey, type, onPress, null);
    }

    public Keybind registerKeybind(String name, int defaultKey, KeybindType type, Runnable onPress, Runnable onRelease) {
        Keybind keybind = new Keybind(name, defaultKey, type, onPress, onRelease);
        keybinds.put(name.toLowerCase(), keybind);
        updateList.add(keybind);
        
        Team.LOGGER.debug("Registered keybind: {} with key {} ({})", name, defaultKey, type.getName());
        return keybind;
    }

    public void unregisterKeybind(String name) {
        Keybind removed = keybinds.remove(name.toLowerCase());
        if (removed != null) {
            updateList.remove(removed);
            Team.LOGGER.debug("Unregistered keybind: {}", name);
        }
    }

    public Keybind getKeybind(String name) {
        return keybinds.get(name.toLowerCase());
    }

    public List<Keybind> getAllKeybinds() {
        return new CopyOnWriteArrayList<>(keybinds.values());
    }

    public Map<String, Keybind> getKeybinds() {
        return new ConcurrentHashMap<>(keybinds);
    }

    public List<Keybind> getKeybindsByType(KeybindType type) {
        return keybinds.values().stream()
                .filter(keybind -> keybind.getType() == type)
                .toList();
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        // Update all keybinds
        for (Keybind keybind : updateList) {
            try {
                keybind.update();
            } catch (Exception e) {
                Team.LOGGER.error("Error updating keybind: " + keybind.getName(), e);
            }
        }
    }

    public boolean isKeyUsed(int key) {
        return keybinds.values().stream().anyMatch(keybind -> keybind.getKey() == key);
    }

    public List<Keybind> getKeybindsForKey(int key) {
        return keybinds.values().stream()
                .filter(keybind -> keybind.getKey() == key)
                .toList();
    }

    public void loadKeybinds() {
        // Load keybind configurations from config
        try {
            for (Map.Entry<String, Keybind> entry : keybinds.entrySet()) {
                String configKey = "keybind." + entry.getKey();
                if (Team.getInstance().getConfig().hasKey(configKey)) {
                    int savedKey = Team.getInstance().getConfig().getInt(configKey);
                    entry.getValue().setKey(savedKey);
                    Team.LOGGER.debug("Loaded keybind {} with key {}", entry.getKey(), savedKey);
                }
            }
        } catch (Exception e) {
            Team.LOGGER.error("Error loading keybinds from config", e);
        }
    }

    public void saveKeybinds() {
        // Save keybind configurations to config
        try {
            for (Map.Entry<String, Keybind> entry : keybinds.entrySet()) {
                String configKey = "keybind." + entry.getKey();
                Team.getInstance().getConfig().setInt(configKey, entry.getValue().getKey());
            }
            Team.LOGGER.debug("Saved {} keybinds to config", keybinds.size());
        } catch (Exception e) {
            Team.LOGGER.error("Error saving keybinds to config", e);
        }
    }

    public void resetAllKeybinds() {
        for (Keybind keybind : keybinds.values()) {
            keybind.resetToDefault();
        }
        saveKeybinds();
        Team.LOGGER.info("Reset all keybinds to defaults");
    }

    public int getTotalKeybinds() {
        return keybinds.size();
    }

    public int getKeybindCount(KeybindType type) {
        return (int) keybinds.values().stream().filter(k -> k.getType() == type).count();
    }
}