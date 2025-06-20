package dev.mindle.team.util;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

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
        return InputUtil.isKeyPressed(net.minecraft.client.MinecraftClient.getInstance().getWindow().getHandle(), key);
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