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

        // Setting background with better visual feedback
        int bgColor = hovered ? 0xFF2F2F2F : 0xFF1F1F1F;
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), bgColor);

        // Setting name with more bottom margin
        MinecraftClient mc = MinecraftClient.getInstance();
        context.drawText(mc.textRenderer, setting.getName(), 
            (int) (x + 4), (int) (y + 3), 0xFFDDDDDD, false);

        // Slider with better positioning and spacing
        int sliderX = (int) (x + 4);
        int sliderY = (int) (y + height - 6); // Increased margin from bottom
        int sliderWidth = (int) (width - 8);
        
        // Slider track background
        context.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 3, 0xFF404040);
        
        // Slider filled portion
        double percentage = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int filledWidth = (int) (percentage * sliderWidth);
        context.fill(sliderX, sliderY, sliderX + filledWidth, sliderY + 3, 0xFF4CAF50);
        
        // Slider handle with better visibility
        int handleX = (int) (sliderX + percentage * sliderWidth);
        context.fill(handleX - 2, sliderY - 2, handleX + 2, sliderY + 5, 0xFFFFFFFF);
        
        // Value text with better positioning
        String valueText = String.format("%.1f", setting.getValue());
        int textWidth = mc.textRenderer.getWidth(valueText);
        context.drawText(mc.textRenderer, valueText, 
            (int) (x + width - textWidth - 4), (int) (y + 3), 0xFFFFFFFF, false);
            
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