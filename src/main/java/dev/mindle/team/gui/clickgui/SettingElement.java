package dev.mindle.team.gui.clickgui;

import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import dev.mindle.team.module.setting.ModeSetting;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class SettingElement {
    private final Setting<?> setting;
    private float x, y, width, height;
    private boolean hovered;
    private boolean dragging = false;

    public SettingElement(Setting<?> setting) {
        this.setting = setting;
    }

    public void init() {
        // Initialize setting element
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hovered = RenderUtil.isHovered(mouseX, mouseY, x, y, width, height);
        
        if (!setting.isVisible()) {
            return;
        }

        // Setting background
        int bgColor = hovered ? 0xFF2A2A2A : 0xFF1A1A1A;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Setting name
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 3), (int) (y + 2), 0xFFCCCCCC, false);

        // Render based on setting type
        if (setting instanceof BooleanSetting boolSetting) {
            renderBooleanSetting(context, boolSetting);
        } else if (setting instanceof NumberSetting numberSetting) {
            renderNumberSetting(context, numberSetting, mouseX);
        } else if (setting instanceof ModeSetting modeSetting) {
            renderModeSetting(context, modeSetting);
        }
    }

    private void renderBooleanSetting(DrawContext context, BooleanSetting setting) {
        // Toggle button
        int toggleX = (int) (x + width - 15);
        int toggleY = (int) (y + 2);
        int toggleColor = setting.getValue() ? 0xFF4CAF50 : 0xFF444444;
        
        context.fill(toggleX, toggleY, toggleX + 10, toggleY + 9, toggleColor);
        context.drawBorder(toggleX, toggleY, 10, 9, 0xFF666666);
    }

    private void renderNumberSetting(DrawContext context, NumberSetting setting, int mouseX) {
        // Slider
        int sliderX = (int) (x + 3);
        int sliderY = (int) (y + height - 4);
        int sliderWidth = (int) (width - 6);
        
        // Slider background
        context.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, 0xFF333333);
        
        // Slider handle
        double percentage = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int handleX = (int) (sliderX + percentage * sliderWidth);
        context.fill(handleX - 1, sliderY - 1, handleX + 1, sliderY + 3, 0xFF4CAF50);
        
        // Value text
        String valueText = String.format("%.1f", setting.getValue());
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, valueText, 
            (int) (x + width - mc.textRenderer.getWidth(valueText) - 3), (int) (y + 2), 0xFFFFFFFF, false);
            
        // Handle dragging
        if (dragging) {
            double newPercentage = Math.max(0, Math.min(1, (mouseX - sliderX) / (double) sliderWidth));
            double newValue = setting.getMin() + newPercentage * (setting.getMax() - setting.getMin());
            
            // Round to increment
            if (setting.getIncrement() > 0) {
                newValue = Math.round(newValue / setting.getIncrement()) * setting.getIncrement();
            }
            
            setting.setValue(newValue);
        }
    }

    private void renderModeSetting(DrawContext context, ModeSetting setting) {
        // Current mode
        String currentMode = setting.getValue();
        MinecraftClient mc = MinecraftClient.getInstance();
        int textWidth = mc.textRenderer.getWidth(currentMode);
        context.drawText(mc.textRenderer, currentMode, 
            (int) (x + width - textWidth - 3), (int) (y + 2), 0xFF4CAF50, false);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!hovered || !setting.isVisible()) return;

        if (setting instanceof BooleanSetting boolSetting) {
            if (button == 0) {
                boolSetting.setValue(!boolSetting.getValue());
            }
        } else if (setting instanceof NumberSetting numberSetting) {
            if (button == 0) {
                dragging = true;
            }
        } else if (setting instanceof ModeSetting modeSetting) {
            if (button == 0) {
                modeSetting.cycle();
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
    }

    public void keyPressed(int keyCode) {
        // Handle key presses for settings if needed
    }

    public void charTyped(char key, int modifier) {
        // Handle character input for settings if needed
    }

    public void tick() {
        // Update setting element state if needed
    }

    // Getters and setters
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }
    public Setting<?> getSetting() { return setting; }
}