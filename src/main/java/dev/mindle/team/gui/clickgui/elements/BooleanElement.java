package dev.mindle.team.gui.clickgui.elements;

import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class BooleanElement extends AbstractElement {
    private final BooleanSetting setting;

    public BooleanElement(Setting<?> setting) {
        super(setting);
        this.setting = (BooleanSetting) setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Setting background with better visual feedback
        int bgColor = hovered ? 0xFF2F2F2F : 0xFF1F1F1F;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Setting name with better positioning
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 4), (int) (y + 3), 0xFFDDDDDD, false);

        // Toggle button with improved design
        int toggleX = (int) (x + width - 18);
        int toggleY = (int) (y + 2);
        int toggleWidth = 14;
        int toggleHeight = 9;
        
        // Background
        int toggleBgColor = setting.getValue() ? 0xFF4CAF50 : 0xFF555555;
        context.fill(toggleX, toggleY, toggleX + toggleWidth, toggleY + toggleHeight, toggleBgColor);
        
        // Border
        int borderColor = hovered ? 0xFF888888 : 0xFF666666;
        context.fill(toggleX - 1, toggleY - 1, toggleX + toggleWidth + 1, toggleY, borderColor); // Top
        context.fill(toggleX - 1, toggleY + toggleHeight, toggleX + toggleWidth + 1, toggleY + toggleHeight + 1, borderColor); // Bottom
        context.fill(toggleX - 1, toggleY - 1, toggleX, toggleY + toggleHeight + 1, borderColor); // Left
        context.fill(toggleX + toggleWidth, toggleY - 1, toggleX + toggleWidth + 1, toggleY + toggleHeight + 1, borderColor); // Right
        
        // Toggle indicator dot
        if (setting.getValue()) {
            context.fill(toggleX + 9, toggleY + 2, toggleX + 12, toggleY + 7, 0xFFFFFFFF);
        } else {
            context.fill(toggleX + 2, toggleY + 2, toggleX + 5, toggleY + 7, 0xFFAAAAAA);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.setValue(!setting.getValue());
        }
    }
}