package dev.mindle.team.gui.clickgui.elements;

import dev.mindle.team.module.setting.NumberSetting;
import dev.mindle.team.module.setting.Setting;
import dev.mindle.team.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class NumberElement extends AbstractElement {
    private final NumberSetting setting;
    private boolean dragging = false;

    public NumberElement(Setting<?> setting) {
        super(setting);
        this.setting = (NumberSetting) setting;
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

        // Slider
        int sliderX = (int) (x + 3);
        int sliderY = (int) (y + height - 4);
        int sliderWidth = (int) (width - 6);
        
        // Slider background
        context.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, 0xFF333333);
        
        // Slider handle
        double percentage = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int handleX = (int) (sliderX + percentage * sliderWidth);
        context.fill(handleX - 1, sliderY - 1, handleX + 1, sliderY + 3, 0xFF4CAF50);
        
        // Value text
        String valueText = String.format("%.1f", setting.getValue());
        int textWidth = mc.textRenderer.getWidth(valueText);
        context.drawText(mc.textRenderer, valueText, 
            (int) (x + width - textWidth - 3), (int) (y + 2), 0xFFFFFFFF, false);
            
        // Handle dragging
        if (dragging) {
            double newPercentage = Math.max(0, Math.min(1, (mouseX - sliderX) / (double) sliderWidth));
            double newValue = setting.getMin() + newPercentage * (setting.getMax() - setting.getMin());
            
            // Round to increment
            if (setting.getIncrement() > 0) {
                newValue = Math.round(newValue / setting.getIncrement()) * setting.getIncrement();
            }
            
            setting.setValue(newValue);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (hovered && button == 0) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        if (button == 0) {
            dragging = false;
        }
    }
}