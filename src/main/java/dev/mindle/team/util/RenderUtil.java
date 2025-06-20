package dev.mindle.team.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class RenderUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawString(DrawContext context, String text, int x, int y, int color) {
        TextRenderer textRenderer = mc.textRenderer;
        context.drawText(textRenderer, text, x, y, color, true);
    }

    public static void drawCenteredString(DrawContext context, String text, int centerX, int y, int color) {
        TextRenderer textRenderer = mc.textRenderer;
        int x = centerX - textRenderer.getWidth(text) / 2;
        context.drawText(textRenderer, text, x, y, color, true);
    }

    public static void drawRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y2, color);
    }

    public static void drawOutlinedRect(DrawContext context, int x1, int y1, int x2, int y2, int outlineColor, int fillColor) {
        // Fill
        context.fill(x1, y1, x2, y2, fillColor);
        
        // Outline
        context.fill(x1, y1, x2, y1 + 1, outlineColor); // Top
        context.fill(x1, y2 - 1, x2, y2, outlineColor); // Bottom
        context.fill(x1, y1, x1 + 1, y2, outlineColor); // Left
        context.fill(x2 - 1, y1, x2, y2, outlineColor); // Right
    }

    public static boolean isHovered(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public static Vec3d getInterpolatedPlayerPosition(float tickDelta) {
        if (mc.player == null) return Vec3d.ZERO;
        
        return new Vec3d(
            mc.player.prevX + (mc.player.getX() - mc.player.prevX) * tickDelta,
            mc.player.prevY + (mc.player.getY() - mc.player.prevY) * tickDelta,
            mc.player.prevZ + (mc.player.getZ() - mc.player.prevZ) * tickDelta
        );
    }

    public static Vec3d getCameraPosition() {
        Camera camera = mc.gameRenderer.getCamera();
        return camera.getPos();
    }

    public static int getStringWidth(String text) {
        return mc.textRenderer.getWidth(text);
    }

    public static int getFontHeight() {
        return mc.textRenderer.fontHeight;
    }
}