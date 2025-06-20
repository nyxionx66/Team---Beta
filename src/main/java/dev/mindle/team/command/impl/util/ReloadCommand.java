package dev.mindle.team.command.impl.util;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.util.ChatUtil;

public class ReloadCommand extends Command {
    public ReloadCommand() {
        super("reload", "Reload mod configuration and systems", CommandManager.PREFIX + "reload [config|events|all]", "rl");
    }

    @Override
    public void execute(String[] args) {
        String target = args.length > 0 ? args[0].toLowerCase() : "all";
        
        switch (target) {
            case "config":
                reloadConfig();
                break;
            case "events":
                reloadEvents();
                break;
            case "all":
            default:
                reloadAll();
                break;
        }
    }

    private void reloadConfig() {
        ChatUtil.sendMessage("§7Reloading configuration...");
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config != null) {
                config.load();
                ChatUtil.sendMessage("§aConfiguration reloaded successfully!");
            } else {
                ChatUtil.sendMessage("§cConfiguration system not available");
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError reloading configuration: " + e.getMessage());
            Team.LOGGER.error("Error reloading config", e);
        }
    }

    private void reloadEvents() {
        ChatUtil.sendMessage("§7Clearing event bus...");
        try {
            EventBus eventBus = Team.getInstance().getEventBus();
            if (eventBus != null) {
                eventBus.clear();
                ChatUtil.sendMessage("§aEvent bus cleared! Re-register your listeners.");
            } else {
                ChatUtil.sendMessage("§cEvent system not available");
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError clearing event bus: " + e.getMessage());
            Team.LOGGER.error("Error clearing event bus", e);
        }
    }

    private void reloadAll() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " §7- Full reload initiated...");
        
        // Reload config
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config != null) {
                config.load();
                ChatUtil.sendMessage("§7✓ Configuration reloaded");
            } else {
                ChatUtil.sendMessage("§7✗ Configuration not available");
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§7✗ Configuration reload failed");
            Team.LOGGER.error("Error reloading config in reloadAll", e);
        }
        
        // Clear and note event bus
        try {
            EventBus eventBus = Team.getInstance().getEventBus();
            if (eventBus != null) {
                eventBus.clear();
                ChatUtil.sendMessage("§7✓ Event bus cleared");
            } else {
                ChatUtil.sendMessage("§7✗ Event bus not available");
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§7✗ Event bus clear failed");
            Team.LOGGER.error("Error clearing event bus in reloadAll", e);
        }
        
        ChatUtil.sendMessage("§a" + Team.MOD_NAME + " reload complete!");
        ChatUtil.sendMessage("§7Note: Event listeners need to be re-registered");
    }
}