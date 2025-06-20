package dev.mindle.team.gui.components;

import dev.mindle.team.Team;
import dev.mindle.team.gui.ClickGUIScreen;
import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public class CategoryPanel extends BasePanel {
    private static final int CATEGORY_HEIGHT = 48;
    private static final int CATEGORY_SPACING = 4;
    
    private final Map<ModuleCategory, AnimationUtils.HoverAnimation> categoryAnimations = new HashMap<>();
    private final Map<ModuleCategory, Integer> categoryPositions = new HashMap<>();
    
    public CategoryPanel(ClickGUIScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        
        // Initialize animations for each category
        for (ModuleCategory category : ModuleCategory.values()) {
            categoryAnimations.put(category, new AnimationUtils.HoverAnimation());
        }
    }
    
    @Override
    public void init() {
        // Calculate category positions
        int currentY = y + ThemeManager.Spacing.PANEL_PADDING;
        
        for (ModuleCategory category : ModuleCategory.values()) {
            categoryPositions.put(category, currentY);
            currentY += CATEGORY_HEIGHT + CATEGORY_SPACING;
        }
    }
    
    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        int textRenderer = context.getTextRenderer().fontHeight;
        
        // Render title
        String title = "Categories";
        int titleY = y + ThemeManager.Spacing.PANEL_PADDING;
        context.drawText(context.getTextRenderer(), title, 
            x + ThemeManager.Spacing.PANEL_PADDING, titleY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
        
        // Render categories
        for (ModuleCategory category : ModuleCategory.values()) {
            renderCategory(context, category, mouseX, mouseY, delta, alpha);
        }
        
        // Render statistics
        renderStatistics(context, alpha);
    }
    
    private void renderCategory(DrawContext context, ModuleCategory category, int mouseX, int mouseY, float delta, float alpha) {
        Integer categoryY = categoryPositions.get(category);
        if (categoryY == null) return;
        
        boolean isSelected = parent.getSelectedCategory() == category;
        boolean isHovered = RenderUtils.isPointInRect(mouseX, mouseY, 
            x + ThemeManager.Spacing.SMALL, categoryY, 
            width - ThemeManager.Spacing.MEDIUM, CATEGORY_HEIGHT);
        
        // Update animation
        AnimationUtils.HoverAnimation animation = categoryAnimations.get(category);
        animation.update(isHovered, delta);
        
        // Calculate colors and positions
        int categoryX = x + ThemeManager.Spacing.SMALL;
        int categoryWidth = width - ThemeManager.Spacing.MEDIUM;
        
        int backgroundColor;
        if (isSelected) {
            backgroundColor = theme.applyAlpha(theme.getPrimary(), alpha * 0.8f);
        } else {
            float hoverProgress = animation.getEasedProgress();
            backgroundColor = theme.applyAlpha(
                theme.interpolate(theme.getSurface(), theme.getHover(), hoverProgress), 
                alpha
            );
        }
        
        // Render category background
        RenderUtils.drawRoundedRect(context, categoryX, categoryY, categoryWidth, CATEGORY_HEIGHT, 
            ThemeManager.BorderRadius.MEDIUM, backgroundColor);
        
        // Render category icon and text
        String icon = category.getIcon();
        String name = category.getName();
        
        int iconX = categoryX + ThemeManager.Spacing.MEDIUM;
        int textX = iconX + 20; // Icon width + spacing
        int textY = categoryY + (CATEGORY_HEIGHT - context.getTextRenderer().fontHeight) / 2;
        
        // Icon
        int iconColor = isSelected ? theme.getTextPrimary() : theme.getTextSecondary();
        context.drawText(context.getTextRenderer(), icon, iconX, textY, 
            theme.applyAlpha(iconColor, alpha), false);
        
        // Text
        int textColor = isSelected ? theme.getTextPrimary() : 
            RenderUtils.getTextColor(true, isHovered);
        context.drawText(context.getTextRenderer(), name, textX, textY, 
            theme.applyAlpha(textColor, alpha), false);
        
        // Module count
        int moduleCount = Team.getInstance().getModuleManager().getCategoryCount(category);
        int enabledCount = Team.getInstance().getModuleManager().getEnabledCategoryCount(category);
        
        String countText = enabledCount + "/" + moduleCount;
        int countColor = isSelected ? theme.getTextSecondary() : theme.getTextDisabled();
        RenderUtils.drawRightAlignedText(context, countText, 
            categoryX, textY, categoryWidth - ThemeManager.Spacing.MEDIUM, 
            theme.applyAlpha(countColor, alpha));
        
        // Selection indicator
        if (isSelected) {
            int indicatorWidth = 3;
            int indicatorHeight = CATEGORY_HEIGHT - ThemeManager.Spacing.MEDIUM;
            int indicatorX = categoryX + categoryWidth - indicatorWidth;
            int indicatorY = categoryY + ThemeManager.Spacing.SMALL;
            
            RenderUtils.drawRoundedRect(context, indicatorX, indicatorY, indicatorWidth, indicatorHeight,
                indicatorWidth / 2, theme.applyAlpha(theme.getTextPrimary(), alpha));
        }
    }
    
    private void renderStatistics(DrawContext context, float alpha) {
        int statsY = y + height - 100; // Position at bottom
        int statsX = x + ThemeManager.Spacing.PANEL_PADDING;
        
        // Total modules
        int totalModules = Team.getInstance().getModuleManager().getTotalModules();
        int enabledModules = Team.getInstance().getModuleManager().getEnabledCount();
        
        String statsTitle = "Statistics";
        String totalText = "Total: " + totalModules;
        String enabledText = "Enabled: " + enabledModules;
        
        int lineHeight = context.getTextRenderer().fontHeight + 4;
        
        context.drawText(context.getTextRenderer(), statsTitle, statsX, statsY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
        
        context.drawText(context.getTextRenderer(), totalText, statsX, statsY + lineHeight, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        
        context.drawText(context.getTextRenderer(), enabledText, statsX, statsY + lineHeight * 2, 
            theme.applyAlpha(theme.getSuccess(), alpha), false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        if (button == 0) { // Left click
            for (ModuleCategory category : ModuleCategory.values()) {
                Integer categoryY = categoryPositions.get(category);
                if (categoryY == null) continue;
                
                if (RenderUtils.isPointInRect(mouseX, mouseY, 
                    x + ThemeManager.Spacing.SMALL, categoryY, 
                    width - ThemeManager.Spacing.MEDIUM, CATEGORY_HEIGHT)) {
                    
                    parent.selectCategory(category);
                    return true;
                }
            }
        }
        
        return false;
    }
}