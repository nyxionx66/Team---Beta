package dev.mindle.team.module.keybind;

import dev.mindle.team.Team;
import dev.mindle.team.util.KeybindUtil;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Keybind {
    private final String name;
    private final int defaultKey;
    private int key;
    private final KeybindType type;
    private boolean pressed;
    private boolean lastPressed;
    private final Runnable onPress;
    private final Runnable onRelease;

    public Keybind(String name, int defaultKey, KeybindType type, Runnable onPress) {
        this(name, defaultKey, type, onPress, null);
    }

    public Keybind(String name, int defaultKey, KeybindType type, Runnable onPress, Runnable onRelease) {
        this.name = name;
        this.defaultKey = defaultKey;
        this.key = defaultKey;
        this.type = type;
        this.pressed = false;
        this.lastPressed = false;
        this.onPress = onPress;
        this.onRelease = onRelease;
    }

    public void update() {
        this.lastPressed = this.pressed;
        this.pressed = KeybindUtil.isKeyPressed(key);

        if (type == KeybindType.TOGGLE) {
            // Toggle mode: trigger on key press (not held)
            if (pressed && !lastPressed) {
                if (onPress != null) {
                    try {
                        onPress.run();
                    } catch (Exception e) {
                        Team.LOGGER.error("Error executing keybind action for: " + name, e);
                    }
                }
            }
        } else if (type == KeybindType.HOLD) {
            // Hold mode: trigger on press and release
            if (pressed && !lastPressed) {
                if (onPress != null) {
                    try {
                        onPress.run();
                    } catch (Exception e) {
                        Team.LOGGER.error("Error executing keybind press action for: " + name, e);
                    }
                }
            } else if (!pressed && lastPressed) {
                if (onRelease != null) {
                    try {
                        onRelease.run();
                    } catch (Exception e) {
                        Team.LOGGER.error("Error executing keybind release action for: " + name, e);
                    }
                }
            }
        }
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isJustPressed() {
        return pressed && !lastPressed;
    }

    public boolean isJustReleased() {
        return !pressed && lastPressed;
    }

    public String getName() {
        return name;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getDefaultKey() {
        return defaultKey;
    }

    public KeybindType getType() {
        return type;
    }

    public String getKeyName() {
        return KeybindUtil.getKeyName(key);
    }

    public void resetToDefault() {
        this.key = defaultKey;
    }

    public boolean isDefault() {
        return key == defaultKey;
    }
}