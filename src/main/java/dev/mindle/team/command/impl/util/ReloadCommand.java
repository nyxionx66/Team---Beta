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
        Team.getInstance().getConfig().load();
        ChatUtil.sendMessage("§aConfiguration reloaded successfully!");
    }

    private void reloadEvents() {
        ChatUtil.sendMessage("§7Clearing event bus...");
        Team.getInstance().getEventBus().clear();
        ChatUtil.sendMessage("§aEvent bus cleared! Re-register your listeners.");
    }

    private void reloadAll() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " §7- Full reload initiated...");
        
        // Reload config
        Team.getInstance().getConfig().load();
        ChatUtil.sendMessage("§7✓ Configuration reloaded");
        
        // Clear and note event bus
        Team.getInstance().getEventBus().clear();
        ChatUtil.sendMessage("§7✓ Event bus cleared");
        
        ChatUtil.sendMessage("§a" + Team.MOD_NAME + " reload complete!");
        ChatUtil.sendMessage("§7Note: Event listeners need to be re-registered");
    }
}