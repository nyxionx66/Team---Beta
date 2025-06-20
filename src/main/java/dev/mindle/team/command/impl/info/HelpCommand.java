package dev.mindle.team.command.impl.info;

import java.util.ArrayList;
import java.util.List;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.util.ChatUtil;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Shows help for commands", CommandManager.PREFIX + "help [command]", "h", "?");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            showCommandList();
        } else {
            showCommandHelp(args[0]);
        }
    }

    private void showCommandList() {
        ChatUtil.sendMessage("§b" + Team.MOD_NAME + " Commands:");
        ChatUtil.sendMessage("§7Prefix: §f" + CommandManager.PREFIX);
        ChatUtil.sendMessage("§7─────────────────────────");
        
        // Categorize commands for display
        showCommandCategory("§9Information Commands:", 
            Team.getInstance().getCommandManager().getCommandsByCategory("info"));
        showCommandCategory("§aConfiguration Commands:", 
            Team.getInstance().getCommandManager().getCommandsByCategory("config"));
        showCommandCategory("§eUtility Commands:", 
            Team.getInstance().getCommandManager().getCommandsByCategory("util"));
        
        ChatUtil.sendMessage("§7─────────────────────────");
        ChatUtil.sendMessage("§7Type §f" + CommandManager.PREFIX + "help [command] §7for detailed help");
    }

    private void showCommandCategory(String categoryName, List<Command> commands) {
        if (!commands.isEmpty()) {
            ChatUtil.sendMessage(categoryName);
            for (Command command : commands) {
                ChatUtil.sendMessage("  §f" + CommandManager.PREFIX + command.getName() + " §7- " + command.getDescription());
            }
        }
    }

    private void showCommandHelp(String commandName) {
        Command command = Team.getInstance().getCommandManager().getCommand(commandName);
        
        if (command == null) {
            ChatUtil.sendMessage("§cCommand not found: §f" + commandName);
            return;
        }
        
        ChatUtil.sendMessage("§b" + command.getName() + " Command Help:");
        ChatUtil.sendMessage("§7Category: §f" + getCommandCategory(command));
        ChatUtil.sendMessage("§7Description: §f" + command.getDescription());
        ChatUtil.sendMessage("§7Usage: §f" + command.getUsage());
        
        if (command.getAliases().length > 0) {
            ChatUtil.sendMessage("§7Aliases: §f" + String.join(", ", command.getAliases()));
        }
    }

    private String getCommandCategory(Command command) {
        String className = command.getClass().getName();
        if (className.contains(".info.")) return "Information";
        if (className.contains(".config.")) return "Configuration";
        if (className.contains(".util.")) return "Utility";
        return "General";
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();
        
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String commandName : Team.getInstance().getCommandManager().getCommands().keySet()) {
                if (commandName.startsWith(partial)) {
                    suggestions.add(commandName);
                }
            }
        }
        
        return suggestions;
    }
}