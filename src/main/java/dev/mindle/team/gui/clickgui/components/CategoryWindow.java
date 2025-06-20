package dev.mindle.team.gui.clickgui.components;

import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.impl.client.ClickGUI;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a draggable category window containing modules
 * Handles category display, module organization, and user interactions
 */
public class CategoryWindow {
    private final ModuleCategory category;
    private final List<ModuleButton> buttons;
    private float x, y, width, height;
    private float prevX, prevY;
    private boolean hovered;
    private boolean dragging;
    private boolean open;

    public CategoryWindow(ModuleCategory category, List<Module> modules, float x, float y, float width, float height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.open = false;
        this.buttons = new ArrayList<>();

        initializeButtons(modules);
    }

    private void initializeButtons(List<Module> modules) {
        for (Module module : modules) {
            buttons.add(new ModuleButton(module));
        }
    }

    public void init() {
        buttons.forEach(ModuleButton::init);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updatePosition(mouseX, mouseY);
        
        renderHeader(context);
        
        if (open) {
            renderModules(context, mouseX, mouseY, delta);
        }
    }

    private void updatePosition(int mouseX, int mouseY) {
        hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, width, height);
        
        if (dragging) {
            this.x = this.prevX + mouseX;
            this.y = this.prevY + mouseY;
        }
    }

    private void renderHeader(DrawContext context) {
        // Clean black and white header
        int headerColor = hovered ? 0xFFFFFFFF : 0xFF000000;
        int textColor = hovered ? 0xFF000000 : 0xFFFFFFFF;
        int borderColor = 0xFFFFFFFF;
        
        // Header background
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), headerColor);
        
        // Clean white border
        context.fill((int) x, (int) y, (int) (x + width), (int) y + 1, borderColor); // Top
        context.fill((int) x, (int) (y + height - 1), (int) (x + width), (int) (y + height), borderColor); // Bottom
        context.fill((int) x, (int) y, (int) x + 1, (int) (y + height), borderColor); // Left
        context.fill((int) (x + width - 1), (int) y, (int) (x + width), (int) (y + height), borderColor); // Right
        
        // Category title with clean typography
        String categoryName = category.getName();
        MinecraftClient mc = MinecraftClient.getInstance();
        int textX = (int) (x + width / 2 - mc.textRenderer.getWidth(categoryName) / 2);
        context.drawText(mc.textRenderer, categoryName, textX, (int) (y + height / 2 - 4), textColor, false);
    }

    private void renderModules(DrawContext context, int mouseX, int mouseY, float delta) {
        float currentY = y + height + 2; // Small gap after header
        
        // Clean module area background
        float moduleAreaHeight = getModuleAreaHeight();
        
        // Black background with white border
        context.fill((int) x, (int) currentY, (int) (x + width), (int) (currentY + moduleAreaHeight), 0xFF000000);
        
        // Clean borders
        context.fill((int) x, (int) currentY, (int) (x + width), (int) currentY + 1, 0xFFFFFFFF); // Top
        context.fill((int) x, (int) (currentY + moduleAreaHeight - 1), (int) (x + width), (int) (currentY + moduleAreaHeight), 0xFFFFFFFF); // Bottom
        context.fill((int) x, (int) currentY, (int) x + 1, (int) (currentY + moduleAreaHeight), 0xFFFFFFFF); // Left
        context.fill((int) (x + width - 1), (int) currentY, (int) (x + width), (int) (currentY + moduleAreaHeight), 0xFFFFFFFF); // Right
        
        currentY += 2; // Top padding
        
        for (ModuleButton button : buttons) {
            button.setX(x + 2); // Left padding
            button.setY(currentY);
            button.setWidth(width - 4); // Padding on both sides
            button.setHeight(ClickGUI.moduleHeight.getValue().floatValue());
            button.render(context, mouseX, mouseY, delta);
            currentY += ClickGUI.moduleHeight.getValue().floatValue() + 2; // Clean spacing
        }
    }
    
    private int interpolateColor(int color1, int color2, float factor) {
        factor = Math.max(0, Math.min(1, factor));
        
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (handleHeaderClick(mouseX, mouseY, button)) {
            return;
        }

        if (open) {
            buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        }
    }

    private boolean handleHeaderClick(int mouseX, int mouseY, int button) {
        if (hovered) {
            if (button == 1) { // Right click - toggle open/close
                setOpen(!open);
                return true;
            } else if (button == 0) { // Left click - start dragging
                dragging = true;
                prevX = x - mouseX;
                prevY = y - mouseY;
                return true;
            }
        }
        return false;
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
        
        if (open) {
            buttons.forEach(b -> b.mouseReleased(mouseX, mouseY, button));
        }
    }

    public void keyPressed(int keyCode) {
        if (open) {
            buttons.forEach(b -> b.keyPressed(keyCode));
        }
    }

    public void charTyped(char key, int modifier) {
        if (open) {
            buttons.forEach(b -> b.charTyped(key, modifier));
        }
    }

    public void tick() {
        buttons.forEach(ModuleButton::tick);
    }

    private float getModuleAreaHeight() {
        return buttons.size() * (ClickGUI.moduleHeight.getValue().floatValue() + 2) + 4; // Added padding
    }

    // Getters and setters
    public float getX() { return x; }
    public void setX(float x) { this.x = x; }
    public float getY() { return y; }
    public void setY(float y) { this.y = y; }
    public boolean isDragging() { return dragging; }
    public void setDragging(boolean dragging) { this.dragging = dragging; }
    public boolean isOpen() { return open; }
    public void setOpen(boolean open) { this.open = open; }
    public ModuleCategory getCategory() { return category; }
    public List<ModuleButton> getButtons() { return buttons; }
}