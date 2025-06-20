package dev.mindle.team.module.impl.movement;

import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.keybind.KeybindType;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class Speed extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private NumberSetting speedMultiplier;

    public Speed() {
        super("Speed", "Increases movement speed", ModuleCategory.MOVEMENT, GLFW.GLFW_KEY_V, KeybindType.TOGGLE);
    }

    @Override
    protected void initializeSettings() {
        speedMultiplier = (NumberSetting) addSetting(new NumberSetting(getName(), "Speed", "Speed multiplier", 2.0, 1.0, 10.0, 0.1));
    }

    @Override
    protected void onEnable() {
        // Speed enabled
    }

    @Override
    protected void onDisable() {
        // Reset speed to normal
        if (mc.player != null) {
            mc.player.getAbilities().setWalkSpeed(0.1f);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        if (mc.player == null) return;
        
        // Apply speed multiplier
        float baseSpeed = 0.1f;
        float newSpeed = baseSpeed * speedMultiplier.getFloatValue();
        mc.player.getAbilities().setWalkSpeed(newSpeed);
    }
}