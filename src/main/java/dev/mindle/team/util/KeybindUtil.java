package dev.mindle.team.util;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import dev.mindle.team.screen.ScreenManager;

public class KeybindUtil {
    
    public static KeyBinding createKeybind(String name, int defaultKey, String category) {
        return new KeyBinding(
            name,
            InputUtil.Type.KEYSYM,
            defaultKey,
            category
        );
    }

    public static boolean isKeyPressed(int key) {
        return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key);
    }

    public static boolean shouldProcessKeybinds() {
        // Use our ScreenManager for more reliable screen detection
        return !ScreenManager.isScreenOpen();
    }

    public static boolean isInGame() {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.player != null && mc.world != null && !ScreenManager.isScreenOpen();
    }

    public static boolean isChatOpen() {
        return ScreenManager.isChatOpen();
    }

    public static boolean isInventoryOpen() {
        return ScreenManager.isInventoryOpen();
    }

    public static boolean isPauseMenuOpen() {
        return ScreenManager.isPauseMenuOpen();
    }

    public static String getCurrentScreenType() {
        return ScreenManager.getScreenType();
    }

    public static boolean isCtrlPressed() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static boolean isShiftPressed() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isAltPressed() {
        return isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static String getKeyName(int key) {
        return InputUtil.fromKeyCode(key, 0).getLocalizedText().getString();
    }
}