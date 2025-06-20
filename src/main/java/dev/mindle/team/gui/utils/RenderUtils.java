package dev.mindle.team.gui.utils;

import dev.mindle.team.gui.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.MathHelper;

public class RenderUtils {
    private static final ThemeManager theme = ThemeManager.getInstance();
    
    // Rectangle rendering
    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        if (radius <= 0) {
            context.fill(x, y, x + width, y + height, color);
            return;
        }
        
        // Draw main rectangle
        context.fill(x + radius, y, x + width - radius, y + height, color);
        context.fill(x, y + radius, x + width, y + height - radius, color);
        
        // Draw corners (simplified rounded corners)
        context.fill(x, y, x + radius, y + radius, color);
        context.fill(x + width - radius, y, x + width, y + radius, color);
        context.fill(x, y + height - radius, x + radius, y + height, color);
        context.fill(x + width - radius, y + height - radius, x + width, y + height, color);
    }
    
    public static void drawBorder(DrawContext context, int x, int y, int width, int height, int thickness, int color) {
        // Top
        context.fill(x, y, x + width, y + thickness, color);
        // Bottom
        context.fill(x, y + height - thickness, x + width, y + height, color);
        // Left
        context.fill(x, y, x + thickness, y + height, color);
        // Right
        context.fill(x + width - thickness, y, x + width, y + height, color);
    }
    
    public static void drawRoundedBorder(DrawContext context, int x, int y, int width, int height, int radius, int thickness, int color) {
        drawRoundedRect(context, x, y, width, height, radius, color);
        drawRoundedRect(context, x + thickness, y + thickness, width - 2 * thickness, height - 2 * thickness, radius - thickness, theme.getSurface());
    }
    
    // Gradient rendering
    public static void renderGradientOverlay(DrawContext context, int x, int y, int width, int height, int startColor, int endColor) {
        // Simple vertical gradient simulation
        int steps = Math.min(height, 32);
        int stepHeight = height / steps;
        
        for (int i = 0; i < steps; i++) {
            float factor = (float) i / (steps - 1);
            int color = theme.interpolate(startColor, endColor, factor);
            int yPos = y + i * stepHeight;
            int nextYPos = (i == steps - 1) ? y + height : y + (i + 1) * stepHeight;
            context.fill(x, yPos, x + width, nextYPos, color);
        }
    }
    
    // Shadow rendering
    public static void drawShadow(DrawContext context, int x, int y, int width, int height, int blur, int color) {
        for (int i = 0; i < blur; i++) {
            float alpha = (float) (blur - i) / blur * 0.1f;
            int shadowColor = theme.applyAlpha(color, alpha);
            context.fill(x - i, y - i, x + width + i, y + height + i, shadowColor);
        }
    }
    
    // Text rendering helpers
    public static void drawCenteredText(DrawContext context, String text, int x, int y, int width, int color) {
        int textWidth = context.getTextRenderer().getWidth(text);
        int centeredX = x + (width - textWidth) / 2;
        context.drawText(context.getTextRenderer(), text, centeredX, y, color, false);
    }
    
    public static void drawRightAlignedText(DrawContext context, String text, int x, int y, int width, int color) {
        int textWidth = context.getTextRenderer().getWidth(text);
        int rightAlignedX = x + width - textWidth;
        context.drawText(context.getTextRenderer(), text, rightAlignedX, y, color, false);
    }
    
    public static String truncateText(net.minecraft.client.font.TextRenderer textRenderer, String text, int maxWidth) {
        if (textRenderer.getWidth(text) <= maxWidth) {
            return text;
        }
        
        String ellipsis = "...";
        int ellipsisWidth = textRenderer.getWidth(ellipsis);
        
        int availableWidth = maxWidth - ellipsisWidth;
        if (availableWidth <= 0) {
            return ellipsis;
        }
        
        StringBuilder truncated = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String substring = text.substring(0, i + 1);
            if (textRenderer.getWidth(substring) > availableWidth) {
                break;
            }
            truncated = new StringBuilder(substring);
        }
        
        return truncated.toString() + ellipsis;
    }
    
    // Progress bar rendering
    public static void drawProgressBar(DrawContext context, int x, int y, int width, int height, float progress, int backgroundColor, int progressColor) {
        progress = MathHelper.clamp(progress, 0.0f, 1.0f);
        
        // Background
        drawRoundedRect(context, x, y, width, height, ThemeManager.BorderRadius.SMALL, backgroundColor);
        
        // Progress
        int progressWidth = (int) (width * progress);
        if (progressWidth > 0) {
            drawRoundedRect(context, x, y, progressWidth, height, ThemeManager.BorderRadius.SMALL, progressColor);
        }
    }
    
    // Slider rendering
    public static void drawSlider(DrawContext context, int x, int y, int width, int height, float value, boolean hovered, boolean dragging) {
        value = MathHelper.clamp(value, 0.0f, 1.0f);
        
        // Track
        int trackHeight = height / 3;
        int trackY = y + (height - trackHeight) / 2;
        drawRoundedRect(context, x, trackY, width, trackHeight, ThemeManager.BorderRadius.SMALL, theme.getSurface());
        
        // Progress
        int progressWidth = (int) (width * value);
        if (progressWidth > 0) {
            drawRoundedRect(context, x, trackY, progressWidth, trackHeight, ThemeManager.BorderRadius.SMALL, theme.getPrimary());
        }
        
        // Thumb
        int thumbSize = height;
        int thumbX = x + progressWidth - thumbSize / 2;
        int thumbColor = dragging ? theme.getPrimaryVariant() : (hovered ? theme.lighten(theme.getPrimary(), 0.1f) : theme.getPrimary());
        
        drawShadow(context, thumbX, y, thumbSize, thumbSize, 2, theme.getShadow());
        drawRoundedRect(context, thumbX, y, thumbSize, thumbSize, thumbSize / 2, thumbColor);
    }
    
    // Toggle switch rendering
    public static void drawToggle(DrawContext context, int x, int y, int width, int height, boolean enabled, boolean hovered) {
        int backgroundColor = theme.getToggleColor(enabled, hovered);
        drawRoundedRect(context, x, y, width, height, height / 2, backgroundColor);
        
        // Thumb
        int thumbSize = height - 4;
        int thumbX = enabled ? x + width - thumbSize - 2 : x + 2;
        int thumbY = y + 2;
        
        drawShadow(context, thumbX, thumbY, thumbSize, thumbSize, 1, theme.getShadow());
        drawRoundedRect(context, thumbX, thumbY, thumbSize, thumbSize, thumbSize / 2, theme.getTextPrimary());
    }
    
    // Checkbox rendering
    public static void drawCheckbox(DrawContext context, int x, int y, int size, boolean checked, boolean hovered) {
        int backgroundColor = checked ? theme.getPrimary() : theme.getSurface();
        if (hovered && !checked) {
            backgroundColor = theme.getHover();
        }
        
        drawRoundedRect(context, x, y, size, size, ThemeManager.BorderRadius.SMALL, backgroundColor);
        drawRoundedBorder(context, x, y, size, size, ThemeManager.BorderRadius.SMALL, 1, theme.getBorder());
        
        if (checked) {
            // Draw checkmark
            int checkSize = size / 2;
            int checkX = x + (size - checkSize) / 2;
            int checkY = y + (size - checkSize) / 2;
            context.drawText(context.getTextRenderer(), "✓", checkX, checkY, theme.getTextPrimary(), false);
        }
    }
    
    // Utility methods
    public static boolean isPointInRect(double x, double y, int rectX, int rectY, int rectWidth, int rectHeight) {
        return x >= rectX && x < rectX + rectWidth && y >= rectY && y < rectY + rectHeight;
    }
    
    public static int getTextColor(boolean enabled, boolean hovered) {
        if (!enabled) return theme.getTextDisabled();
        if (hovered) return theme.getTextPrimary();
        return theme.getTextSecondary();
    }
}