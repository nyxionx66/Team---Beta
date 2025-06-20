package dev.mindle.team.gui.clickgui;

import dev.mindle.team.Team;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.ModuleManager;
import dev.mindle.team.module.impl.client.ClickGUI;
import dev.mindle.team.gui.clickgui.components.CategoryWindow;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    public static List<CategoryWindow> windows;
    public static boolean anyHovered;
    public static String currentDescription = "";

    private boolean firstOpen;

    public ClickGuiScreen() {
        super(Text.of("ClickGUI"));
        windows = new ArrayList<>();
        firstOpen = true;
    }

    @Override
    protected void init() {
        if (firstOpen) {
            float offset = 0;
            int windowHeight = 18;

            int halfWidth = width / 2;
            int halfWidthCats = (int) ((((float) ModuleCategory.values().length - 1) / 2f) * (ClickGUI.moduleWidth.getValue().floatValue() + 4f));

            for (ModuleCategory category : ModuleCategory.values()) {
                List<Module> modules = Team.getInstance().getModuleManager().getModulesByCategory(category);
                if (!modules.isEmpty()) {
                    CategoryWindow window = new CategoryWindow(category, modules, 
                        (halfWidth - halfWidthCats) + offset, 20, ClickGUI.moduleWidth.getValue().floatValue(), windowHeight);
                    window.setOpen(true);
                    windows.add(window);
                    offset += ClickGUI.moduleWidth.getValue().floatValue() + 2;
                    if (offset > width) {
                        offset = 0;
                    }
                }
            }
            firstOpen = false;
        } else {
            if (!windows.isEmpty() && (windows.get(0).getX() < 0 || windows.get(0).getY() < 0)) {
                float offset = 0;
                int halfWidth = width / 2;
                int halfWidthCats = (int) (3 * (ClickGUI.moduleWidth.getValue().floatValue() + 4f));

                for (CategoryWindow w : windows) {
                    w.setX((halfWidth - halfWidthCats) + offset);
                    w.setY(20);
                    offset += ClickGUI.moduleWidth.getValue().floatValue() + 2;
                    if (offset > width) {
                        offset = 0;
                    }
                }
            }
        }
        windows.forEach(CategoryWindow::init);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void tick() {
        windows.forEach(CategoryWindow::tick);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        anyHovered = false;
        
        // Dark background
        context.fill(0, 0, width, height, 0x80000000);

        windows.forEach(w -> w.render(context, mouseX, mouseY, delta));

        // Show description tooltip
        if (!currentDescription.isEmpty() && ClickGUI.descriptions.getValue()) {
            int descWidth = textRenderer.getWidth(currentDescription) + 6;
            context.fill(mouseX + 7, mouseY + 5, mouseX + 7 + descWidth, mouseY + 16, 0xFF2D2D2D);
            context.drawText(textRenderer, currentDescription, mouseX + 10, mouseY + 8, 0xFFFFFFFF, true);
            currentDescription = "";
        }

        // Show tips
        if (ClickGUI.tips.getValue()) {
            String tipText = "Left Click: Toggle Module | Right Click: Settings | Middle Click: Bind";
            context.drawText(textRenderer, tipText, 5, height - 15, 0xFFAAAAAA, true);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        windows.forEach(w -> {
            w.mouseClicked((int) mouseX, (int) mouseY, button);
            windows.forEach(w1 -> {
                if (w.isDragging() && w != w1) w1.setDragging(false);
            });
        });
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        windows.forEach(w -> w.mouseReleased((int) mouseX, (int) mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        windows.forEach(w -> w.keyPressed(keyCode));

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char key, int modifier) {
        windows.forEach(w -> w.charTyped(key, modifier));
        return super.charTyped(key, modifier);
    }
}