package dev.mindle.team.module.impl.render;

import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Fullbright extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private NumberSetting brightness;
    private BooleanSetting smooth;
    
    private double originalGamma;

    public Fullbright() {
        super("Fullbright", "Makes everything bright", ModuleCategory.RENDER, GLFW.GLFW_KEY_B);
    }

    @Override
    protected void initializeSettings() {
        brightness = (NumberSetting) addSetting(new NumberSetting(getName(), "Brightness", "Brightness level", 15.0, 1.0, 15.0, 0.5));
        smooth = (BooleanSetting) addSetting(new BooleanSetting(getName(), "Smooth", "Smooth brightness transitions", true));
    }

    @Override
    protected void onEnable() {
        if (mc.options != null) {
            originalGamma = mc.options.getGamma().getValue();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.options != null) {
            mc.options.getGamma().setValue(originalGamma);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        if (mc.options == null) return;
        
        double targetGamma = brightness.getValue();
        
        if (smooth.getValue()) {
            // Smooth transition
            double currentGamma = mc.options.getGamma().getValue();
            double diff = targetGamma - currentGamma;
            if (Math.abs(diff) > 0.1) {
                mc.options.getGamma().setValue(currentGamma + diff * 0.1);
            } else {
                mc.options.getGamma().setValue(targetGamma);
            }
        } else {
            // Instant change
            mc.options.getGamma().setValue(targetGamma);
        }
    }
}