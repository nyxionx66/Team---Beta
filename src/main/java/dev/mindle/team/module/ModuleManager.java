package dev.mindle.team.module;

import dev.mindle.team.Team;
import dev.mindle.team.module.keybind.KeybindManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ModuleManager {
    private final Map<String, Module> modules = new ConcurrentHashMap<>();
    private final Map<ModuleCategory, List<Module>> modulesByCategory = new ConcurrentHashMap<>();
    private final KeybindManager keybindManager;

    public ModuleManager() {
        this.keybindManager = new KeybindManager();
        
        // Initialize category lists
        for (ModuleCategory category : ModuleCategory.values()) {
            modulesByCategory.put(category, new CopyOnWriteArrayList<>());
        }
        
        Team.LOGGER.info("Initializing ModuleManager");
        
        // Register modules
        registerModules();
        
        // Load all module states and settings
        loadAllModules();
        
        Team.LOGGER.info("ModuleManager initialized with {} modules", modules.size());
    }

    private void registerModules() {
        try {
            // Register modules from each category
            registerCombatModules();
            registerMovementModules();
            registerRenderModules();
            registerPlayerModules();
            registerWorldModules();
            registerMiscModules();
            registerClientModules();
            
        } catch (Exception e) {
            Team.LOGGER.error("Error registering modules", e);
        }
    }

    private void registerCombatModules() {
        try {
            registerModule(new dev.mindle.team.module.impl.combat.KillAura());
        } catch (Exception e) {
            Team.LOGGER.error("Error registering combat modules", e);
        }
        Team.LOGGER.debug("Registered {} combat modules", getModulesByCategory(ModuleCategory.COMBAT).size());
    }

    private void registerMovementModules() {
        try {
            registerModule(new dev.mindle.team.module.impl.movement.Speed());
            registerModule(new dev.mindle.team.module.impl.movement.Fly());
        } catch (Exception e) {
            Team.LOGGER.error("Error registering movement modules", e);
        }
        Team.LOGGER.debug("Registered {} movement modules", getModulesByCategory(ModuleCategory.MOVEMENT).size());
    }

    private void registerRenderModules() {
        try {
            registerModule(new dev.mindle.team.module.impl.render.Fullbright());
        } catch (Exception e) {
            Team.LOGGER.error("Error registering render modules", e);
        }
        Team.LOGGER.debug("Registered {} render modules", getModulesByCategory(ModuleCategory.RENDER).size());
    }

    private void registerPlayerModules() {
        try {
            registerModule(new dev.mindle.team.module.impl.player.AutoSprint());
        } catch (Exception e) {
            Team.LOGGER.error("Error registering player modules", e);
        }
        Team.LOGGER.debug("Registered {} player modules", getModulesByCategory(ModuleCategory.PLAYER).size());
    }

    private void registerWorldModules() {
        // Will be populated with world modules
        Team.LOGGER.debug("Registered {} world modules", getModulesByCategory(ModuleCategory.WORLD).size());
    }

    private void registerMiscModules() {
        try {
            registerModule(new dev.mindle.team.module.impl.misc.AutoClicker());
        } catch (Exception e) {
            Team.LOGGER.error("Error registering misc modules", e);
        }
        Team.LOGGER.debug("Registered {} misc modules", getModulesByCategory(ModuleCategory.MISC).size());
    }

    public void registerModule(Module module) {
        if (module == null) {
            Team.LOGGER.warn("Attempted to register null module");
            return;
        }

        String name = module.getName().toLowerCase();
        if (modules.containsKey(name)) {
            Team.LOGGER.warn("Module already registered: {}", module.getName());
            return;
        }

        modules.put(name, module);
        modulesByCategory.get(module.getCategory()).add(module);

        // Register keybind if module has one
        if (module.hasKeybind()) {
            keybindManager.registerKeybind(
                module.getName().toLowerCase(),
                module.getDefaultKey(),
                module.getKeybindType(),
                module::toggle
            );
        }

        Team.LOGGER.debug("Registered module: {} in category: {}", module.getName(), module.getCategory().getName());
    }

    public void unregisterModule(String name) {
        Module module = modules.remove(name.toLowerCase());
        if (module != null) {
            modulesByCategory.get(module.getCategory()).remove(module);
            
            // Disable if enabled
            if (module.isEnabled()) {
                module.disable();
            }
            
            // Unregister keybind
            if (module.hasKeybind()) {
                keybindManager.unregisterKeybind(module.getName().toLowerCase());
            }
            
            Team.LOGGER.debug("Unregistered module: {}", module.getName());
        }
    }

    public Module getModule(String name) {
        return modules.get(name.toLowerCase());
    }

    public <T extends Module> T getModule(Class<T> moduleClass) {
        return modules.values().stream()
                .filter(moduleClass::isInstance)
                .map(moduleClass::cast)
                .findFirst()
                .orElse(null);
    }

    public List<Module> getAllModules() {
        return new ArrayList<>(modules.values());
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return new ArrayList<>(modulesByCategory.getOrDefault(category, new ArrayList<>()));
    }

    public List<Module> getEnabledModules() {
        return modules.values().stream()
                .filter(Module::isEnabled)
                .collect(Collectors.toList());
    }

    public List<Module> getDisabledModules() {
        return modules.values().stream()
                .filter(module -> !module.isEnabled())
                .collect(Collectors.toList());
    }

    public List<Module> getModulesWithKeybinds() {
        return modules.values().stream()
                .filter(Module::hasKeybind)
                .collect(Collectors.toList());
    }

    public void enableModule(String name) {
        Module module = getModule(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = getModule(name);
        if (module != null) {
            module.disable();
        }
    }

    public void toggleModule(String name) {
        Module module = getModule(name);
        if (module != null) {
            module.toggle();
        }
    }

    public void disableAll() {
        for (Module module : getEnabledModules()) {
            module.disable();
        }
        Team.LOGGER.info("Disabled all modules");
    }

    public void enableAll() {
        for (Module module : modules.values()) {
            module.enable();
        }
        Team.LOGGER.info("Enabled all modules");
    }

    public void disableCategory(ModuleCategory category) {
        for (Module module : getModulesByCategory(category)) {
            if (module.isEnabled()) {
                module.disable();
            }
        }
        Team.LOGGER.info("Disabled all modules in category: {}", category.getName());
    }

    public void enableCategory(ModuleCategory category) {
        for (Module module : getModulesByCategory(category)) {
            if (!module.isEnabled()) {
                module.enable();
            }
        }
        Team.LOGGER.info("Enabled all modules in category: {}", category.getName());
    }

    public void loadAllModules() {
        for (Module module : modules.values()) {
            try {
                module.loadState();
                module.loadSettings();
            } catch (Exception e) {
                Team.LOGGER.error("Error loading module: " + module.getName(), e);
            }
        }
        
        // Load keybinds
        keybindManager.loadKeybinds();
        
        Team.LOGGER.debug("Loaded states and settings for {} modules", modules.size());
    }

    public void saveAllModules() {
        for (Module module : modules.values()) {
            try {
                module.saveSettings();
            } catch (Exception e) {
                Team.LOGGER.error("Error saving module: " + module.getName(), e);
            }
        }
        
        // Save keybinds
        keybindManager.saveKeybinds();
        
        Team.LOGGER.debug("Saved settings for {} modules", modules.size());
    }

    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    // Statistics
    public int getTotalModules() {
        return modules.size();
    }

    public int getEnabledCount() {
        return (int) modules.values().stream().filter(Module::isEnabled).count();
    }

    public int getDisabledCount() {
        return getTotalModules() - getEnabledCount();
    }

    public int getCategoryCount(ModuleCategory category) {
        return modulesByCategory.getOrDefault(category, new ArrayList<>()).size();
    }

    public int getEnabledCategoryCount(ModuleCategory category) {
        return (int) getModulesByCategory(category).stream().filter(Module::isEnabled).count();
    }

    public Map<ModuleCategory, Integer> getCategoryStatistics() {
        Map<ModuleCategory, Integer> stats = new HashMap<>();
        for (ModuleCategory category : ModuleCategory.values()) {
            stats.put(category, getCategoryCount(category));
        }
        return stats;
    }

    public List<String> getModuleNames() {
        return modules.values().stream()
                .map(Module::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    public boolean hasModule(String name) {
        return modules.containsKey(name.toLowerCase());
    }
}