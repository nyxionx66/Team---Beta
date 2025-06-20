package dev.mindle.team.module.setting;

import dev.mindle.team.util.KeybindUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends Setting<Integer> {
    public KeybindSetting(String module, String name, String description, Integer defaultValue) {
        super(module, name, description, defaultValue);
    }

    @Override
    public void setValue(Integer value) {
        if (value != null) {
            this.value = value;
            save();
        }
    }

    @Override
    public Integer getValue() {
        return value != null ? value : defaultValue;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @Override
    public void setValueFromString(String value) {
        if (value != null) {
            try {
                setValue(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                resetToDefault();
            }
        }
    }

    @Override
    public Setting<Integer> copy() {
        KeybindSetting copy = new KeybindSetting(module, name, description, defaultValue);
        copy.value = this.value;
        return copy;
    }

    public String getKeyName() {
        int keyCode = getValue();
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return "None";
        }
        return KeybindUtil.getKeyName(keyCode);
    }

    public boolean isPressed() {
        int keyCode = getValue();
        if (keyCode == GLFW.GLFW_KEY_UNKNOWN) {
            return false;
        }
        return KeybindUtil.isKeyPressed(keyCode);
    }

    public void clearKeybind() {
        setValue(GLFW.GLFW_KEY_UNKNOWN);
    }

    public boolean hasKeybind() {
        return getValue() != GLFW.GLFW_KEY_UNKNOWN;
    }
}