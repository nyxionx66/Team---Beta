package dev.mindle.team.gui.clickgui.elements;

import dev.mindle.team.module.setting.ModeSetting;
import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ModeElement extends AbstractElement {
    private final ModeSetting setting;

    public ModeElement(Setting<?> setting) {
        super(setting);
        this.setting = (ModeSetting) setting;
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

        // Current mode
        String currentMode = setting.getValue();
        int textWidth = mc.textRenderer.getWidth(currentMode);
        context.drawText(mc.textRenderer, currentMode, 
            (int) (x + width - textWidth - 3), (int) (y + 2), 0xFF4CAF50, false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.cycle();
        }
    }
}