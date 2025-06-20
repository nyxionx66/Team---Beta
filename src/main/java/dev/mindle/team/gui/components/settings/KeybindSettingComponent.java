package dev.mindle.team.gui.components.settings;

import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.setting.KeybindSetting;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

public class KeybindSettingComponent extends BaseSettingComponent<KeybindSetting> {
    private boolean waitingForKey = false;
    
    public KeybindSettingComponent(KeybindSetting setting) {
        super(setting);
    }
    
    @Override
    protected void renderControl(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlY = y + (height - ThemeManager.ComponentSize.BUTTON_HEIGHT) / 2;
        
        boolean controlHovered = isMouseOverControl(mouseX, mouseY);
        
        // Button background
        int buttonBg;
        if (waitingForKey) {
            buttonBg = theme.applyAlpha(theme.getWarning(), alpha);
        } else if (controlHovered) {
            buttonBg = theme.applyAlpha(theme.getHover(), alpha);
        } else {
            buttonBg = theme.applyAlpha(theme.getSurface(), alpha);
        }
        
        RenderUtils.drawRoundedRect(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, buttonBg);
        
        RenderUtils.drawRoundedBorder(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, 1, theme.applyAlpha(theme.getBorder(), alpha));
        
        // Button text
        String buttonText;
        if (waitingForKey) {
            buttonText = "Press key...";
        } else {
            buttonText = setting.getKeyName();
        }
        
        int textColor = waitingForKey ? theme.getTextPrimary() : 
            RenderUtils.getTextColor(true, controlHovered);
        
        RenderUtils.drawCenteredText(context, buttonText, controlX, 
            controlY + (ThemeManager.ComponentSize.BUTTON_HEIGHT - RenderUtils.getTextRenderer().fontHeight) / 2, 
            getControlWidth(), theme.applyAlpha(textColor, alpha));
    }
    
    @Override
    protected int getControlWidth() {
        return 80;
    }
    
    @Override
    public int getHeight() {
        return ThemeManager.ComponentSize.SETTING_ROW_HEIGHT;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        if (button == 0 && isMouseOverControl(mouseX, mouseY)) {
            waitingForKey = true;
            return true;
        } else if (button == 1 && isMouseOverControl(mouseX, mouseY)) {
            // Right click to clear keybind
            setting.setValue(GLFW.GLFW_KEY_UNKNOWN);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                // Cancel keybind setting
                waitingForKey = false;
            } else {
                // Set new keybind
                setting.setValue(keyCode);
                waitingForKey = false;
            }
            return true;
        }
        
        return false;
    }
}