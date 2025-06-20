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

        // Setting background
        int bgColor = hovered ? 0xFF2A2A2A : 0xFF1A1A1A;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Setting name
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 3), (int) (y + 2), 0xFFCCCCCC, false);

        // Toggle button
        int toggleX = (int) (x + width - 15);
        int toggleY = (int) (y + 2);
        int toggleColor = setting.getValue() ? 0xFF4CAF50 : 0xFF444444;
        
        context.fill(toggleX, toggleY, toggleX + 10, toggleY + 9, toggleColor);
        
        // Draw border manually
        context.fill(toggleX, toggleY, toggleX + 10, toggleY + 1, 0xFF666666); // Top
        context.fill(toggleX, toggleY + 8, toggleX + 10, toggleY + 9, 0xFF666666); // Bottom
        context.fill(toggleX, toggleY, toggleX + 1, toggleY + 9, 0xFF666666); // Left
        context.fill(toggleX + 9, toggleY, toggleX + 10, toggleY + 9, 0xFF666666); // Right
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.setValue(!setting.getValue());
        }
    }
}