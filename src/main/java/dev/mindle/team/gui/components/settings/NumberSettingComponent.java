package dev.mindle.team.gui.components.settings;

import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

public class NumberSettingComponent extends BaseSettingComponent<NumberSetting> {
    private boolean dragging = false;
    private double lastMouseX = 0;
    
    public NumberSettingComponent(NumberSetting setting) {
        super(setting);
    }
    
    @Override
    protected void renderControl(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlY = y + (height - ThemeManager.ComponentSize.SLIDER_HEIGHT) / 2;
        
        boolean controlHovered = isMouseOverControl(mouseX, mouseY);
        
        // Calculate slider value (0.0 to 1.0)
        double value = setting.getValue();
        double min = setting.getMin();
        double max = setting.getMax();
        float sliderValue = (float) ((value - min) / (max - min));
        
        // Render slider
        RenderUtils.drawSlider(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.SLIDER_HEIGHT, 
            sliderValue, controlHovered, dragging);
        
        // Render value text
        String valueText = formatValue(value);
        int textWidth = context.getTextRenderer().getWidth(valueText);
        int textX = controlX + (getControlWidth() - textWidth) / 2;
        int textY = controlY - context.getTextRenderer().fontHeight - 4;
        
        // Background for value text
        int textBgWidth = textWidth + 16;
        int textBgX = textX - 8;
        int textBgY = textY - 4;
        RenderUtils.drawRoundedRect(context, textBgX, textBgY, textBgWidth, context.getTextRenderer().fontHeight + 8,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(theme.getSurfaceVariant(), alpha));
        
        context.drawText(context.getTextRenderer(), valueText, textX, textY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
    }
    
    private String formatValue(double value) {
        // Format the value based on increment
        double increment = setting.getIncrement();
        if (increment >= 1.0) {
            return String.valueOf((int) value);
        } else if (increment >= 0.1) {
            return String.format("%.1f", value);
        } else {
            return String.format("%.2f", value);
        }
    }
    
    @Override
    protected int getControlWidth() {
        return 120;
    }
    
    @Override
    protected int getControlHeight() {
        return ThemeManager.ComponentSize.SLIDER_HEIGHT;
    }
    
    @Override
    public int getHeight() {
        return ThemeManager.ComponentSize.SETTING_ROW_HEIGHT + 10; // Extra space for value text
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        if (button == 0 && isMouseOverControl(mouseX, mouseY)) {
            dragging = true;
            lastMouseX = mouseX;
            updateValueFromMouse(mouseX);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) {
            updateValueFromMouse(mouseX);
            return true;
        }
        return false;
    }
    
    private void updateValueFromMouse(double mouseX) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlWidth = getControlWidth();
        
        // Calculate percentage based on mouse position
        double percentage = MathHelper.clamp((mouseX - controlX) / controlWidth, 0.0, 1.0);
        
        // Convert to setting value
        double min = setting.getMin();
        double max = setting.getMax();
        double newValue = min + (max - min) * percentage;
        
        // Snap to increment
        double increment = setting.getIncrement();
        if (increment > 0) {
            newValue = Math.round(newValue / increment) * increment;
        }
        
        setting.setValue(newValue);
    }
}