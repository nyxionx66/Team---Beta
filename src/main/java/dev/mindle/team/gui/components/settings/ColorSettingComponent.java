package dev.mindle.team.gui.components.settings;

import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.setting.ColorSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;

public class ColorSettingComponent extends BaseSettingComponent<ColorSetting> {
    private final AnimationUtils.HoverAnimation pickerAnimation = new AnimationUtils.HoverAnimation();
    private boolean pickerOpen = false;
    private boolean draggingHue = false;
    private boolean draggingSaturation = false;
    
    private static final int PICKER_WIDTH = 200;
    private static final int PICKER_HEIGHT = 150;
    private static final int HUE_BAR_WIDTH = 20;
    
    public ColorSettingComponent(ColorSetting setting) {
        super(setting);
    }
    
    @Override
    protected void renderControl(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlY = y + (height - ThemeManager.ComponentSize.BUTTON_HEIGHT) / 2;
        
        boolean controlHovered = isMouseOverControl(mouseX, mouseY);
        pickerAnimation.update(controlHovered || pickerOpen, delta);
        
        // Color preview button
        int colorValue = setting.getValue();
        RenderUtils.drawRoundedRect(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(colorValue, alpha));
        
        RenderUtils.drawRoundedBorder(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, 2, theme.applyAlpha(theme.getBorder(), alpha));
        
        // Hex value text
        String hexText = setting.getHexString();
        int textColor = getContrastColor(colorValue);
        RenderUtils.drawCenteredText(context, hexText, controlX, 
            controlY + (ThemeManager.ComponentSize.BUTTON_HEIGHT - context.getTextRenderer().fontHeight) / 2, 
            getControlWidth(), theme.applyAlpha(textColor, alpha));
        
        // Render color picker if open
        if (pickerOpen) {
            renderColorPicker(context, controlX, controlY + ThemeManager.ComponentSize.BUTTON_HEIGHT + 4, 
                mouseX, mouseY, alpha);
        }
    }
    
    private void renderColorPicker(DrawContext context, int pickerX, int pickerY, int mouseX, int mouseY, float alpha) {
        // Adjust position if picker would go off screen
        if (pickerX + PICKER_WIDTH > x + width) {
            pickerX = x + width - PICKER_WIDTH;
        }
        
        // Picker background with shadow
        RenderUtils.drawShadow(context, pickerX, pickerY, PICKER_WIDTH, PICKER_HEIGHT, 6, theme.getShadow());
        RenderUtils.drawRoundedRect(context, pickerX, pickerY, PICKER_WIDTH, PICKER_HEIGHT,
            ThemeManager.BorderRadius.MEDIUM, theme.applyAlpha(theme.getSurfaceVariant(), alpha));
        
        RenderUtils.drawRoundedBorder(context, pickerX, pickerY, PICKER_WIDTH, PICKER_HEIGHT,
            ThemeManager.BorderRadius.MEDIUM, 1, theme.applyAlpha(theme.getBorder(), alpha));
        
        // Get current color HSB values
        Color currentColor = setting.getColor();
        float[] hsb = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        
        // Saturation/Brightness area
        int sbWidth = PICKER_WIDTH - HUE_BAR_WIDTH - 3 * ThemeManager.Spacing.SMALL;
        int sbHeight = PICKER_HEIGHT - 2 * ThemeManager.Spacing.SMALL - 30; // Space for hex input
        int sbX = pickerX + ThemeManager.Spacing.SMALL;
        int sbY = pickerY + ThemeManager.Spacing.SMALL;
        
        renderSaturationBrightnessArea(context, sbX, sbY, sbWidth, sbHeight, hsb[0], hsb[1], hsb[2], alpha);
        
        // Hue bar
        int hueX = sbX + sbWidth + ThemeManager.Spacing.SMALL;
        int hueY = sbY;
        int hueHeight = sbHeight;
        
        renderHueBar(context, hueX, hueY, HUE_BAR_WIDTH, hueHeight, hsb[0], alpha);
        
        // Hex input
        int hexY = pickerY + PICKER_HEIGHT - 25;
        String hexText = setting.getHexString();
        context.drawText(context.getTextRenderer(), "Hex: " + hexText, pickerX + ThemeManager.Spacing.SMALL, hexY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
    }
    
    private void renderSaturationBrightnessArea(DrawContext context, int areaX, int areaY, int areaWidth, int areaHeight, 
                                               float hue, float saturation, float brightness, float alpha) {
        // Render saturation/brightness gradient (simplified)
        for (int x = 0; x < areaWidth; x += 2) {
            for (int y = 0; y < areaHeight; y += 2) {
                float s = (float) x / areaWidth;
                float b = 1.0f - (float) y / areaHeight;
                
                int color = Color.HSBtoRGB(hue, s, b);
                context.fill(areaX + x, areaY + y, areaX + x + 2, areaY + y + 2, 
                    theme.applyAlpha(color | 0xFF000000, alpha));
            }
        }
        
        // Current selection indicator
        int indicatorX = areaX + (int) (saturation * areaWidth) - 4;
        int indicatorY = areaY + (int) ((1.0f - brightness) * areaHeight) - 4;
        
        RenderUtils.drawRoundedRect(context, indicatorX, indicatorY, 8, 8, 4, 
            theme.applyAlpha(0xFFFFFFFF, alpha));
        RenderUtils.drawRoundedBorder(context, indicatorX, indicatorY, 8, 8, 4, 1, 
            theme.applyAlpha(0xFF000000, alpha));
    }
    
    private void renderHueBar(DrawContext context, int barX, int barY, int barWidth, int barHeight, 
                             float currentHue, float alpha) {
        // Render hue gradient
        for (int y = 0; y < barHeight; y += 2) {
            float hue = (float) y / barHeight;
            int color = Color.HSBtoRGB(hue, 1.0f, 1.0f);
            context.fill(barX, barY + y, barX + barWidth, barY + y + 2, 
                theme.applyAlpha(color | 0xFF000000, alpha));
        }
        
        // Current hue indicator
        int indicatorY = barY + (int) (currentHue * barHeight) - 2;
        context.fill(barX - 2, indicatorY, barX + barWidth + 2, indicatorY + 4, 
            theme.applyAlpha(0xFFFFFFFF, alpha));
    }
    
    private int getContrastColor(int backgroundColor) {
        Color bgColor = new Color(backgroundColor, true);
        int luminance = (int) (0.299 * bgColor.getRed() + 0.587 * bgColor.getGreen() + 0.114 * bgColor.getBlue());
        return luminance > 128 ? 0xFF000000 : 0xFFFFFFFF;
    }
    
    @Override
    protected int getControlWidth() {
        return 60;
    }
    
    @Override
    public int getHeight() {
        int baseHeight = ThemeManager.ComponentSize.SETTING_ROW_HEIGHT;
        if (pickerOpen) {
            return baseHeight + PICKER_HEIGHT + 8;
        }
        return baseHeight;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            // Close picker if clicked outside
            if (pickerOpen) {
                pickerOpen = false;
                return true;
            }
            return false;
        }
        
        if (button == 0) {
            if (pickerOpen) {
                // Handle color picker interactions
                int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
                int pickerX = controlX;
                int pickerY = y + (height - getPickerHeight()) / 2 + ThemeManager.ComponentSize.BUTTON_HEIGHT + 4;
                
                // Adjust position if picker would go off screen
                if (pickerX + PICKER_WIDTH > x + width) {
                    pickerX = x + width - PICKER_WIDTH;
                }
                
                // Check saturation/brightness area
                int sbWidth = PICKER_WIDTH - HUE_BAR_WIDTH - 3 * ThemeManager.Spacing.SMALL;
                int sbHeight = PICKER_HEIGHT - 2 * ThemeManager.Spacing.SMALL - 30;
                int sbX = pickerX + ThemeManager.Spacing.SMALL;
                int sbY = pickerY + ThemeManager.Spacing.SMALL;
                
                if (RenderUtils.isPointInRect(mouseX, mouseY, sbX, sbY, sbWidth, sbHeight)) {
                    draggingSaturation = true;
                    updateSaturationBrightness(mouseX, mouseY, sbX, sbY, sbWidth, sbHeight);
                    return true;
                }
                
                // Check hue bar
                int hueX = sbX + sbWidth + ThemeManager.Spacing.SMALL;
                int hueY = sbY;
                int hueHeight = sbHeight;
                
                if (RenderUtils.isPointInRect(mouseX, mouseY, hueX, hueY, HUE_BAR_WIDTH, hueHeight)) {
                    draggingHue = true;
                    updateHue(mouseY, hueY, hueHeight);
                    return true;
                }
            } else if (isMouseOverControl(mouseX, mouseY)) {
                // Open color picker
                pickerOpen = true;
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            draggingHue = false;
            draggingSaturation = false;
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0 && pickerOpen) {
            int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
            int pickerX = controlX;
            int pickerY = y + (height - getPickerHeight()) / 2 + ThemeManager.ComponentSize.BUTTON_HEIGHT + 4;
            
            // Adjust position if picker would go off screen
            if (pickerX + PICKER_WIDTH > x + width) {
                pickerX = x + width - PICKER_WIDTH;
            }
            
            if (draggingSaturation) {
                int sbWidth = PICKER_WIDTH - HUE_BAR_WIDTH - 3 * ThemeManager.Spacing.SMALL;
                int sbHeight = PICKER_HEIGHT - 2 * ThemeManager.Spacing.SMALL - 30;
                int sbX = pickerX + ThemeManager.Spacing.SMALL;
                int sbY = pickerY + ThemeManager.Spacing.SMALL;
                
                updateSaturationBrightness(mouseX, mouseY, sbX, sbY, sbWidth, sbHeight);
                return true;
            }
            
            if (draggingHue) {
                int sbWidth = PICKER_WIDTH - HUE_BAR_WIDTH - 3 * ThemeManager.Spacing.SMALL;
                int sbHeight = PICKER_HEIGHT - 2 * ThemeManager.Spacing.SMALL - 30;
                int hueY = pickerY + ThemeManager.Spacing.SMALL;
                
                updateHue(mouseY, hueY, sbHeight);
                return true;
            }
        }
        
        return false;
    }
    
    private void updateSaturationBrightness(double mouseX, double mouseY, int areaX, int areaY, int areaWidth, int areaHeight) {
        float saturation = MathHelper.clamp((float) (mouseX - areaX) / areaWidth, 0.0f, 1.0f);
        float brightness = MathHelper.clamp(1.0f - (float) (mouseY - areaY) / areaHeight, 0.0f, 1.0f);
        
        Color currentColor = setting.getColor();
        float[] hsb = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        
        setting.setFromHSB(hsb[0], saturation, brightness);
    }
    
    private void updateHue(double mouseY, int barY, int barHeight) {
        float hue = MathHelper.clamp((float) (mouseY - barY) / barHeight, 0.0f, 1.0f);
        
        Color currentColor = setting.getColor();
        float[] hsb = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null);
        
        setting.setFromHSB(hue, hsb[1], hsb[2]);
    }
    
    private int getPickerHeight() {
        return pickerOpen ? PICKER_HEIGHT + 8 : ThemeManager.ComponentSize.BUTTON_HEIGHT;
    }
}