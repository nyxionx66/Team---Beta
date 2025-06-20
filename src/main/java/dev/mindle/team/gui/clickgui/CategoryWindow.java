package dev.mindle.team.gui.clickgui;

import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.impl.client.ClickGUI;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

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

        for (Module module : modules) {
            buttons.add(new ModuleButton(module));
        }
    }

    public void init() {
        buttons.forEach(ModuleButton::init);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, width, height);
        
        if (dragging) {
            this.x = this.prevX + mouseX;
            this.y = this.prevY + mouseY;
        }

        // Category header background
        int headerColor = 0xFF2D2D2D;
        if (hovered) {
            headerColor = 0xFF3D3D3D;
        }
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), headerColor);
        
        // Category title
        String categoryName = category.getName();
        MinecraftClient mc = MinecraftClient.getInstance();
        int textX = (int) (x + width / 2 - mc.textRenderer.getWidth(categoryName) / 2);
        context.drawText(mc.textRenderer, categoryName, textX, (int) (y + height / 2 - 4), 0xFFFFFFFF, true);

        // Module buttons (if open)
        if (open) {
            float currentY = y + height;
            
            // Background for module area
            float moduleAreaHeight = getModuleAreaHeight();
            context.fill((int) x, (int) currentY, (int) (x + width), (int) (currentY + moduleAreaHeight), 0xFF1A1A1A);
            
            for (ModuleButton button : buttons) {
                button.setX(x);
                button.setY(currentY);
                button.setWidth(width);
                button.setHeight(ClickGUI.moduleHeight.getValue().intValue());
                button.render(context, mouseX, mouseY, delta);
                currentY += ClickGUI.moduleHeight.getValue() + 1;
            }
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 1 && hovered) {
            setOpen(!open);
        }
        
        if (hovered && button == 0) {
            dragging = true;
            prevX = x - mouseX;
            prevY = y - mouseY;
        }

        if (open) {
            buttons.forEach(b -> b.mouseClicked(mouseX, mouseY, button));
        }
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
        return buttons.size() * (ClickGUI.moduleHeight.getValue() + 1);
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
}