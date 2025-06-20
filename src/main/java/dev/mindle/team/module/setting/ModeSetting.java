package dev.mindle.team.module.setting;

import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
    private final List<String> modes;
    private int currentIndex;

    public ModeSetting(String module, String name, String description, String defaultValue, String... modes) {
        super(module, name, description, defaultValue);
        this.modes = Arrays.asList(modes);
        this.currentIndex = this.modes.indexOf(defaultValue);
        if (this.currentIndex == -1) {
            this.currentIndex = 0;
        }
    }

    @Override
    public void setValue(String value) {
        if (value != null && modes.contains(value)) {
            this.value = value;
            this.currentIndex = modes.indexOf(value);
            save();
        }
    }

    @Override
    public String getValue() {
        if (value != null && modes.contains(value)) {
            return value;
        }
        return modes.isEmpty() ? defaultValue : modes.get(Math.max(0, Math.min(currentIndex, modes.size() - 1)));
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public void setValueFromString(String value) {
        setValue(value);
    }

    @Override
    public Setting<String> copy() {
        ModeSetting copy = new ModeSetting(module, name, description, defaultValue, modes.toArray(new String[0]));
        copy.value = this.value;
        copy.currentIndex = this.currentIndex;
        return copy;
    }

    public List<String> getModes() {
        return modes;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setIndex(int index) {
        if (index >= 0 && index < modes.size()) {
            this.currentIndex = index;
            setValue(modes.get(index));
        }
    }

    public void cycle() {
        setIndex((currentIndex + 1) % modes.size());
    }

    public void cyclePrevious() {
        setIndex((currentIndex - 1 + modes.size()) % modes.size());
    }

    public boolean is(String mode) {
        return getValue().equalsIgnoreCase(mode);
    }

    public int getModeCount() {
        return modes.size();
    }
}