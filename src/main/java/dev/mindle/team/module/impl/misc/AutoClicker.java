package dev.mindle.team.module.impl.misc;

import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.ModeSetting;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class AutoClicker extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private NumberSetting cps;
    private BooleanSetting leftClick;
    private BooleanSetting rightClick;
    private ModeSetting mode;
    
    private long lastLeftClick = 0;
    private long lastRightClick = 0;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks", ModuleCategory.MISC, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    protected void initializeSettings() {
        cps = addSetting(new NumberSetting(getName(), "CPS", "Clicks per second", 10.0, 1.0, 20.0, 0.5));
        leftClick = addSetting(new BooleanSetting(getName(), "LeftClick", "Auto left click", true));
        rightClick = addSetting(new BooleanSetting(getName(), "RightClick", "Auto right click", false));
        mode = addSetting(new ModeSetting(getName(), "Mode", "Click mode", "Hold", "Hold", "Toggle"));
    }

    @Override
    protected void onEnable() {
        // AutoClicker enabled
    }

    @Override
    protected void onDisable() {
        // AutoClicker disabled
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        if (mc.player == null) return;
        
        long currentTime = System.currentTimeMillis();
        long delay = (long) (1000.0 / cps.getValue());
        
        boolean shouldClick = false;
        
        if (mode.is("Hold")) {
            // Only click while mouse is held down
            shouldClick = true; // This would need proper mouse state checking
        } else {
            // Toggle mode - always click when enabled
            shouldClick = true;
        }
        
        if (!shouldClick) return;
        
        // Left click
        if (leftClick.getValue() && currentTime - lastLeftClick >= delay) {
            if (mc.options.attackKey.isPressed()) {
                mc.options.attackKey.setPressed(true);
                lastLeftClick = currentTime;
            }
        }
        
        // Right click
        if (rightClick.getValue() && currentTime - lastRightClick >= delay) {
            if (mc.options.useKey.isPressed()) {
                mc.options.useKey.setPressed(true);
                lastRightClick = currentTime;
            }
        }
    }
}