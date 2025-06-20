package dev.mindle.team.gui.clickgui.elements;

import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

public abstract class AbstractElement {
    protected final Setting<?> setting;
    protected float x, y, width, height;
    protected boolean hovered;

    public AbstractElement(Setting<?> setting) {
        this.setting = setting;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, width, height);
    }

    public void init() {
        // Initialize element
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        // Handle mouse clicks
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        // Handle mouse releases
    }

    public void keyPressed(int keyCode) {
        // Handle key presses
    }

    public void charTyped(char key, int modifier) {
        // Handle character input
    }

    public void tick() {
        // Update element state
    }

    // Getters and setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }
    public Setting<?> getSetting() { return setting; }
    public boolean isVisible() { return true; } // Always visible for now
}