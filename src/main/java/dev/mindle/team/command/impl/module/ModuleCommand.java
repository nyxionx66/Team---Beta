package dev.mindle.team.command.impl.module;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.ModuleManager;
import dev.mindle.team.util.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class ModuleCommand extends Command {
    public ModuleCommand() {
        super("module", "Manage modules", CommandManager.PREFIX + "module <list|enable|disable|toggle|info> [module] [category]", "mod", "m");
    }

    @Override
    public void execute(String[] args) {
        validateArgs(args, 1);

        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        if (moduleManager == null) {
            ChatUtil.sendMessage("§cModule system not available");
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "list":
                if (args.length > 1) {
                    listModulesByCategory(args[1]);
                } else {
                    listAllModules();
                }
                break;
            case "enable":
                validateArgs(args, 2);
                enableModule(args[1]);
                break;
            case "disable":
                validateArgs(args, 2);
                disableModule(args[1]);
                break;
            case "toggle":
                validateArgs(args, 2);
                toggleModule(args[1]);
                break;
            case "info":
                validateArgs(args, 2);
                showModuleInfo(args[1]);
                break;
            case "category":
                if (args.length > 2) {
                    manageCategoryModules(args[1], args[2]);
                } else {
                    listCategories();
                }
                break;
            default:
                ChatUtil.sendMessage("§cInvalid action: §f" + action);
                ChatUtil.sendMessage("§7Usage: §f" + getUsage());
        }
    }

    private void listAllModules() {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " Modules:");
        ChatUtil.sendMessage("§7Total: §f" + moduleManager.getTotalModules() + " §7| Enabled: §a" + moduleManager.getEnabledCount() + " §7| Disabled: §c" + moduleManager.getDisabledCount());
        ChatUtil.sendMessage("§7─────────────────────────");

        for (ModuleCategory category : ModuleCategory.values()) {
            List<Module> modules = moduleManager.getModulesByCategory(category);
            if (!modules.isEmpty()) {
                ChatUtil.sendMessage(category.getDisplayName() + " §7(" + modules.size() + "):");
                for (Module module : modules) {
                    String status = module.isEnabled() ? "§a✓" : "§c✗";
                    String keybind = module.hasKeybind() ? " §7[" + module.getKeybind().getKeyName() + "]" : "";
                    ChatUtil.sendMessage("  " + status + " §f" + module.getName() + keybind);
                }
            }
        }
    }

    private void listModulesByCategory(String categoryName) {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        
        ModuleCategory category = null;
        for (ModuleCategory cat : ModuleCategory.values()) {
            if (cat.getName().equalsIgnoreCase(categoryName)) {
                category = cat;
                break;
            }
        }

        if (category == null) {
            ChatUtil.sendMessage("§cInvalid category: §f" + categoryName);
            listCategories();
            return;
        }

        List<Module> modules = moduleManager.getModulesByCategory(category);
        ChatUtil.sendMessage(category.getDisplayName() + " §7Modules (" + modules.size() + "):");
        ChatUtil.sendMessage("§7─────────────────────────");

        for (Module module : modules) {
            String status = module.isEnabled() ? "§a✓" : "§c✗";
            String keybind = module.hasKeybind() ? " §7[" + module.getKeybind().getKeyName() + "]" : "";
            ChatUtil.sendMessage("  " + status + " §f" + module.getName() + " §7- " + module.getDescription() + keybind);
        }
    }

    private void listCategories() {
        ChatUtil.sendMessage("§bModule Categories:");
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        
        for (ModuleCategory category : ModuleCategory.values()) {
            int total = moduleManager.getCategoryCount(category);
            int enabled = moduleManager.getEnabledCategoryCount(category);
            ChatUtil.sendMessage("  " + category.getDisplayName() + " §7- " + enabled + "/" + total + " enabled");
        }
    }

    private void enableModule(String name) {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        Module module = moduleManager.getModule(name);
        
        if (module == null) {
            ChatUtil.sendMessage("§cModule not found: §f" + name);
            return;
        }

        if (module.isEnabled()) {
            ChatUtil.sendMessage("§7Module §f" + module.getName() + " §7is already enabled");
        } else {
            module.enable();
        }
    }

    private void disableModule(String name) {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        Module module = moduleManager.getModule(name);
        
        if (module == null) {
            ChatUtil.sendMessage("§cModule not found: §f" + name);
            return;
        }

        if (!module.isEnabled()) {
            ChatUtil.sendMessage("§7Module §f" + module.getName() + " §7is already disabled");
        } else {
            module.disable();
        }
    }

    private void toggleModule(String name) {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        Module module = moduleManager.getModule(name);
        
        if (module == null) {
            ChatUtil.sendMessage("§cModule not found: §f" + name);
            return;
        }

        module.toggle();
    }

    private void showModuleInfo(String name) {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        Module module = moduleManager.getModule(name);
        
        if (module == null) {
            ChatUtil.sendMessage("§cModule not found: §f" + name);
            return;
        }

        ChatUtil.sendMessage("§b" + module.getName() + " Information:");
        ChatUtil.sendMessage("§7─────────────────────────");
        ChatUtil.sendMessage("§7Description: §f" + module.getDescription());
        ChatUtil.sendMessage("§7Category: §f" + module.getCategory().getDisplayName());
        ChatUtil.sendMessage("§7Status: " + (module.isEnabled() ? "§aEnabled" : "§cDisabled"));
        
        if (module.hasKeybind()) {
            ChatUtil.sendMessage("§7Keybind: §f" + module.getKeybind().getKeyName() + " §7(" + module.getKeybindType().getName() + ")");
        }
        
        if (!module.getSettings().isEmpty()) {
            ChatUtil.sendMessage("§7Settings: §f" + module.getSettings().size());
            module.getSettings().forEach(setting -> {
                ChatUtil.sendMessage("  §f" + setting.getName() + " §7= §e" + setting.getValueAsString());
            });
        }
    }

    private void manageCategoryModules(String action, String categoryName) {
        ModuleManager moduleManager = Team.getInstance().getModuleManager();
        
        ModuleCategory category = null;
        for (ModuleCategory cat : ModuleCategory.values()) {
            if (cat.getName().equalsIgnoreCase(categoryName)) {
                category = cat;
                break;
            }
        }

        if (category == null) {
            ChatUtil.sendMessage("§cInvalid category: §f" + categoryName);
            return;
        }

        switch (action.toLowerCase()) {
            case "enable":
                moduleManager.enableCategory(category);
                ChatUtil.sendMessage("§aEnabled all modules in category: §f" + category.getDisplayName());
                break;
            case "disable":
                moduleManager.disableCategory(category);
                ChatUtil.sendMessage("§cDisabled all modules in category: §f" + category.getDisplayName());
                break;
            default:
                ChatUtil.sendMessage("§cInvalid category action: §f" + action);
                ChatUtil.sendMessage("§7Use: enable or disable");
        }
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String action : new String[]{"list", "enable", "disable", "toggle", "info", "category"}) {
                if (action.startsWith(partial)) {
                    suggestions.add(action);
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("category")) {
                String partial = args[1].toLowerCase();
                for (ModuleCategory category : ModuleCategory.values()) {
                    if (category.getName().toLowerCase().startsWith(partial)) {
                        suggestions.add(category.getName());
                    }
                }
            } else if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("disable") || 
                      args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("info")) {
                ModuleManager moduleManager = Team.getInstance().getModuleManager();
                if (moduleManager != null) {
                    String partial = args[1].toLowerCase();
                    for (String moduleName : moduleManager.getModuleNames()) {
                        if (moduleName.toLowerCase().startsWith(partial)) {
                            suggestions.add(moduleName);
                        }
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("category")) {
            String partial = args[2].toLowerCase();
            for (String action : new String[]{"enable", "disable"}) {
                if (action.startsWith(partial)) {
                    suggestions.add(action);
                }
            }
        }

        return suggestions;
    }
}