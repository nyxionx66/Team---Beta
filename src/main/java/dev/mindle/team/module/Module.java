package dev.mindle.team.module;

import dev.mindle.team.Team;
import dev.mindle.team.module.keybind.Keybind;
import dev.mindle.team.module.keybind.KeybindType;
import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.util.ChatUtil;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public abstract class Module {
    protected final String name;
    protected final String description;
    protected final ModuleCategory category;
    protected final int defaultKey;
    protected final KeybindType keybindType;
    
    protected boolean enabled = false;
    protected Keybind keybind;
    protected final List<Setting<?>> settings = new CopyOnWriteArrayList<>();

    public Module(String name, String description, ModuleCategory category) {
        this(name, description, category, GLFW.GLFW_KEY_UNKNOWN, KeybindType.TOGGLE);
    }

    public Module(String name, String description, ModuleCategory category, int defaultKey) {
        this(name, description, category, defaultKey, KeybindType.TOGGLE);
    }

    public Module(String name, String description, ModuleCategory category, int defaultKey, KeybindType keybindType) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.defaultKey = defaultKey;
        this.keybindType = keybindType;
        
        // Initialize settings first
        initializeSettings();
        
        // Setup keybind
        setupKeybind();
        
        Team.LOGGER.debug("Initialized module: {} in category: {}", name, category.getName());
    }

    protected abstract void initializeSettings();
    
    protected abstract void onEnable();
    protected abstract void onDisable();

    private void setupKeybind() {
        if (defaultKey != GLFW.GLFW_KEY_UNKNOWN) {
            if (keybindType == KeybindType.TOGGLE) {
                this.keybind = new Keybind(
                    name + "_toggle",
                    defaultKey,
                    keybindType,
                    this::toggle
                );
            } else {
                this.keybind = new Keybind(
                    name + "_hold",
                    defaultKey,
                    keybindType,
                    this::enable,
                    this::disable
                );
            }
        }
    }

    public void enable() {
        if (!enabled) {
            enabled = true;
            try {
                onEnable();
                
                // Register event listeners if this module has them
                if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
                    Team.getInstance().getEventBus().register(this);
                }
                
                ChatUtil.sendMessage("§a[+] §f" + name + " §aenabled");
                Team.LOGGER.debug("Enabled module: {}", name);
            } catch (Exception e) {
                enabled = false;
                ChatUtil.sendMessage("§c[-] §fFailed to enable " + name + ": " + e.getMessage());
                Team.LOGGER.error("Failed to enable module: " + name, e);
            }
            saveState();
        }
    }

    public void disable() {
        if (enabled) {
            enabled = false;
            try {
                onDisable();
                
                // Unregister event listeners
                if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
                    Team.getInstance().getEventBus().unregister(this);
                }
                
                ChatUtil.sendMessage("§c[-] §f" + name + " §cdisabled");
                Team.LOGGER.debug("Disabled module: {}", name);
            } catch (Exception e) {
                ChatUtil.sendMessage("§c[-] §fError disabling " + name + ": " + e.getMessage());
                Team.LOGGER.error("Failed to disable module: " + name, e);
            }
            saveState();
        }
    }

    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    // Setting management
    protected <T> Setting<T> addSetting(Setting<T> setting) {
        settings.add(setting);
        setting.load(); // Load saved value
        return setting;
    }

    public List<Setting<?>> getSettings() {
        return new CopyOnWriteArrayList<>(settings);
    }

    public Setting<?> getSetting(String name) {
        return settings.stream()
                .filter(setting -> setting.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void saveSettings() {
        for (Setting<?> setting : settings) {
            setting.save();
        }
    }

    public void loadSettings() {
        for (Setting<?> setting : settings) {
            setting.load();
        }
    }

    public void resetSettings() {
        for (Setting<?> setting : settings) {
            setting.resetToDefault();
        }
        saveSettings();
    }

    // State management
    private void saveState() {
        try {
            String configKey = "module." + name.toLowerCase() + ".enabled";
            Team.getInstance().getConfig().setBoolean(configKey, enabled);
        } catch (Exception e) {
            Team.LOGGER.error("Failed to save state for module: " + name, e);
        }
    }

    public void loadState() {
        try {
            String configKey = "module." + name.toLowerCase() + ".enabled";
            if (Team.getInstance().getConfig().hasKey(configKey)) {
                boolean savedState = Team.getInstance().getConfig().getBoolean(configKey);
                if (savedState && !enabled) {
                    enable();
                } else if (!savedState && enabled) {
                    disable();
                }
            }
        } catch (Exception e) {
            Team.LOGGER.error("Failed to load state for module: " + name, e);
        }
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Keybind getKeybind() {
        return keybind;
    }

    public int getDefaultKey() {
        return defaultKey;
    }

    public KeybindType getKeybindType() {
        return keybindType;
    }

    public boolean hasKeybind() {
        return keybind != null;
    }

    public String getDisplayName() {
        return enabled ? "§a" + name : "§7" + name;
    }

    public String getFullInfo() {
        StringBuilder info = new StringBuilder();
        info.append("§b").append(name).append(" §7- ").append(description);
        info.append("\n§7Category: §f").append(category.getDisplayName());
        info.append("\n§7Status: ").append(enabled ? "§aEnabled" : "§cDisabled");
        
        if (hasKeybind()) {
            info.append("\n§7Keybind: §f").append(keybind.getKeyName()).append(" §7(").append(keybindType.getName()).append(")");
        }
        
        if (!settings.isEmpty()) {
            info.append("\n§7Settings: §f").append(settings.size());
        }
        
        return info.toString();
    }
}