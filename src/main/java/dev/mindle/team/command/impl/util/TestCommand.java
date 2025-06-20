package dev.mindle.team.command.impl.util;

import java.util.ArrayList;
import java.util.List;

import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.util.ChatUtil;

public class TestCommand extends Command {
    public TestCommand() {
        super("test", "Test command for verification", CommandManager.PREFIX + "test [message]", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            ChatUtil.sendMessage("§aTest command executed successfully!");
            ChatUtil.sendMessage("§7This is a test command to verify the help system auto-detection works.");
            
            // Show screen status
            ChatUtil.sendMessage("§7Screen Status:");
            ChatUtil.sendMessage("§7- Screen Open: " + (ScreenManager.isScreenOpen() ? "§cYes" : "§aNo"));
            ChatUtil.sendMessage("§7- Current Screen: §f" + ScreenManager.getScreenType());
            ChatUtil.sendMessage("§7- Keybinds Allowed: " + (KeybindUtil.shouldProcessKeybinds() ? "§aYes" : "§cNo"));
        } else {
            String message = String.join(" ", args);
            ChatUtil.sendMessage("§aTest message: §f" + message);
        }
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String option : new String[]{"hello", "world", "test", "message"}) {
                if (option.startsWith(partial)) {
                    suggestions.add(option);
                }
            }
        }
        
        return suggestions;
    }
}