package dev.mindle.team.module;

public enum ModuleCategory {
    COMBAT("Combat", "⚔"),
    MOVEMENT("Movement", "🏃"),
    RENDER("Render", "👁"),
    PLAYER("Player", "👤"),
    WORLD("World", "🌍"),
    MISC("Misc", "🔧"),
    CLIENT("Client", "⚙");

    private final String name;
    private final String icon;

    ModuleCategory(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return icon + " " + name;
    }
}