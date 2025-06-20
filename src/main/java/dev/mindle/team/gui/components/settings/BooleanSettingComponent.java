package dev.mindle.team.gui.components.settings;

import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

public class BooleanSettingComponent extends BaseSettingComponent<BooleanSetting> {
    
    public BooleanSettingComponent(BooleanSetting setting) {
        super(setting);
    }
    
    @Override
    protected void renderControl(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlY = y + (height - ThemeManager.ComponentSize.TOGGLE_HEIGHT) / 2;
        
        boolean controlHovered = isMouseOverControl(mouseX, mouseY);
        boolean enabled = setting.getValue();
        
        RenderUtils.drawToggle(context, controlX, controlY, 
            ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT, 
            enabled, controlHovered);
    }
    
    @Override
    protected int getControlWidth() {
        return ThemeManager.ComponentSize.TOGGLE_WIDTH;
    }
    
    @Override
    protected int getControlHeight() {
        return ThemeManager.ComponentSize.TOGGLE_HEIGHT;
    }
    
    @Override
    public int getHeight() {
        return ThemeManager.ComponentSize.SETTING_ROW_HEIGHT;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        if (button == 0 && isMouseOverControl(mouseX, mouseY)) {
            setting.toggle();
            return true;
        }
        
        return false;
    }
}