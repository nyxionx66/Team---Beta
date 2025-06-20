package dev.mindle.team.gui.components;

import dev.mindle.team.gui.ClickGUIScreen;
import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public abstract class BasePanel {
    protected final ClickGUIScreen parent;
    protected final ThemeManager theme = ThemeManager.getInstance();
    
    protected int x, y, width, height;
    protected boolean hovered = false;
    protected AnimationUtils.FadeAnimation fadeAnimation = new AnimationUtils.FadeAnimation();
    
    public BasePanel(ClickGUIScreen parent, int x, int y, int width, int height) {
        this.parent = parent;
        updateDimensions(x, y, width, height);
        fadeAnimation.setImmediate(true);
    }
    
    public void updateDimensions(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public abstract void init();
    
    public void render(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        fadeAnimation.update(true, delta);
        float panelAlpha = fadeAnimation.getAlpha() * alpha;
        
        if (panelAlpha < 0.01f) return;
        
        // Update hover state
        boolean wasHovered = hovered;
        hovered = isMouseOver(mouseX, mouseY);
        
        // Render panel background
        renderBackground(context, panelAlpha);
        
        // Render panel content
        renderContent(context, mouseX, mouseY, delta, panelAlpha);
        
        // Render panel border if needed
        renderBorder(context, panelAlpha);
    }
    
    protected void renderBackground(DrawContext context, float alpha) {
        int backgroundColor = theme.applyAlpha(theme.getSurface(), alpha);
        RenderUtils.drawRoundedRect(context, x, y, width, height, ThemeManager.BorderRadius.MEDIUM, backgroundColor);
    }
    
    protected void renderBorder(DrawContext context, float alpha) {
        int borderColor = theme.applyAlpha(theme.getBorder(), alpha * 0.5f);
        RenderUtils.drawRoundedBorder(context, x, y, width, height, ThemeManager.BorderRadius.MEDIUM, 1, borderColor);
    }
    
    protected abstract void renderContent(DrawContext context, int mouseX, int mouseY, float delta, float alpha);
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }
    
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }
    
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
    
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }
    
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }
    
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }
    
    protected boolean isMouseOver(double mouseX, double mouseY) {
        return RenderUtils.isPointInRect(mouseX, mouseY, x, y, width, height);
    }
    
    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isHovered() { return hovered; }
}