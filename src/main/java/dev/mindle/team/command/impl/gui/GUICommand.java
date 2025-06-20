package dev.mindle.team.command.impl.gui;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.gui.ClickGUIManager;
import dev.mindle.team.util.ChatUtil;

public class GUICommand extends Command {
    
    public GUICommand() {
        super("gui", "Open the ClickGUI", "gui");
    }
    
    @Override
    public void execute(String[] args) {
        try {
            ClickGUIManager guiManager = Team.getInstance().getGUIManager();
            
            if (args.length == 0) {
                // Open GUI
                guiManager.openGUI();
                ChatUtil.sendMessage("§aOpened ClickGUI");
            } else if (args.length == 1) {
                String action = args[0].toLowerCase();
                
                switch (action) {
                    case "open":
                        guiManager.openGUI();
                        ChatUtil.sendMessage("§aOpened ClickGUI");
                        break;
                    case "close":
                        guiManager.closeGUI();
                        ChatUtil.sendMessage("§cClosed ClickGUI");
                        break;
                    case "toggle":
                        guiManager.toggleGUI();
                        ChatUtil.sendMessage(guiManager.isGUIOpen() ? "§aOpened ClickGUI" : "§cClosed ClickGUI");
                        break;
                    case "status":
                        ChatUtil.sendMessage("§7ClickGUI is " + (guiManager.isGUIOpen() ? "§aopen" : "§cclosed"));
                        break;
                    default:
                        ChatUtil.sendMessage("§cUnknown action: " + action);
                        showUsage();
                        break;
                }
            } else {
                ChatUtil.sendMessage("§cToo many arguments!");
                showUsage();
            }
        } catch (Exception e) {
            ChatUtil.sendMessage("§cError executing GUI command: " + e.getMessage());
            Team.LOGGER.error("Error in GUI command", e);
        }
    }
    
    private void showUsage() {
        ChatUtil.sendMessage("§7Usage:");
        ChatUtil.sendMessage("§7  .gui - Open the ClickGUI");
        ChatUtil.sendMessage("§7  .gui open - Open the ClickGUI");
        ChatUtil.sendMessage("§7  .gui close - Close the ClickGUI");
        ChatUtil.sendMessage("§7  .gui toggle - Toggle the ClickGUI");
        ChatUtil.sendMessage("§7  .gui status - Check GUI status");
    }
    
    @Override
    public String getUsage() {
        return ".gui [open|close|toggle|status]";
    }
}