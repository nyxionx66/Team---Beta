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

        // Clean black background
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), 0xFF000000);

        // Setting name
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 4), (int) (y + 2), 0xFFFFFFFF, false);

        // Clean minimal toggle switch
        int toggleX = (int) (x + width - 16);
        int toggleY = (int) (y + 2);
        int toggleWidth = 12;
        int toggleHeight = 10;
        
        // Toggle background and border
        int bgColor = setting.getValue() ? 0xFFFFFFFF : 0xFF000000;
        int borderColor = 0xFFFFFFFF;
        
        // Background
        context.fill(toggleX, toggleY, toggleX + toggleWidth, toggleY + toggleHeight, bgColor);
        
        // Clean border
        context.fill(toggleX - 1, toggleY - 1, toggleX + toggleWidth + 1, toggleY, borderColor); // Top
        context.fill(toggleX - 1, toggleY + toggleHeight, toggleX + toggleWidth + 1, toggleY + toggleHeight + 1, borderColor); // Bottom
        context.fill(toggleX - 1, toggleY - 1, toggleX, toggleY + toggleHeight + 1, borderColor); // Left
        context.fill(toggleX + toggleWidth, toggleY - 1, toggleX + toggleWidth + 1, toggleY + toggleHeight + 1, borderColor); // Right
        
        // Simple indicator
        if (setting.getValue()) {
            // Black checkmark on white background
            context.drawText(mc.textRenderer, "✓", toggleX + 2, toggleY + 1, 0xFF000000, false);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.setValue(!setting.getValue());
        }
    }
}