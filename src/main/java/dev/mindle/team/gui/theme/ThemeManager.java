package dev.mindle.team.gui.theme;

import dev.mindle.team.Team;

import java.awt.Color;

public class ThemeManager {
    private static ThemeManager instance;
    
    // Primary Dark Theme Colors (from clickgui.md specifications)
    private static final int BACKGROUND = 0xFF1a1a1a;
    private static final int SURFACE = 0xFF2d2d2d;
    private static final int SURFACE_VARIANT = 0xFF3a3a3a;
    private static final int PRIMARY = 0xFF6366f1;
    private static final int PRIMARY_VARIANT = 0xFF8b5cf6;
    private static final int SUCCESS = 0xFF10b981;
    private static final int WARNING = 0xFFf59e0b;
    private static final int ERROR = 0xFFef4444;
    private static final int TEXT_PRIMARY = 0xFFffffff;
    private static final int TEXT_SECONDARY = 0xFFa1a1aa;
    private static final int TEXT_DISABLED = 0xFF71717a;
    private static final int BORDER = 0xFF404040;
    
    // Additional UI colors
    private static final int HOVER = 0xFF4a4a4a;
    private static final int ACTIVE = 0xFF5a5a5a;
    private static final int SHADOW = 0x40000000;
    
    // Animation constants
    public static final int ANIMATION_DURATION = 300;
    public static final float HOVER_SCALE = 1.02f;
    
    private ThemeManager() {}
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    // Color getters
    public int getBackground() { return BACKGROUND; }
    public int getSurface() { return SURFACE; }
    public int getSurfaceVariant() { return SURFACE_VARIANT; }
    public int getPrimary() { return PRIMARY; }
    public int getPrimaryVariant() { return PRIMARY_VARIANT; }
    public int getSuccess() { return SUCCESS; }
    public int getWarning() { return WARNING; }
    public int getError() { return ERROR; }
    public int getTextPrimary() { return TEXT_PRIMARY; }
    public int getTextSecondary() { return TEXT_SECONDARY; }
    public int getTextDisabled() { return TEXT_DISABLED; }
    public int getBorder() { return BORDER; }
    public int getHover() { return HOVER; }
    public int getActive() { return ACTIVE; }
    public int getShadow() { return SHADOW; }
    
    // Utility methods
    public int applyAlpha(int color, float alpha) {
        int a = (int) (255 * Math.max(0, Math.min(1, alpha)));
        return (color & 0x00FFFFFF) | (a << 24);
    }
    
    public int lighten(int color, float amount) {
        Color c = new Color(color, true);
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float newBrightness = Math.min(1.0f, hsb[2] + amount);
        int rgb = Color.HSBtoRGB(hsb[0], hsb[1], newBrightness);
        return (color & 0xFF000000) | (rgb & 0x00FFFFFF);
    }
    
    public int darken(int color, float amount) {
        return lighten(color, -amount);
    }
    
    public int interpolate(int color1, int color2, float factor) {
        factor = Math.max(0, Math.min(1, factor));
        
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        
        int a = (int) (a1 + (a2 - a1) * factor);
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    // State-based color helpers
    public int getButtonColor(boolean enabled, boolean hovered, boolean pressed) {
        if (!enabled) return getTextDisabled();
        if (pressed) return getActive();
        if (hovered) return getHover();
        return getSurface();
    }
    
    public int getToggleColor(boolean enabled, boolean hovered) {
        if (enabled) {
            return hovered ? lighten(getSuccess(), 0.1f) : getSuccess();
        }
        return hovered ? getHover() : getSurface();
    }
    
    public int getModuleColor(boolean enabled) {
        return enabled ? getSuccess() : getTextSecondary();
    }
    
    // Typography
    public static class Typography {
        public static final int TITLE_SIZE = 24;
        public static final int HEADER_SIZE = 20;
        public static final int BODY_SIZE = 16;
        public static final int CAPTION_SIZE = 14;
        public static final int SMALL_SIZE = 12;
    }
    
    // Spacing (8px base unit as per specifications)
    public static class Spacing {
        public static final int UNIT = 8;
        public static final int SMALL = UNIT;
        public static final int MEDIUM = UNIT * 2;
        public static final int LARGE = UNIT * 3;
        public static final int PANEL_PADDING = UNIT * 2;
        public static final int COMPONENT_MARGIN = UNIT;
    }
    
    // Border radius
    public static class BorderRadius {
        public static final int SMALL = 6;
        public static final int MEDIUM = 8;
        public static final int LARGE = 12;
    }
    
    // Component dimensions
    public static class ComponentSize {
        public static final int BUTTON_HEIGHT = 32;
        public static final int INPUT_HEIGHT = 36;
        public static final int TOGGLE_WIDTH = 48;
        public static final int TOGGLE_HEIGHT = 24;
        public static final int SLIDER_HEIGHT = 20;
        public static final int MODULE_CARD_HEIGHT = 80;
        public static final int SETTING_ROW_HEIGHT = 48;
    }
}