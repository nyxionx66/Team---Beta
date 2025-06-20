package dev.mindle.team.gui;

import dev.mindle.team.Team;
import dev.mindle.team.module.keybind.Keybind;
import dev.mindle.team.module.keybind.KeybindType;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ClickGUIManager {
    private static ClickGUIManager instance;
    private ClickGUIScreen currentGUI;
    private Keybind openKeybind;
    
    private ClickGUIManager() {
        setupKeybind();
    }
    
    public static ClickGUIManager getInstance() {
        if (instance == null) {
            instance = new ClickGUIManager();
        }
        return instance;
    }
    
    private void setupKeybind() {
        // Default keybind: Right Shift
        openKeybind = new Keybind(
            "clickgui_open",
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeybindType.TOGGLE,
            this::toggleGUI
        );
        
        // Register the keybind
        if (Team.getInstance() != null && Team.getInstance().getModuleManager() != null) {
            Team.getInstance().getModuleManager().getKeybindManager()
                .registerKeybind("clickgui", GLFW.GLFW_KEY_RIGHT_SHIFT, KeybindType.TOGGLE, this::toggleGUI);
        }
        
        Team.LOGGER.info("ClickGUI keybind registered: Right Shift");
    }
    
    public void toggleGUI() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.currentScreen instanceof ClickGUIScreen) {
            closeGUI();
        } else {
            openGUI();
        }
    }
    
    public void openGUI() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.currentScreen instanceof ClickGUIScreen) {
            return; // Already open
        }
        
        currentGUI = new ClickGUIScreen();
        client.setScreen(currentGUI);
        
        Team.LOGGER.debug("ClickGUI opened");
    }
    
    public void closeGUI() {
        MinecraftClient client = MinecraftClient.getInstance();
        
        if (client.currentScreen instanceof ClickGUIScreen) {
            client.setScreen(null);
            currentGUI = null;
            Team.LOGGER.debug("ClickGUI closed");
        }
    }
    
    public boolean isGUIOpen() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.currentScreen instanceof ClickGUIScreen;
    }
    
    public ClickGUIScreen getCurrentGUI() {
        return currentGUI;
    }
    
    public Keybind getOpenKeybind() {
        return openKeybind;
    }
    
    public void setOpenKeybind(int keyCode) {
        if (openKeybind != null) {
            // Unregister old keybind
            Team.getInstance().getModuleManager().getKeybindManager()
                .unregisterKeybind("clickgui");
        }
        
        // Register new keybind
        openKeybind = new Keybind(
            "clickgui_open",
            keyCode,
            KeybindType.TOGGLE,
            this::toggleGUI
        );
        
        Team.getInstance().getModuleManager().getKeybindManager()
            .registerKeybind("clickgui", keyCode, KeybindType.TOGGLE, this::toggleGUI);
        
        // Save to config
        try {
            Team.getInstance().getConfig().setInt("gui.open_keybind", keyCode);
            Team.LOGGER.debug("ClickGUI keybind changed to: {}", keyCode);
        } catch (Exception e) {
            Team.LOGGER.error("Failed to save ClickGUI keybind", e);
        }
    }
    
    public void loadKeybind() {
        try {
            if (Team.getInstance().getConfig().hasKey("gui.open_keybind")) {
                int keyCode = Team.getInstance().getConfig().getInt("gui.open_keybind");
                setOpenKeybind(keyCode);
            }
        } catch (Exception e) {
            Team.LOGGER.error("Failed to load ClickGUI keybind", e);
        }
    }
}