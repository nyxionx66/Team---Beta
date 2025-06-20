package dev.mindle.team.screen;

import dev.mindle.team.Team;
import net.minecraft.client.gui.screen.Screen;

public class ScreenManager {
    private static volatile boolean screenOpen = false;
    private static Screen currentScreen = null;
    
    public static void setScreenOpen(Screen screen) {
        screenOpen = screen != null;
        currentScreen = screen;
        
        if (screenOpen) {
            Team.LOGGER.debug("Screen opened: {}", screen != null ? screen.getClass().getSimpleName() : "null");
        } else {
            Team.LOGGER.debug("Screen closed");
        }
    }
    
    public static boolean isScreenOpen() {
        return screenOpen;
    }
    
    public static Screen getCurrentScreen() {
        return currentScreen;
    }
    
    public static boolean isChatOpen() {
        return screenOpen && currentScreen != null && 
               currentScreen.getClass().getSimpleName().contains("Chat");
    }
    
    public static boolean isInventoryOpen() {
        return screenOpen && currentScreen != null && 
               (currentScreen.getClass().getSimpleName().contains("Inventory") ||
                currentScreen.getClass().getSimpleName().contains("Container"));
    }
    
    public static boolean isPauseMenuOpen() {
        return screenOpen && currentScreen != null && 
               currentScreen.getClass().getSimpleName().contains("GameMenu");
    }
    
    public static String getScreenType() {
        if (!screenOpen || currentScreen == null) {
            return "none";
        }
        return currentScreen.getClass().getSimpleName();
    }
}