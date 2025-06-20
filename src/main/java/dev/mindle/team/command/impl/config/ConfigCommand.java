package dev.mindle.team.command.impl.config;

import java.util.ArrayList;
import java.util.List;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.util.ChatUtil;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("config", "Manage mod configuration", CommandManager.PREFIX + "config <get|set|list|reset> [key] [value]", "cfg", "settings");
    }

    @Override
    public void execute(String[] args) {
        validateArgs(args, 1);

        // Check if config is available with better error handling
        TeamConfig config = null;
        try {
            config = Team.getInstance().getConfig();
        } catch (Exception e) {
            Team.LOGGER.error("Error accessing config", e);
        }
        
        if (config == null) {
            ChatUtil.sendMessage("§cConfiguration system not available. Attempting to initialize...");
            try {
                // Force initialization
                Team.getInstance().setConfig(new TeamConfig());
                config = Team.getInstance().getConfig();
                ChatUtil.sendMessage("§aConfiguration system initialized successfully!");
            } catch (Exception e) {
                ChatUtil.sendMessage("§cFailed to initialize configuration system: " + e.getMessage());
                return;
            }
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "list":
                listConfig();
                break;
            case "get":
                validateArgs(args, 2);
                getConfig(args[1]);
                break;
            case "set":
                validateArgs(args, 3);
                setConfig(args[1], args[2]);
                break;
            case "reset":
                resetConfig();
                break;
            default:
                ChatUtil.sendMessage("§cInvalid action: §f" + action);
                ChatUtil.sendMessage("§7Usage: §f" + getUsage());
        }
    }

    private void listConfig() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " Configuration:");
        ChatUtil.sendMessage("§7─────────────────────────");

        // Categorize config for better display
        showConfigCategory("§9General Settings:", "hud", "debug", "notifications");
        showConfigCategory("§aCommand Settings:", "autocomplete", "commandPrefix", "maxChatHistory");
        showConfigCategory("§eRender Settings:", "renderDistance", "showCoordinates", "showFPS");

        ChatUtil.sendMessage("§7─────────────────────────");
        ChatUtil.sendMessage("§7Use §f" + CommandManager.PREFIX + "config get/set <key> §7to modify");
    }

    private void showConfigCategory(String categoryName, String... keys) {
        ChatUtil.sendMessage(categoryName);
        for (String key : keys) {
            try {
                TeamConfig config = Team.getInstance().getConfig();
                if (config != null) {
                    Object value = config.get(key);
                    if (value != null) {
                        String valueStr = formatConfigValue(value);
                        ChatUtil.sendMessage("  §f" + key + " §7= " + valueStr);
                    } else {
                        ChatUtil.sendMessage("  §f" + key + " §7= §cnot set");
                    }
                } else {
                    ChatUtil.sendMessage("  §f" + key + " §7= §cconfig unavailable");
                }
            } catch (Exception e) {
                ChatUtil.sendMessage("  §f" + key + " §7= §cerror accessing");
                Team.LOGGER.debug("Error accessing config key: " + key, e);
            }
        }
    }

    private String formatConfigValue(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value ? "§atrue" : "§cfalse";
        } else if (value instanceof Number) {
            return "§e" + value;
        } else {
            return "§f" + value;
        }
    }

    private void getConfig(String key) {
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config != null) {
                Object value = config.get(key);
                if (value != null) {
                    ChatUtil.sendMessage("§f" + key + " §7= " + formatConfigValue(value));
                } else {
                    ChatUtil.sendMessage("§cConfiguration key not found: §f" + key);
                }
            } else {
                ChatUtil.sendMessage("§cConfiguration system not available");
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError accessing configuration key: §f" + key);
            Team.LOGGER.error("Error accessing config key: " + key, e);
        }
    }

    private void setConfig(String key, String value) {
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config == null) {
                ChatUtil.sendMessage("§cConfiguration system not available");
                return;
            }
            
            // Try to parse as different types
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                config.setBoolean(key, Boolean.parseBoolean(value));
            } else if (value.matches("-?\\d+")) {
                config.setInt(key, Integer.parseInt(value));
            } else if (value.matches("-?\\d*\\.\\d+")) {
                config.setDouble(key, Double.parseDouble(value));
            } else {
                config.setString(key, value);
            }

            Object newValue = config.get(key);
            if (newValue != null) {
                ChatUtil.sendMessage("§7Set §f" + key + " §7to " + formatConfigValue(newValue));
            } else {
                ChatUtil.sendMessage("§cFailed to set configuration value");
            }
        } catch (NumberFormatException e) {
            ChatUtil.sendMessage("§cInvalid value format for: §f" + key);
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError setting configuration: §f" + e.getMessage());
            Team.LOGGER.error("Error setting config key: " + key, e);
        }
    }

    private void resetConfig() {
        Team.getInstance().getConfig().reset();
        ChatUtil.sendMessage("§aConfiguration reset to defaults!");
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String action : new String[]{"list", "get", "set", "reset"}) {
                if (action.startsWith(partial)) {
                    suggestions.add(action);
                }
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("set"))) {
            if (Team.getInstance().getConfig() != null) {
                String partial = args[1].toLowerCase();
                for (String key : Team.getInstance().getConfig().getKeys()) {
                    if (key.toLowerCase().startsWith(partial)) {
                        suggestions.add(key);
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            // Suggest values based on key type
            if (Team.getInstance().getConfig() != null) {
                String key = args[1];
                Object currentValue = Team.getInstance().getConfig().get(key);
                if (currentValue instanceof Boolean) {
                    suggestions.add("true");
                    suggestions.add("false");
                }
            }
        }

        return suggestions;
    }
}