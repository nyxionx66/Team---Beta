package dev.mindle.team.module.setting;

import java.awt.Color;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String module, String name, String description, Integer defaultValue) {
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
        ColorSetting copy = new ColorSetting(module, name, description, defaultValue);
        copy.value = this.value;
        return copy;
    }

    public String getHexString() {
        int color = getValue();
        return String.format("#%06X", color & 0xFFFFFF);
    }

    public Color getColor() {
        return new Color(getValue(), true);
    }

    public void setFromHSB(float hue, float saturation, float brightness) {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        setValue(rgb | 0xFF000000); // Ensure alpha is 255
    }

    public int getRed() {
        return (getValue() >> 16) & 0xFF;
    }

    public int getGreen() {
        return (getValue() >> 8) & 0xFF;
    }

    public int getBlue() {
        return getValue() & 0xFF;
    }

    public int getAlpha() {
        return (getValue() >> 24) & 0xFF;
    }

    public void setRGB(int red, int green, int blue) {
        setRGBA(red, green, blue, 255);
    }

    public void setRGBA(int red, int green, int blue, int alpha) {
        int color = (alpha << 24) | (red << 16) | (green << 8) | blue;
        setValue(color);
    }
}