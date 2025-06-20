package dev.mindle.team.gui.components;

import dev.mindle.team.gui.ClickGUIScreen;
import dev.mindle.team.gui.components.settings.*;
import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.setting.*;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class SettingsPanel extends BasePanel {
    private Module currentModule;
    private final List<BaseSettingComponent<?>> settingComponents = new ArrayList<>();
    private float scrollOffset = 0.0f;
    private float targetScrollOffset = 0.0f;
    private final AnimationUtils.FadeAnimation moduleChangeAnimation = new AnimationUtils.FadeAnimation();
    
    public SettingsPanel(ClickGUIScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        moduleChangeAnimation.setImmediate(true);
    }
    
    @Override
    public void init() {
        refreshSettings();
    }
    
    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        // Update animations
        scrollOffset = AnimationUtils.updateAnimation(scrollOffset, targetScrollOffset, delta * 6.0f);
        moduleChangeAnimation.update(currentModule != null, delta);
        
        float moduleAlpha = moduleChangeAnimation.getAlpha() * alpha;
        
        if (currentModule == null) {
            renderEmptyState(context, alpha);
            return;
        }
        
        // Render module header
        renderModuleHeader(context, moduleAlpha);
        
        // Setup scissor for scrolling
        int contentY = y + 60; // After header
        int contentHeight = height - 60;
        context.enableScissor(x, contentY, x + width, contentY + contentHeight);
        
        // Render settings
        renderSettings(context, mouseX, mouseY, delta, moduleAlpha);
        
        context.disableScissor();
        
        // Render scrollbar
        renderScrollbar(context, moduleAlpha);
    }
    
    private void renderModuleHeader(DrawContext context, float alpha) {
        int headerHeight = 60;
        int headerX = x + ThemeManager.Spacing.PANEL_PADDING;
        int headerY = y + ThemeManager.Spacing.PANEL_PADDING;
        int headerWidth = width - 2 * ThemeManager.Spacing.PANEL_PADDING;
        
        // Header background
        RenderUtils.drawRoundedRect(context, headerX, headerY, headerWidth, headerHeight, 
            ThemeManager.BorderRadius.MEDIUM, theme.applyAlpha(theme.getSurfaceVariant(), alpha));
        
        // Module name
        String moduleName = currentModule.getName();
        int nameY = headerY + ThemeManager.Spacing.MEDIUM;
        context.drawText(context.getTextRenderer(), moduleName, headerX + ThemeManager.Spacing.MEDIUM, nameY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
        
        // Module description
        String description = currentModule.getDescription();
        String truncatedDesc = RenderUtils.truncateText(context.getTextRenderer(), description, 
            headerWidth - 2 * ThemeManager.Spacing.MEDIUM);
        int descY = nameY + context.getTextRenderer().fontHeight + 4;
        context.drawText(context.getTextRenderer(), truncatedDesc, headerX + ThemeManager.Spacing.MEDIUM, descY, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        
        // Module toggle
        int toggleX = headerX + headerWidth - ThemeManager.ComponentSize.TOGGLE_WIDTH - ThemeManager.Spacing.MEDIUM;
        int toggleY = headerY + (headerHeight - ThemeManager.ComponentSize.TOGGLE_HEIGHT) / 2;
        
        boolean toggleHovered = RenderUtils.isPointInRect(parent.client.mouse.getX(), parent.client.mouse.getY(), 
            toggleX, toggleY, ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT);
        
        RenderUtils.drawToggle(context, toggleX, toggleY, 
            ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT, 
            currentModule.isEnabled(), toggleHovered);
    }
    
    private void renderSettings(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        if (settingComponents.isEmpty()) {
            renderNoSettings(context, alpha);
            return;
        }
        
        int currentY = y + 80 + (int) scrollOffset; // Start after header
        
        for (BaseSettingComponent<?> component : settingComponents) {
            int componentHeight = component.getHeight();
            
            // Skip if component is not visible
            if (currentY + componentHeight < y || currentY > y + height) {
                currentY += componentHeight + ThemeManager.Spacing.COMPONENT_MARGIN;
                continue;
            }
            
            // Update component bounds
            component.updateBounds(x + ThemeManager.Spacing.PANEL_PADDING, currentY, 
                width - 2 * ThemeManager.Spacing.PANEL_PADDING, componentHeight);
            
            // Render component
            component.render(context, mouseX, mouseY, delta, alpha);
            
            currentY += componentHeight + ThemeManager.Spacing.COMPONENT_MARGIN;
        }
    }
    
    private void renderEmptyState(DrawContext context, float alpha) {
        String emptyText = "Select a module to view settings";
        int textWidth = context.getTextRenderer().getWidth(emptyText);
        int textX = x + (width - textWidth) / 2;
        int textY = y + height / 2;
        
        context.drawText(context.getTextRenderer(), emptyText, textX, textY, 
            theme.applyAlpha(theme.getTextDisabled(), alpha), false);
    }
    
    private void renderNoSettings(DrawContext context, float alpha) {
        String noSettingsText = "This module has no settings";
        int textWidth = context.getTextRenderer().getWidth(noSettingsText);
        int textX = x + (width - textWidth) / 2;
        int textY = y + 120; // Below header
        
        context.drawText(context.getTextRenderer(), noSettingsText, textX, textY, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
    }
    
    private void renderScrollbar(DrawContext context, float alpha) {
        if (!needsScrollbar()) return;
        
        int scrollbarWidth = 6;
        int scrollbarX = x + width - scrollbarWidth - 2;
        int scrollbarHeight = height - 82; // Account for header
        int scrollbarY = y + 80;
        
        // Track
        RenderUtils.drawRoundedRect(context, scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight, 
            scrollbarWidth / 2, theme.applyAlpha(theme.getSurface(), alpha * 0.5f));
        
        // Thumb
        float maxScroll = getMaxScrollOffset();
        if (maxScroll > 0) {
            float scrollProgress = Math.abs(targetScrollOffset) / maxScroll;
            int thumbHeight = Math.max(20, (int) (scrollbarHeight * 0.3f));
            int thumbY = scrollbarY + (int) ((scrollbarHeight - thumbHeight) * scrollProgress);
            
            RenderUtils.drawRoundedRect(context, scrollbarX, thumbY, scrollbarWidth, thumbHeight, 
                scrollbarWidth / 2, theme.applyAlpha(theme.getTextSecondary(), alpha));
        }
    }
    
    private boolean needsScrollbar() {
        return getMaxScrollOffset() > 0;
    }
    
    private float getMaxScrollOffset() {
        if (settingComponents.isEmpty()) return 0;
        
        int totalHeight = 0;
        for (BaseSettingComponent<?> component : settingComponents) {
            totalHeight += component.getHeight() + ThemeManager.Spacing.COMPONENT_MARGIN;
        }
        totalHeight -= ThemeManager.Spacing.COMPONENT_MARGIN; // Remove last margin
        
        int availableHeight = height - 80 - ThemeManager.Spacing.PANEL_PADDING; // Account for header
        return Math.max(0, totalHeight - availableHeight);
    }
    
    public void setModule(Module module) {
        if (this.currentModule != module) {
            this.currentModule = module;
            refreshSettings();
            
            // Reset scroll
            scrollOffset = 0;
            targetScrollOffset = 0;
            
            // Trigger fade animation
            moduleChangeAnimation.update(module != null, 0);
        }
    }
    
    public void clearSettings() {
        this.currentModule = null;
        this.settingComponents.clear();
        scrollOffset = 0;
        targetScrollOffset = 0;
    }
    
    private void refreshSettings() {
        settingComponents.clear();
        
        if (currentModule == null) return;
        
        List<Setting<?>> settings = currentModule.getSettings();
        for (Setting<?> setting : settings) {
            BaseSettingComponent<?> component = createSettingComponent(setting);
            if (component != null) {
                settingComponents.add(component);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private BaseSettingComponent<?> createSettingComponent(Setting<?> setting) {
        if (setting instanceof BooleanSetting) {
            return new BooleanSettingComponent((BooleanSetting) setting);
        } else if (setting instanceof NumberSetting) {
            return new NumberSettingComponent((NumberSetting) setting);
        } else if (setting instanceof ModeSetting) {
            return new ModeSettingComponent((ModeSetting) setting);
        }
        // Add more setting types as needed
        
        return null;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        if (currentModule != null && button == 0) {
            // Check module toggle
            int toggleX = x + width - ThemeManager.ComponentSize.TOGGLE_WIDTH - ThemeManager.Spacing.PANEL_PADDING - ThemeManager.Spacing.MEDIUM;
            int toggleY = y + ThemeManager.Spacing.PANEL_PADDING + (60 - ThemeManager.ComponentSize.TOGGLE_HEIGHT) / 2;
            
            if (RenderUtils.isPointInRect(mouseX, mouseY, toggleX, toggleY, 
                ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT)) {
                currentModule.toggle();
                return true;
            }
            
            // Check setting components
            for (BaseSettingComponent<?> component : settingComponents) {
                if (component.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (BaseSettingComponent<?> component : settingComponents) {
            component.mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (BaseSettingComponent<?> component : settingComponents) {
            if (component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        
        float maxScroll = getMaxScrollOffset();
        if (maxScroll > 0) {
            float scrollAmount = (float) (verticalAmount * 30); // Scroll speed
            targetScrollOffset = Math.max(-maxScroll, Math.min(0, targetScrollOffset + scrollAmount));
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (BaseSettingComponent<?> component : settingComponents) {
            if (component.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (BaseSettingComponent<?> component : settingComponents) {
            if (component.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }
}