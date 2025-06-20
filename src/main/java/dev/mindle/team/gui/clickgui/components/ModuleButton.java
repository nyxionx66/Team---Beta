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
    
    // Animation fields
    private float hoverAnimation = 0.0f;
    private float enableAnimation = 0.0f;
    private float openAnimation = 0.0f;
    private long lastUpdate = System.currentTimeMillis();

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
        
        updateAnimations();
        
        if (hovered && ClickGUI.descriptions.getValue()) {
            ClickGuiScreen.currentDescription = module.getDescription();
        }

        renderButton(context);
        renderText(context);
        
        if (open && !elements.isEmpty()) {
            renderSettingsElements(context, mouseX, mouseY, delta);
        }
    }
    
    private void updateAnimations() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdate) / 1000.0f;
        lastUpdate = currentTime;
        
        // Hover animation
        float hoverTarget = hovered ? 1.0f : 0.0f;
        hoverAnimation = lerp(hoverAnimation, hoverTarget, deltaTime * 8.0f);
        
        // Enable animation  
        float enableTarget = module.isEnabled() ? 1.0f : 0.0f;
        enableAnimation = lerp(enableAnimation, enableTarget, deltaTime * 6.0f);
        
        // Open animation
        float openTarget = open ? 1.0f : 0.0f;
        openAnimation = lerp(openAnimation, openTarget, deltaTime * 10.0f);
    }
    
    private float lerp(float a, float b, float t) {
        return a + (b - a) * Math.min(1.0f, t);
    }

    private void renderButton(DrawContext context) {
        // Clean black and white design
        int bgColor = 0xFF000000; // Black background
        int borderColor = 0xFFFFFFFF; // White border
        int hoverBgColor = 0xFF333333; // Dark gray for hover
        int enabledBgColor = 0xFFFFFFFF; // White for enabled
        int enabledTextColor = 0xFF000000; // Black text on white
        
        // Determine colors based on state
        int finalBgColor = bgColor;
        if (module.isEnabled()) {
            finalBgColor = enabledBgColor;
        } else if (hovered) {
            finalBgColor = hoverBgColor;
        }
        
        // Main button background
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), finalBgColor);
        
        // Clean borders
        context.fill((int) x, (int) y, (int) (x + width), (int) y + 1, borderColor); // Top
        context.fill((int) x, (int) (y + height - 1), (int) (x + width), (int) (y + height), borderColor); // Bottom
        context.fill((int) x, (int) y, (int) x + 1, (int) (y + height), borderColor); // Left
        context.fill((int) (x + width - 1), (int) y, (int) (x + width), (int) (y + height), borderColor); // Right
        
        // Add binding indicator with minimal design
        if (binding) {
            // Simple flashing border
            if ((System.currentTimeMillis() / 300) % 2 == 0) {
                context.fill((int) x + 1, (int) y + 1, (int) (x + width - 1), (int) y + 2, 0xFFFFFFFF);
                context.fill((int) x + 1, (int) (y + height - 2), (int) (x + width - 1), (int) (y + height - 1), 0xFFFFFFFF);
                context.fill((int) x + 1, (int) y + 1, (int) x + 2, (int) (y + height - 1), 0xFFFFFFFF);
                context.fill((int) (x + width - 2), (int) y + 1, (int) (x + width - 1), (int) (y + height - 1), 0xFFFFFFFF);
            }
        }
    }
    
    private int interpolateColor(int color1, int color2, float factor) {
        factor = Math.max(0, Math.min(1, factor));
        
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int) (a1 + (a2 - a1) * factor);
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private void renderText(DrawContext context) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        // Clean black and white text colors
        int textColor = module.isEnabled() ? 0xFF000000 : 0xFFFFFFFF; // Black on white, white on black
        
        // Module name with clean spacing
        context.drawText(mc.textRenderer, module.getName(), 
            (int) (x + 6), (int) (y + height / 2 - 4), textColor, false);

        // Keybind display with minimal design
        if (binding) {
            String bindingText = "...";
            int bindingWidth = mc.textRenderer.getWidth(bindingText);
            context.drawText(mc.textRenderer, bindingText, 
                (int) (x + width - bindingWidth - 6), (int) (y + height / 2 - 4), textColor, false);
        } else if (module.hasKeybind()) {
            String keybindName = getKeybindName();
            int keybindWidth = mc.textRenderer.getWidth(keybindName);
            
            // Simple bracket notation for keybind
            String displayText = "[" + keybindName + "]";
            int displayWidth = mc.textRenderer.getWidth(displayText);
            context.drawText(mc.textRenderer, displayText, 
                (int) (x + width - displayWidth - 6), (int) (y + height / 2 - 4), 
                module.isEnabled() ? 0xFF666666 : 0xFF999999, false);
        }
    }

    private void renderSettingsElements(DrawContext context, int mouseX, int mouseY, float delta) {
        if (openAnimation <= 0.01f) return; // Don't render if barely open
        
        float currentY = y + height + 1; // Small gap
        
        // Calculate animated height
        float maxSettingsHeight = elements.size() * 16 + 4; // Clean spacing
        float settingsHeight = maxSettingsHeight * openAnimation;
        
        // Clean black background with white border
        context.fill((int) x, (int) currentY, (int) (x + width), (int) (currentY + settingsHeight), 0xFF000000);
        
        // Borders for settings area
        context.fill((int) x, (int) currentY, (int) (x + width), (int) currentY + 1, 0xFFFFFFFF); // Top
        context.fill((int) x, (int) (currentY + settingsHeight - 1), (int) (x + width), (int) (currentY + settingsHeight), 0xFFFFFFFF); // Bottom
        context.fill((int) x, (int) currentY, (int) x + 1, (int) (currentY + settingsHeight), 0xFFFFFFFF); // Left
        context.fill((int) (x + width - 1), (int) currentY, (int) (x + width), (int) (currentY + settingsHeight), 0xFFFFFFFF); // Right
        
        // Render elements with clean spacing
        float elementY = currentY + 2; // Top padding
        for (int i = 0; i < elements.size(); i++) {
            if (elementY > currentY + settingsHeight - 14) break; // Don't render outside animated area
            
            AbstractElement element = elements.get(i);
            element.setX(x + 2); // Clean padding
            element.setY(elementY);
            element.setWidth(width - 4); // Clean padding
            element.setHeight(14); // Standard height
            
            // Only render if fully visible in animated area
            if (elementY + 14 <= currentY + settingsHeight) {
                element.render(context, mouseX, mouseY, delta);
            }
            
            elementY += 16; // Clean spacing
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