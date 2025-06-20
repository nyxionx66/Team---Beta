package dev.mindle.team.event.events;

import dev.mindle.team.event.Event;

public class MouseEvent extends Event {
    private final int button;
    private final int action;
    private final int modifiers;
    private final double mouseX;
    private final double mouseY;

    public MouseEvent(int button, int action, int modifiers, double mouseX, double mouseY) {
        this.button = button;
        this.action = action;
        this.modifiers = modifiers;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public int getButton() { return button; }
    public int getAction() { return action; }
    public int getModifiers() { return modifiers; }
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
}