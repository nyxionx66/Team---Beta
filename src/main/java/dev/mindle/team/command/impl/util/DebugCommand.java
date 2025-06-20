package dev.mindle.team.command.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.config.TeamConfig;
import dev.mindle.team.event.EventBus;
import dev.mindle.team.util.ChatUtil;
import net.minecraft.client.MinecraftClient;

public class DebugCommand extends Command {
    public DebugCommand() {
        super("debug", "Debug information and tools", CommandManager.PREFIX + "debug <info|events|commands|system>", "dbg");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            showDebugMenu();
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "info":
                showDebugInfo();
                break;
            case "events":
                showEventInfo();
                break;
            case "commands":
                showCommandInfo();
                break;
            case "modules":
                showModuleInfo();
                break;
            case "system":
                showSystemInfo();
                break;
            default:
                ChatUtil.sendMessage("§cUnknown debug action: §f" + action);
                showDebugMenu();
        }
    }

    private void showDebugMenu() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " Debug Menu:");
        ChatUtil.sendMessage("§7Use: §f" + CommandManager.PREFIX + "debug <option>");
        ChatUtil.sendMessage("§7─────────────────────────");
        ChatUtil.sendMessage("§f" + CommandManager.PREFIX + "debug info §7- General debug information");
        ChatUtil.sendMessage("§f" + CommandManager.PREFIX + "debug events §7- Event system statistics");
        ChatUtil.sendMessage("§f" + CommandManager.PREFIX + "debug commands §7- Command system details");
        ChatUtil.sendMessage("§f" + CommandManager.PREFIX + "debug modules §7- Module system information");
        ChatUtil.sendMessage("§f" + CommandManager.PREFIX + "debug system §7- System performance info");
    }

    private void showDebugInfo() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " Debug Information:");
        ChatUtil.sendMessage("§7─────────────────────────");
        ChatUtil.sendMessage("§7Mod Version: §f" + Team.VERSION);
        
        // Safe access to config with null check
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config != null) {
                boolean debugMode = config.getBoolean("debug");
                ChatUtil.sendMessage("§7Debug Mode: " + (debugMode ? "§aEnabled" : "§cDisabled"));
                ChatUtil.sendMessage("§7Config File: §f" + config.getKeys().size() + " keys");
            } else {
                ChatUtil.sendMessage("§7Debug Mode: §cConfig not available");
                ChatUtil.sendMessage("§7Config File: §cNot loaded");
            }
        } catch (Exception e) {
            Team.LOGGER.error("Error accessing config in debug info", e);
            ChatUtil.sendMessage("§7Debug Mode: §cError accessing config");
        }
        
        ChatUtil.sendMessage("§7Memory Usage: §f" + getMemoryUsage());
    }

    private void showEventInfo() {
        ChatUtil.sendMessage("§9Event System Statistics:");
        
        try {
            EventBus eventBus = Team.getInstance().getEventBus();
            if (eventBus != null) {
                ChatUtil.sendMessage("§7Active Listeners: §f" + eventBus.getListenerCount());
                ChatUtil.sendMessage("§7Total Handlers: §f" + eventBus.getHandlerCount());
                ChatUtil.sendMessage("§7Event Types: §f" + eventBus.getEventTypeCount());

                ChatUtil.sendMessage("§7Handler Distribution:");
                Map<String, Integer> stats = eventBus.getEventStatistics();
                for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                    ChatUtil.sendMessage("  §f" + entry.getKey() + ": §7" + entry.getValue() + " handlers");
                }
            } else {
                ChatUtil.sendMessage("§7Event system not available");
            }
        } catch (Exception e) {
            Team.LOGGER.error("Error accessing event bus in debug info", e);
            ChatUtil.sendMessage("§7Error accessing event system");
        }
    }

    private void showCommandInfo() {
        try {
            CommandManager cmdManager = Team.getInstance().getCommandManager();
            if (cmdManager != null) {
                ChatUtil.sendMessage("§aCommand System Details:");
                ChatUtil.sendMessage("§7Total Commands: §f" + cmdManager.getTotalCommands());
                ChatUtil.sendMessage("§7Command Prefix: §f" + CommandManager.PREFIX);

                ChatUtil.sendMessage("§7Categories:");
                for (String category : cmdManager.getCategoryNames()) {
                    int count = cmdManager.getCommandsInCategory(category);
                    ChatUtil.sendMessage("  §f" + category + ": §7" + count + " commands");
                }
            } else {
                ChatUtil.sendMessage("§7Command system not available");
            }
        } catch (Exception e) {
            Team.LOGGER.error("Error accessing command manager in debug info", e);
            ChatUtil.sendMessage("§7Error accessing command system");
        }
    }

    private void showSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        MinecraftClient mc = MinecraftClient.getInstance();

        ChatUtil.sendMessage("§eSystem Performance:");
        ChatUtil.sendMessage("§7JVM Memory: §f" + (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + "MB / " + runtime.totalMemory() / 1024 / 1024 + "MB");
        ChatUtil.sendMessage("§7Available Processors: §f" + runtime.availableProcessors());

        // Get FPS using the correct method for 1.21
        String fpsInfo = "N/A";
        try {
            // Access the fps debug string if available
            if (mc.fpsDebugString != null && !mc.fpsDebugString.isEmpty()) {
                String[] parts = mc.fpsDebugString.split(" ");
                if (parts.length > 0) {
                    fpsInfo = parts[0];
                }
            }
        } catch (Exception e) {
            // Fallback if debug string is not available
            fpsInfo = "N/A";
        }
        ChatUtil.sendMessage("§7Current FPS: §f" + fpsInfo);

        ChatUtil.sendMessage("§7TPS: §f" + "20.0"); // Simplified for client-side

        if (mc.world != null) {
            ChatUtil.sendMessage("§7Loaded Chunks: §f" + mc.world.getChunkManager().getLoadedChunkCount());

            // Count entities properly
            int entityCount = 0;
            for (@SuppressWarnings("unused") net.minecraft.entity.Entity entity : mc.world.getEntities()) {
                entityCount++;
            }
            ChatUtil.sendMessage("§7Entities: §f" + entityCount);
        }
    }

    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long total = runtime.totalMemory();
        return String.format("%.1f/%.1fMB (%.1f%%)",
                used / 1024.0 / 1024.0,
                total / 1024.0 / 1024.0,
                (used * 100.0) / total);
    }

    private void showModuleInfo() {
        try {
            var moduleManager = Team.getInstance().getModuleManager();
            if (moduleManager != null) {
                ChatUtil.sendMessage("§dModule System Details:");
                ChatUtil.sendMessage("§7Total Modules: §f" + moduleManager.getTotalModules());
                ChatUtil.sendMessage("§7Enabled: §a" + moduleManager.getEnabledCount() + " §7| Disabled: §c" + moduleManager.getDisabledCount());
                ChatUtil.sendMessage("§7Keybinds: §f" + moduleManager.getKeybindManager().getTotalKeybinds());

                ChatUtil.sendMessage("§7Category Breakdown:");
                var stats = moduleManager.getCategoryStatistics();
                for (var entry : stats.entrySet()) {
                    int enabled = moduleManager.getEnabledCategoryCount(entry.getKey());
                    ChatUtil.sendMessage("  " + entry.getKey().getDisplayName() + ": §f" + enabled + "/" + entry.getValue());
                }
            } else {
                ChatUtil.sendMessage("§7Module system not available");
            }
        } catch (Exception e) {
            Team.LOGGER.error("Error accessing module manager in debug info", e);
            ChatUtil.sendMessage("§7Error accessing module system");
        }
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String option : new String[]{"info", "events", "commands", "modules", "system"}) {
                if (option.startsWith(partial)) {
                    suggestions.add(option);
                }
            }
        }

        return suggestions;
    }
}