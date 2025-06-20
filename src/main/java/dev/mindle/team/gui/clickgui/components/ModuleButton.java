package dev.mindle.team.gui.clickgui.components;

import dev.mindle.team.module.Module;
import dev.mindle.team.module.impl.client.ClickGUI;
import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import dev.mindle.team.module.setting.ModeSetting;
import dev.mindle.team.gui.clickgui.elements.AbstractElement;
import dev.mindle.team.gui.clickgui.elements.BooleanElement;
import dev.mindle.team.gui.clickgui.elements.NumberElement;
import dev.mindle.team.gui.clickgui.elements.ModeElement;
import dev.mindle.team.gui.clickgui.ClickGuiScreen;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a clickable module button in the ClickGUI
 * Handles module toggling, settings display, and keybind management
 */
public class ModuleButton {
    private final Module module;
    private final List<AbstractElement> elements;
    private float x, y, width, height;
    private boolean hovered;
    private boolean open;
    private boolean binding = false;

    public ModuleButton(Module module) {
        this.module = module;
        this.elements = new ArrayList<>();
        
        // Create setting elements for the module
        initializeElements();
    }

    private void initializeElements() {
        for (Setting<?> setting : module.getSettings()) {
            if (!setting.getName().equals("Enabled")) {
                if (setting instanceof BooleanSetting) {
                    elements.add(new BooleanElement(setting));
                } else if (setting instanceof NumberSetting) {
                    elements.add(new NumberElement(setting));
                } else if (setting instanceof ModeSetting) {
                    elements.add(new ModeElement(setting));
                }
            }
        }
    }

    public void init() {
        elements.forEach(AbstractElement::init);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, width, height);
        
        if (hovered && ClickGUI.descriptions.getValue()) {
            ClickGuiScreen.currentDescription = module.getDescription();
        }

        renderButton(context);
        renderText(context);
        
        if (open && !elements.isEmpty()) {
            renderSettingsElements(context, mouseX, mouseY, delta);
        }
    }

    private void renderButton(DrawContext context) {
        int buttonColor = module.isEnabled() ? 0xFF4A4A4A : 0xFF2A2A2A;
        if (hovered) {
            buttonColor = module.isEnabled() ? 0xFF5A5A5A : 0xFF3A3A3A;
        }
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), buttonColor);
    }

    private void renderText(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Module name
        int textColor = module.isEnabled() ? 0xFFFFFFFF : 0xFFAAAAAA;
        context.drawText(mc.textRenderer, module.getName(), 
            (int) (x + 5), (int) (y + height / 2 - 4), textColor, true);

        // Show keybind or binding status
        if (binding) {
            context.drawText(mc.textRenderer, "Press Key...", 
                (int) (x + width - 60), (int) (y + height / 2 - 4), 0xFFFFFF00, true);
        } else if (module.hasKeybind()) {
            String keybindName = getKeybindName();
            int keybindWidth = mc.textRenderer.getWidth(keybindName);
            context.drawText(mc.textRenderer, keybindName, 
                (int) (x + width - keybindWidth - 5), (int) (y + height / 2 - 4), 0xFF888888, true);
        }
    }

    private void renderSettingsElements(DrawContext context, int mouseX, int mouseY, float delta) {
        float currentY = y + height;
        
        // Background for settings area
        float settingsHeight = elements.size() * 15;
        context.fill((int) x, (int) currentY, (int) (x + width), (int) (currentY + settingsHeight), 0xFF151515);
        
        for (AbstractElement element : elements) {
            element.setX(x + 2);
            element.setY(currentY);
            element.setWidth(width - 4);
            element.setHeight(13);
            element.render(context, mouseX, mouseY, delta);
            currentY += 15;
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
            handleBindingClick(button);
            return;
        }

        if (hovered) {
            handleButtonClick(button);
        }

        if (open && !elements.isEmpty()) {
            elements.forEach(e -> e.mouseClicked(mouseX, mouseY, button));
        }
    }

    private void handleBindingClick(int button) {
        if (button >= 0 && button <= 2) { // Mouse buttons
            // Handle mouse button binding if needed
            binding = false;
        }
    }

    private void handleButtonClick(int button) {
        switch (button) {
            case 0: // Left click - toggle module
                module.toggle();
                break;
            case 1: // Right click - open settings
                if (!elements.isEmpty()) {
                    open = !open;
                }
                break;
            case 2: // Middle click - bind module
                binding = true;
                break;
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (open) {
            elements.forEach(e -> e.mouseReleased(mouseX, mouseY, button));
        }
    }

    public void keyPressed(int keyCode) {
        if (binding) {
            handleBindingKey(keyCode);
        }

        if (open) {
            elements.forEach(e -> e.keyPressed(keyCode));
        }
    }

    private void handleBindingKey(int keyCode) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            // Clear keybind
            module.setKeybindKey(GLFW.GLFW_KEY_UNKNOWN);
            Team.getInstance().getKeybindManager().saveKeybinds();
            binding = false;
        } else {
            // Set new keybind
            module.setKeybindKey(keyCode);
            Team.getInstance().getKeybindManager().saveKeybinds();
            binding = false;
        }
    }

    public void charTyped(char key, int modifier) {
        if (open) {
            elements.forEach(e -> e.charTyped(key, modifier));
        }
    }

    public void tick() {
        elements.forEach(AbstractElement::tick);
    }

    // Getters and setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }
    public boolean isOpen() { return open; }
    public Module getModule() { return module; }
}