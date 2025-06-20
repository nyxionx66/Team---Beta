package dev.mindle.team.module.keybind;

public enum KeybindType {
    TOGGLE("Toggle", "Toggles on/off when key is pressed"),
    HOLD("Hold", "Active only while key is held down");

    private final String name;
    private final String description;

    KeybindType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}