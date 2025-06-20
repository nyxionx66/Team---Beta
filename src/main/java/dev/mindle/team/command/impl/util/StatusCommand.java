package dev.mindle.team.command.impl.util;

import java.util.ArrayList;
import java.util.List;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.screen.ScreenManager;
import dev.mindle.team.util.ChatUtil;
import dev.mindle.team.util.KeybindUtil;
import net.minecraft.client.MinecraftClient;

public class StatusCommand extends Command {
    public StatusCommand() {
        super("status", "Shows system status and debugging information", CommandManager.PREFIX + "status [system|screen|keybind]", "stat");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            showGeneralStatus();
        } else {
            String type = args[0].toLowerCase();
            switch (type) {
                case "system":
                    showSystemStatus();
                    break;
                case "screen":
                    showScreenStatus();
                    break;
                case "keybind":
                    showKeybindStatus();
                    break;
                default:
                    ChatUtil.sendMessage("§cUnknown status type: §f" + type);
                    ChatUtil.sendMessage("§7Use: §f" + CommandManager.PREFIX + "status [system|screen|keybind]");
            }
        }
    }

    private void showGeneralStatus() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " System Status:");
        ChatUtil.sendMessage("§7─────────────────────────");
        
        // Screen status
        ChatUtil.sendMessage("§9Screen System:");
        ChatUtil.sendMessage("§7- Screen Open: " + (ScreenManager.isScreenOpen() ? "§cYes" : "§aNo"));
        ChatUtil.sendMessage("§7- Screen Type: §f" + ScreenManager.getScreenType());
        
        // Keybind status
        ChatUtil.sendMessage("§9Keybind System:");
        ChatUtil.sendMessage("§7- Keybinds Active: " + (KeybindUtil.shouldProcessKeybinds() ? "§aYes" : "§cNo"));
        ChatUtil.sendMessage("§7- In Game: " + (KeybindUtil.isInGame() ? "§aYes" : "§cNo"));
        
        // Module status
        try {
            if (Team.getInstance() != null && Team.getInstance().getModuleManager() != null) {
                Object moduleManager = Team.getInstance().getModuleManager();
                if (moduleManager != null) {
                    ChatUtil.sendMessage("§9Module System:");
                    ChatUtil.sendMessage("§7- Total Modules: §f" + ((dev.mindle.team.module.ModuleManager)moduleManager).getTotalModules());
                    ChatUtil.sendMessage("§7- Enabled: §a" + ((dev.mindle.team.module.ModuleManager)moduleManager).getEnabledCount());
                    ChatUtil.sendMessage("§7- Keybinds: §f" + ((dev.mindle.team.module.ModuleManager)moduleManager).getKeybindManager().getTotalKeybinds());
                }
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§9Module System: §cError accessing");
        }
    }

    private void showScreenStatus() {
        ChatUtil.sendMessage("§9Screen Detection Status:");
        ChatUtil.sendMessage("§7─────────────────────────");
        
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Screen Manager status
        ChatUtil.sendMessage("§7ScreenManager Status:");
        ChatUtil.sendMessage("§7- Screen Open: " + (ScreenManager.isScreenOpen() ? "§cYes" : "§aNo"));
        ChatUtil.sendMessage("§7- Current Screen: §f" + ScreenManager.getScreenType());
        ChatUtil.sendMessage("§7- Chat Open: " + (ScreenManager.isChatOpen() ? "§cYes" : "§aNo"));
        ChatUtil.sendMessage("§7- Inventory Open: " + (ScreenManager.isInventoryOpen() ? "§cYes" : "§aNo"));
        ChatUtil.sendMessage("§7- Pause Menu Open: " + (ScreenManager.isPauseMenuOpen() ? "§cYes" : "§aNo"));
        
        // Minecraft Client status (for comparison)
        ChatUtil.sendMessage("§7MinecraftClient Status:");
        if (mc.currentScreen != null) {
            ChatUtil.sendMessage("§7- MC Screen: §f" + mc.currentScreen.getClass().getSimpleName());
        } else {
            ChatUtil.sendMessage("§7- MC Screen: §aNull (no screen)");
        }
    }

    private void showKeybindStatus() {
        ChatUtil.sendMessage("§9Keybind System Status:");
        ChatUtil.sendMessage("§7─────────────────────────");
        
        ChatUtil.sendMessage("§7Processing Status:");
        ChatUtil.sendMessage("§7- Should Process: " + (KeybindUtil.shouldProcessKeybinds() ? "§aYes" : "§cNo"));
        ChatUtil.sendMessage("§7- In Game: " + (KeybindUtil.isInGame() ? "§aYes" : "§cNo"));
        
        ChatUtil.sendMessage("§7Screen Checks:");
        ChatUtil.sendMessage("§7- Chat Open: " + (KeybindUtil.isChatOpen() ? "§cYes" : "§aNo"));
        ChatUtil.sendMessage("§7- Inventory Open: " + (KeybindUtil.isInventoryOpen() ? "§cYes" : "§aNo"));
        ChatUtil.sendMessage("§7- Pause Menu Open: " + (KeybindUtil.isPauseMenuOpen() ? "§cYes" : "§aNo"));
        
        try {
            if (Team.getInstance() != null && Team.getInstance().getModuleManager() != null) {
                Object keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
                if (keybindManager != null) {
                    ChatUtil.sendMessage("§7Keybind Statistics:");
                    ChatUtil.sendMessage("§7- Total Keybinds: §f" + ((dev.mindle.team.module.keybind.KeybindManager)keybindManager).getTotalKeybinds());
                    ChatUtil.sendMessage("§7- Toggle Keybinds: §f" + ((dev.mindle.team.module.keybind.KeybindManager)keybindManager).getKeybindCount(dev.mindle.team.module.keybind.KeybindType.TOGGLE));
                    ChatUtil.sendMessage("§7- Hold Keybinds: §f" + ((dev.mindle.team.module.keybind.KeybindManager)keybindManager).getKeybindCount(dev.mindle.team.module.keybind.KeybindType.HOLD));
                }
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§7Keybind Manager: §cError accessing");
        }
    }

    private void showSystemStatus() {
        ChatUtil.sendMessage("§9System Performance Status:");
        ChatUtil.sendMessage("§7─────────────────────────");
        
        Runtime runtime = Runtime.getRuntime();
        MinecraftClient mc = MinecraftClient.getInstance();
        
        ChatUtil.sendMessage("§7Memory Usage:");
        long used = runtime.totalMemory() - runtime.freeMemory();
        long total = runtime.totalMemory();
        ChatUtil.sendMessage("§7- Used: §f" + (used / 1024 / 1024) + "MB");
        ChatUtil.sendMessage("§7- Total: §f" + (total / 1024 / 1024) + "MB");
        ChatUtil.sendMessage("§7- Usage: §f" + String.format("%.1f%%", (used * 100.0) / total));
        
        ChatUtil.sendMessage("§7Game State:");
        ChatUtil.sendMessage("§7- Player: " + (mc.player != null ? "§aLoaded" : "§cNull"));
        ChatUtil.sendMessage("§7- World: " + (mc.world != null ? "§aLoaded" : "§cNull"));
        ChatUtil.sendMessage("§7- Server: " + (mc.getServer() != null ? "§aIntegrated" : "§7External/None"));
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String option : new String[]{"system", "screen", "keybind"}) {
                if (option.startsWith(partial)) {
                    suggestions.add(option);
                }
            }
        }
        
        return suggestions;
    }
}