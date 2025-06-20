package dev.mindle.team.gui.clickgui;

import dev.mindle.team.module.Module;
import dev.mindle.team.module.impl.client.ClickGUI;
import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton {
    private final Module module;
    private final List<SettingElement> elements;
    private float x, y, width, height;
    private boolean hovered;
    private boolean open;
    private boolean binding = false;

    public ModuleButton(Module module) {
        this.module = module;
        this.elements = new ArrayList<>();
        
        // Create setting elements for the module
        for (Setting<?> setting : module.getSettings()) {
            if (!setting.getName().equals("Enabled")) {
                elements.add(new SettingElement(setting));
            }
        }
    }

    public void init() {
        elements.forEach(SettingElement::init);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, width, height);
        
        if (hovered && ClickGUI.descriptions.getValue()) {
            ClickGuiScreen.currentDescription = module.getDescription();
        }

        // Button background
        int buttonColor = module.isEnabled() ? 0xFF4A4A4A : 0xFF2A2A2A;
        if (hovered) {
            buttonColor = module.isEnabled() ? 0xFF5A5A5A : 0xFF3A3A3A;
        }
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), buttonColor);

        // Module name
        int textColor = module.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA;
        context.drawText(context.getMatrices().peek().getPositionMatrix(), module.getName(), 
            (int) (x + 5), (int) (y + height / 2 - 4), textColor, true);

        // Show keybind
        if (module.hasKeybind() && !binding) {
            String keybindName = getKeybindName();
            int keybindWidth = keybindName.length() * 6; // Rough estimate
            context.drawText(context.getMatrices().peek().getPositionMatrix(), keybindName, 
                (int) (x + width - keybindWidth - 5), (int) (y + height / 2 - 4), 0xFF888888, true);
        }

        if (binding) {
            context.drawText(context.getMatrices().peek().getPositionMatrix(), "Press Key...", 
                (int) (x + width - 60), (int) (y + height / 2 - 4), 0xFFFFFF00, true);
        }

        // Render setting elements if open
        if (open && !elements.isEmpty()) {
            float currentY = y + height;
            
            // Background for settings area
            float settingsHeight = elements.size() * 15;
            context.fill((int) x, (int) currentY, (int) (x + width), (int) (currentY + settingsHeight), 0xFF151515);
            
            for (SettingElement element : elements) {
                element.setX(x + 2);
                element.setY(currentY);
                element.setWidth(width - 4);
                element.setHeight(13);
                element.render(context, mouseX, mouseY, delta);
                currentY += 15;
            }
        }
    }

    private String getKeybindName() {
        if (!module.hasKeybind()) {
            return "NONE";
        }
        return module.getKeybind().getKeyName();
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (binding) {
            if (button >= 0 && button <= 2) { // Mouse buttons
                // Handle mouse button binding if needed
                binding = false;
            }
            return;
        }

        if (hovered) {
            if (button == 0) { // Left click - toggle module
                module.toggle();
            } else if (button == 1 && !elements.isEmpty()) { // Right click - open settings
                open = !open;
            } else if (button == 2) { // Middle click - bind module
                binding = true;
            }
        }

        if (open && !elements.isEmpty()) {
            elements.forEach(e -> e.mouseClicked(mouseX, mouseY, button));
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (open) {
            elements.forEach(e -> e.mouseReleased(mouseX, mouseY, button));
        }
    }

    public void keyPressed(int keyCode) {
        if (binding) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                // Clear keybind - would need to implement this in the Module class
                binding = false;
            } else {
                // Set new keybind - would need to implement this in the Module class
                binding = false;
            }
        }

        if (open) {
            elements.forEach(e -> e.keyPressed(keyCode));
        }
    }

    public void charTyped(char key, int modifier) {
        if (open) {
            elements.forEach(e -> e.charTyped(key, modifier));
        }
    }

    public void tick() {
        elements.forEach(SettingElement::tick);
    }

    // Getters and setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }
    public boolean isOpen() { return open; }
}