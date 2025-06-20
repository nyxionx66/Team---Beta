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

        // Clean black background
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), 0xFF000000);

        // Setting name with proper spacing
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 4), (int) (y + 2), 0xFFFFFFFF, false);

        // Slider with clean design and better spacing
        int sliderX = (int) (x + 4);
        int sliderY = (int) (y + height - 5); // More margin from bottom
        int sliderWidth = (int) (width - 8);
        int sliderHeight = 2;
        
        // Slider track - white outline
        context.fill(sliderX - 1, sliderY - 1, sliderX + sliderWidth + 1, sliderY + sliderHeight + 1, 0xFFFFFFFF);
        context.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 0xFF000000);
        
        // Slider filled portion - white fill
        double percentage = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int filledWidth = (int) (percentage * sliderWidth);
        context.fill(sliderX, sliderY, sliderX + filledWidth, sliderY + sliderHeight, 0xFFFFFFFF);
        
        // Slider handle - minimal white square
        int handleX = (int) (sliderX + percentage * sliderWidth);
        context.fill(handleX - 1, sliderY - 1, handleX + 1, sliderY + sliderHeight + 1, 0xFFFFFFFF);
        
        // Value text
        String valueText = String.format("%.1f", setting.getValue());
        int textWidth = mc.textRenderer.getWidth(valueText);
        context.drawText(mc.textRenderer, valueText, 
            (int) (x + width - textWidth - 4), (int) (y + 2), 0xFFCCCCCC, false);
            
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