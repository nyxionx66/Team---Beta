package dev.mindle.team.gui.components.settings;

import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.setting.ModeSetting;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ModeSettingComponent extends BaseSettingComponent<ModeSetting> {
    private final AnimationUtils.HoverAnimation dropdownAnimation = new AnimationUtils.HoverAnimation();
    private boolean dropdownOpen = false;
    private boolean dropdownHovered = false;
    
    public ModeSettingComponent(ModeSetting setting) {
        super(setting);
    }
    
    @Override
    protected void renderControl(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlY = y + (height - ThemeManager.ComponentSize.BUTTON_HEIGHT) / 2;
        
        boolean controlHovered = isMouseOverControl(mouseX, mouseY);
        dropdownAnimation.update(controlHovered || dropdownOpen, delta);
        
        // Main button background
        float hoverProgress = dropdownAnimation.getEasedProgress();
        int buttonBg = theme.applyAlpha(
            theme.interpolate(theme.getSurface(), theme.getHover(), hoverProgress), 
            alpha
        );
        
        RenderUtils.drawRoundedRect(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, buttonBg);
        
        RenderUtils.drawRoundedBorder(context, controlX, controlY, getControlWidth(), ThemeManager.ComponentSize.BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, 1, theme.applyAlpha(theme.getBorder(), alpha));
        
        // Current value text
        String currentValue = setting.getValue();
        String truncatedValue = RenderUtils.truncateText(RenderUtils.getTextRenderer(), currentValue, 
            getControlWidth() - 20); // Leave space for arrow
        
        int textX = controlX + ThemeManager.Spacing.SMALL;
        int textY = controlY + (ThemeManager.ComponentSize.BUTTON_HEIGHT - RenderUtils.getTextRenderer().fontHeight) / 2;
        
        context.drawText(RenderUtils.getTextRenderer(), truncatedValue, textX, textY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
        
        // Dropdown arrow
        String arrow = dropdownOpen ? "▲" : "▼";
        int arrowX = controlX + getControlWidth() - 16;
        context.drawText(RenderUtils.getTextRenderer(), arrow, arrowX, textY, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        
        // Render dropdown if open
        if (dropdownOpen) {
            renderDropdown(context, controlX, controlY + ThemeManager.ComponentSize.BUTTON_HEIGHT + 2, 
                mouseX, mouseY, alpha);
        }
    }
    
    private void renderDropdown(DrawContext context, int dropdownX, int dropdownY, int mouseX, int mouseY, float alpha) {
        List<String> modes = setting.getModes();
        int itemHeight = 24;
        int dropdownHeight = modes.size() * itemHeight;
        int dropdownWidth = getControlWidth();
        
        // Dropdown background with shadow
        RenderUtils.drawShadow(context, dropdownX, dropdownY, dropdownWidth, dropdownHeight, 4, theme.getShadow());
        RenderUtils.drawRoundedRect(context, dropdownX, dropdownY, dropdownWidth, dropdownHeight,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(theme.getSurfaceVariant(), alpha));
        
        RenderUtils.drawRoundedBorder(context, dropdownX, dropdownY, dropdownWidth, dropdownHeight,
            ThemeManager.BorderRadius.SMALL, 1, theme.applyAlpha(theme.getBorder(), alpha));
        
        // Render options
        String currentValue = setting.getValue();
        
        for (int i = 0; i < modes.size(); i++) {
            String mode = modes.get(i);
            int itemY = dropdownY + i * itemHeight;
            
            boolean itemHovered = RenderUtils.isPointInRect(mouseX, mouseY, dropdownX, itemY, dropdownWidth, itemHeight);
            boolean isSelected = mode.equals(currentValue);
            
            // Item background
            if (itemHovered) {
                RenderUtils.drawRoundedRect(context, dropdownX + 2, itemY + 1, dropdownWidth - 4, itemHeight - 2,
                    ThemeManager.BorderRadius.SMALL, theme.applyAlpha(theme.getHover(), alpha));
            } else if (isSelected) {
                RenderUtils.drawRoundedRect(context, dropdownX + 2, itemY + 1, dropdownWidth - 4, itemHeight - 2,
                    ThemeManager.BorderRadius.SMALL, theme.applyAlpha(theme.getPrimary(), alpha * 0.3f));
            }
            
            // Item text
            int textColor = isSelected ? theme.getPrimary() : 
                (itemHovered ? theme.getTextPrimary() : theme.getTextSecondary());
            
            int textX = dropdownX + ThemeManager.Spacing.SMALL;
            int textY = itemY + (itemHeight - RenderUtils.getTextRenderer().fontHeight) / 2;
            
            String truncatedMode = RenderUtils.truncateText(RenderUtils.getTextRenderer(), mode, 
                dropdownWidth - 2 * ThemeManager.Spacing.SMALL);
            
            context.drawText(RenderUtils.getTextRenderer(), truncatedMode, textX, textY, 
                theme.applyAlpha(textColor, alpha), false);
        }
    }
    
    @Override
    protected int getControlWidth() {
        return 100;
    }
    
    @Override
    public int getHeight() {
        int baseHeight = ThemeManager.ComponentSize.SETTING_ROW_HEIGHT;
        if (dropdownOpen) {
            int itemHeight = 24;
            int dropdownHeight = setting.getModes().size() * itemHeight;
            return baseHeight + dropdownHeight + 4; // Extra spacing
        }
        return baseHeight;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            // Close dropdown if clicked outside
            if (dropdownOpen) {
                dropdownOpen = false;
                return true;
            }
            return false;
        }
        
        if (button == 0) {
            if (dropdownOpen) {
                // Check dropdown items
                int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
                int dropdownY = y + (height - getDropdownHeight()) / 2 + ThemeManager.ComponentSize.BUTTON_HEIGHT + 2;
                
                List<String> modes = setting.getModes();
                int itemHeight = 24;
                
                for (int i = 0; i < modes.size(); i++) {
                    int itemY = dropdownY + i * itemHeight;
                    if (RenderUtils.isPointInRect(mouseX, mouseY, controlX, itemY, getControlWidth(), itemHeight)) {
                        setting.setValue(modes.get(i));
                        dropdownOpen = false;
                        return true;
                    }
                }
            } else if (isMouseOverControl(mouseX, mouseY)) {
                // Open dropdown
                dropdownOpen = true;
                return true;
            }
        }
        
        return false;
    }
    
    private int getDropdownHeight() {
        if (!dropdownOpen) return ThemeManager.ComponentSize.BUTTON_HEIGHT;
        
        int itemHeight = 24;
        int dropdownHeight = setting.getModes().size() * itemHeight;
        return ThemeManager.ComponentSize.BUTTON_HEIGHT + dropdownHeight + 4;
    }
}