package dev.mindle.team.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import dev.mindle.team.Team;
import dev.mindle.team.command.impl.info.*;
import dev.mindle.team.command.impl.config.*;
import dev.mindle.team.command.impl.util.*;
import dev.mindle.team.command.impl.module.*;
import dev.mindle.team.command.impl.gui.*;
import dev.mindle.team.util.ChatUtil;

public class CommandManager {
    public static final String PREFIX = ".";
    private final Map<String, Command> commands = new ConcurrentHashMap<>();
    private final Map<String, String> aliases = new ConcurrentHashMap<>();
    private final Map<String, List<Command>> categories = new ConcurrentHashMap<>();

    public CommandManager() {
        Team.LOGGER.info("Initializing command system with prefix: {}", PREFIX);
        initializeCategories();
    }

    private void initializeCategories() {
        categories.put("info", new ArrayList<>());
        categories.put("config", new ArrayList<>());
        categories.put("util", new ArrayList<>());
        categories.put("module", new ArrayList<>());
    }

    public void registerCommands() {
        Team.LOGGER.info("Registering categorized commands...");
        
        // Information Commands
        registerCommand(new HelpCommand(), "info");
        registerCommand(new InfoCommand(), "info");
        
        // Configuration Commands
        registerCommand(new ConfigCommand(), "config");
        
        // Utility Commands
        registerCommand(new ToggleCommand(), "util");
        registerCommand(new ReloadCommand(), "util");
        registerCommand(new DebugCommand(), "util");
        registerCommand(new TestCommand(), "util");
        registerCommand(new StatusCommand(), "util");
        
        // Module Commands
        registerCommand(new ModuleCommand(), "module");
        registerCommand(new KeybindCommand(), "module");
        
        Team.LOGGER.info("Registered {} commands across {} categories", 
            commands.size(), categories.size());
        
        // Log category statistics
        for (Map.Entry<String, List<Command>> entry : categories.entrySet()) {
            Team.LOGGER.debug("Category '{}': {} commands", 
                entry.getKey(), entry.getValue().size());
        }
    }

    public void registerCommand(Command command) {
        registerCommand(command, "general");
    }

    public void registerCommand(Command command, String category) {
        commands.put(command.getName().toLowerCase(), command);
        
        // Add to category
        categories.computeIfAbsent(category, k -> new ArrayList<>()).add(command);
        
        // Register aliases
        for (String alias : command.getAliases()) {
            aliases.put(alias.toLowerCase(), command.getName().toLowerCase());
        }
        
        Team.LOGGER.debug("Registered command: {} in category: {} with {} aliases", 
            command.getName(), category, command.getAliases().length);
    }

    public boolean executeCommand(String input) {
        if (!input.startsWith(PREFIX)) {
            return false;
        }

        String commandLine = input.substring(PREFIX.length()).trim();
        if (commandLine.isEmpty()) {
            return false;
        }

        String[] parts = commandLine.split("\\s+");
        String commandName = parts[0].toLowerCase();
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, args.length);

        // Check for alias
        if (aliases.containsKey(commandName)) {
            commandName = aliases.get(commandName);
        }

        Command command = commands.get(commandName);
        if (command == null) {
            ChatUtil.sendMessage("§cUnknown command: §f" + commandName);
            ChatUtil.sendMessage("§7Type §f" + PREFIX + "help §7for a list of commands");
            return true;
        }

        try {
            long startTime = System.nanoTime();
            command.execute(args);
            long endTime = System.nanoTime();
            
            // Log execution time for debugging
            if (Team.getInstance() != null && 
                Team.getInstance().getConfig() != null && 
                Team.getInstance().getConfig().getBoolean("debug")) {
                double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
                Team.LOGGER.debug("Command '{}' executed in {:.2f}ms", commandName, executionTime);
            }
            
        } catch (IllegalArgumentException e) {
            ChatUtil.sendMessage("§cUsage Error: §f" + e.getMessage());
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError executing command: §f" + e.getMessage());
            Team.LOGGER.error("Error executing command: {}", commandName, e);
        }

        return true;
    }

    public List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        
        try {
            if (!input.startsWith(PREFIX)) {
                return suggestions;
            }

            String commandLine = input.substring(PREFIX.length()).toLowerCase();
            
            // If no space, suggest command names
            if (!commandLine.contains(" ")) {
                for (String commandName : commands.keySet()) {
                    if (commandName.startsWith(commandLine)) {
                        suggestions.add(PREFIX + commandName);
                    }
                }
                
                for (String alias : aliases.keySet()) {
                    if (alias.startsWith(commandLine)) {
                        suggestions.add(PREFIX + alias);
                    }
                }
            } else {
                // Command-specific suggestions
                String[] parts = commandLine.split("\\s+", 2);
                String commandName = parts[0];
                String args = parts.length > 1 ? parts[1] : "";
                
                if (aliases.containsKey(commandName)) {
                    commandName = aliases.get(commandName);
                }
                
                Command command = commands.get(commandName);
                if (command != null) {
                    try {
                        List<String> commandSuggestions = command.getSuggestions(args.split("\\s+"));
                        for (String suggestion : commandSuggestions) {
                            suggestions.add(PREFIX + commandName + " " + suggestion);
                        }
                    } catch (Exception e) {
                        Team.LOGGER.debug("Error getting suggestions for command: {}", commandName, e);
                    }
                }
            }
        } catch (Exception e) {
            Team.LOGGER.error("Error generating command suggestions", e);
        }

        return suggestions;
    }

    public Map<String, Command> getCommands() {
        return new HashMap<>(commands);
    }

    public Command getCommand(String name) {
        String actualName = aliases.getOrDefault(name.toLowerCase(), name.toLowerCase());
        return commands.get(actualName);
    }

    public List<Command> getCommandsByCategory(String category) {
        return new ArrayList<>(categories.getOrDefault(category, new ArrayList<>()));
    }

    public Map<String, List<Command>> getAllCategories() {
        return new HashMap<>(categories);
    }

    public List<String> getCategoryNames() {
        return new ArrayList<>(categories.keySet());
    }

    public int getTotalCommands() {
        return commands.size();
    }

    public int getCommandsInCategory(String category) {
        return categories.getOrDefault(category, new ArrayList<>()).size();
    }

    public void unregisterCommand(String name) {
        Command command = commands.remove(name.toLowerCase());
        if (command != null) {
            // Remove from categories
            categories.values().forEach(list -> list.remove(command));
            
            // Remove aliases
            aliases.entrySet().removeIf(entry -> entry.getValue().equals(name.toLowerCase()));
            
            Team.LOGGER.debug("Unregistered command: {}", name);
        }
    }

    public void clearCategory(String category) {
        List<Command> categoryCommands = categories.get(category);
        if (categoryCommands != null) {
            for (Command command : new ArrayList<>(categoryCommands)) {
                unregisterCommand(command.getName());
            }
            Team.LOGGER.info("Cleared {} commands from category: {}", categoryCommands.size(), category);
        }
    }
}