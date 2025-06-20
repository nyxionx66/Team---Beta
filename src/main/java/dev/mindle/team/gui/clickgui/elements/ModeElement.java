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

        // Setting background with better visual feedback
        int bgColor = hovered ? 0xFF2F2F2F : 0xFF1F1F1F;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Setting name with better positioning
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 4), (int) (y + 3), 0xFFDDDDDD, false);

        // Current mode with better styling
        String currentMode = setting.getValue();
        int textWidth = mc.textRenderer.getWidth(currentMode);
        
        // Background for mode value
        context.fill((int) (x + width - textWidth - 8), (int) (y + 1), 
                    (int) (x + width - 2), (int) (y + height - 1), 0x604CAF50);
        
        context.drawText(mc.textRenderer, currentMode, 
            (int) (x + width - textWidth - 5), (int) (y + 3), 0xFF4CAF50, false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.cycle();
        }
    }
}