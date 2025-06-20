package dev.mindle.team.command.impl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.util.ChatUtil;

public class ToggleCommand extends Command {
    private static final List<String> TOGGLEABLE_FEATURES = Arrays.asList(
            "hud", "debug", "notifications", "autocomplete", "showCoordinates", "showFPS"
    );

    public ToggleCommand() {
        super("toggle", "Toggle mod features", CommandManager.PREFIX + "toggle <feature|all>", "t", "switch");
    }

    @Override
    public void execute(String[] args) {
        validateArgs(args, 1);

        // Check if config is available with better error handling
        TeamConfig config = null;
        try {
            config = Team.getInstance().getConfig();
        } catch (Exception e) {
            Team.LOGGER.error("Error accessing config in toggle command", e);
        }
        
        if (config == null) {
            ChatUtil.sendMessage("§cConfiguration system not available. Please try again in a moment.");
            return;
        }

        String feature = args[0].toLowerCase();

        if (feature.equals("all")) {
            toggleAll();
            return;
        }

        if (!TOGGLEABLE_FEATURES.contains(feature)) {
            ChatUtil.sendMessage("§cUnknown feature: §f" + feature);
            ChatUtil.sendMessage("§7Available features:");
            showFeatureList();
            return;
        }

        try {
            boolean currentState = config.getBoolean(feature);
            boolean newState = !currentState;
            config.setBoolean(feature, newState);

            String statusColor = newState ? "§a" : "§c";
            String statusText = newState ? "enabled" : "disabled";
            ChatUtil.sendMessage("§7" + capitalize(feature) + " " + statusColor + statusText);
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError toggling feature: §f" + e.getMessage());
            Team.LOGGER.error("Error toggling feature: " + feature, e);
        }
    }

    private void toggleAll() {
        ChatUtil.sendMessage("§bToggling all features:");
        
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config == null) {
                ChatUtil.sendMessage("§cConfiguration system not available");
                return;
            }
            
            for (String feature : TOGGLEABLE_FEATURES) {
                try {
                    boolean currentState = config.getBoolean(feature);
                    boolean newState = !currentState;
                    config.setBoolean(feature, newState);

                    String statusColor = newState ? "§a" : "§c";
                    String statusText = newState ? "ON" : "OFF";
                    ChatUtil.sendMessage("  §f" + capitalize(feature) + ": " + statusColor + statusText);
                } catch (Exception e) {
                    ChatUtil.sendMessage("  §f" + capitalize(feature) + ": §cError");
                    Team.LOGGER.debug("Error toggling feature: " + feature, e);
                }
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError toggling features: " + e.getMessage());
            Team.LOGGER.error("Error in toggleAll", e);
        }
    }

    private void showFeatureList() {
        try {
            TeamConfig config = Team.getInstance().getConfig();
            if (config == null) {
                ChatUtil.sendMessage("§cConfiguration system not available");
                return;
            }
            
            for (String feature : TOGGLEABLE_FEATURES) {
                try {
                    boolean current = config.getBoolean(feature);
                    String statusColor = current ? "§a" : "§c";
                    String statusText = current ? "ON" : "OFF";
                    ChatUtil.sendMessage("  §f" + feature + " " + statusColor + "[" + statusText + "]");
                } catch (Exception e) {
                    ChatUtil.sendMessage("  §f" + feature + " §c[ERROR]");
                    Team.LOGGER.debug("Error accessing feature state: " + feature, e);
                }
            }
            ChatUtil.sendMessage("§7Use §f" + CommandManager.PREFIX + "toggle all §7to toggle everything");
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError showing feature list: " + e.getMessage());
            Team.LOGGER.error("Error in showFeatureList", e);
        }
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();

            // Add "all" option
            if ("all".startsWith(partial)) {
                suggestions.add("all");
            }

            // Add individual features
            for (String feature : TOGGLEABLE_FEATURES) {
                if (feature.startsWith(partial)) {
                    suggestions.add(feature);
                }
            }
        }

        return suggestions;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}