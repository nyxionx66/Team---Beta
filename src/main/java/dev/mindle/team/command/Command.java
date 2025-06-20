package dev.mindle.team.command;

import java.util.List;
import java.util.ArrayList;

public abstract class Command {
    private final String name;
    private final String description;
    private final String usage;
    private final String[] aliases;

    public Command(String name, String description, String usage, String... aliases) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);

    public List<String> getSuggestions(String[] args) {
        return new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String[] getAliases() {
        return aliases;
    }

    protected void validateArgs(String[] args, int minArgs) {
        if (args.length < minArgs) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }

    protected void validateArgs(String[] args, int minArgs, int maxArgs) {
        if (args.length < minArgs || args.length > maxArgs) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }
}