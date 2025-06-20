package dev.mindle.team.gui.components.settings;

import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;

public abstract class BaseSettingComponent<T extends Setting<?>> {
    protected final T setting;
    protected final ThemeManager theme = ThemeManager.getInstance();
    
    protected int x, y, width, height;
    protected boolean hovered = false;
    protected final AnimationUtils.HoverAnimation hoverAnimation = new AnimationUtils.HoverAnimation();
    
    public BaseSettingComponent(T setting) {
        this.setting = setting;
    }
    
    public void updateBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public void render(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        // Update hover state and animation
        boolean wasHovered = hovered;
        hovered = RenderUtils.isPointInRect(mouseX, mouseY, x, y, width, height);
        hoverAnimation.update(hovered, delta);
        
        // Render background
        renderBackground(context, alpha);
        
        // Render setting label
        renderLabel(context, alpha);
        
        // Render setting control
        renderControl(context, mouseX, mouseY, delta, alpha);
        
        // Render border if hovered
        if (hovered || hoverAnimation.getProgress() > 0.01f) {
            float borderAlpha = hoverAnimation.getEasedProgress() * alpha;
            int borderColor = theme.applyAlpha(theme.getBorder(), borderAlpha);
            RenderUtils.drawRoundedBorder(context, x, y, width, height, 
                ThemeManager.BorderRadius.SMALL, 1, borderColor);
        }
    }
    
    protected void renderBackground(DrawContext context, float alpha) {
        float hoverProgress = hoverAnimation.getEasedProgress();
        int backgroundColor = theme.applyAlpha(
            theme.interpolate(theme.getSurface(), theme.getHover(), hoverProgress * 0.5f), 
            alpha
        );
        
        RenderUtils.drawRoundedRect(context, x, y, width, height, 
            ThemeManager.BorderRadius.SMALL, backgroundColor);
    }
    
    protected void renderLabel(DrawContext context, float alpha) {
        String name = setting.getName();
        String description = setting.getDescription();
        
        int labelX = x + ThemeManager.Spacing.MEDIUM;
        int labelY = y + ThemeManager.Spacing.SMALL;
        
        // Setting name
        context.drawText(RenderUtils.getTextRenderer(), name, labelX, labelY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
        
        // Setting description
        if (!description.isEmpty()) {
            String truncatedDesc = RenderUtils.truncateText(RenderUtils.getTextRenderer(), description, 
                width - getControlWidth() - 3 * ThemeManager.Spacing.MEDIUM);
            int descY = labelY + RenderUtils.getTextRenderer().fontHeight + 2;
            context.drawText(RenderUtils.getTextRenderer(), truncatedDesc, labelX, descY, 
                theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        }
    }
    
    protected abstract void renderControl(DrawContext context, int mouseX, int mouseY, float delta, float alpha);
    
    protected abstract int getControlWidth();
    
    public abstract int getHeight();
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return RenderUtils.isPointInRect(mouseX, mouseY, x, y, width, height);
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
    
    protected boolean isMouseOverControl(double mouseX, double mouseY) {
        int controlX = x + width - getControlWidth() - ThemeManager.Spacing.MEDIUM;
        int controlY = y + (height - getControlHeight()) / 2;
        return RenderUtils.isPointInRect(mouseX, mouseY, controlX, controlY, getControlWidth(), getControlHeight());
    }
    
    protected int getControlHeight() {
        return ThemeManager.ComponentSize.BUTTON_HEIGHT;
    }
    
    public T getSetting() {
        return setting;
    }
}