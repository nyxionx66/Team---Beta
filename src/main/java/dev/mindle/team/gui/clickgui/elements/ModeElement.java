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

        // Clean black background
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), 0xFF000000);

        // Setting name
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 4), (int) (y + 2), 0xFFFFFFFF, false);

        // Current mode with clean styling
        String currentMode = setting.getValue();
        int textWidth = mc.textRenderer.getWidth(currentMode);
        
        // Simple bracket notation for mode value
        String displayText = "[" + currentMode + "]";
        int displayWidth = mc.textRenderer.getWidth(displayText);
        
        context.drawText(mc.textRenderer, displayText, 
            (int) (x + width - displayWidth - 4), (int) (y + 2), 0xFFCCCCCC, false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            setting.cycle();
        }
    }
}