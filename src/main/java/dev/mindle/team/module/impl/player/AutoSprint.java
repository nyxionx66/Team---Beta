package dev.mindle.team.module.impl.player;

import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.lwjgl.glfw.GLFW;

public class AutoSprint extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private BooleanSetting keepSprint;
    private BooleanSetting multiDirection;

    public AutoSprint() {
        super("AutoSprint", "Automatically sprints", ModuleCategory.PLAYER, GLFW.GLFW_KEY_UNKNOWN);
    }

    @Override
    protected void initializeSettings() {
        keepSprint = (BooleanSetting) addSetting(new BooleanSetting(getName(), "KeepSprint", "Keep sprinting while attacking", true));
        multiDirection = (BooleanSetting) addSetting(new BooleanSetting(getName(), "MultiDirection", "Sprint in all directions", false));
    }

    @Override
    protected void onEnable() {
        // AutoSprint enabled
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.setSprinting(false);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        if (mc.player == null) return;
        
        boolean shouldSprint = false;
        
        if (multiDirection.getValue()) {
            // Sprint if any movement key is pressed
            shouldSprint = mc.player.input.pressingForward || 
                          mc.player.input.pressingBack || 
                          mc.player.input.pressingLeft || 
                          mc.player.input.pressingRight;
        } else {
            // Only sprint when moving forward
            shouldSprint = mc.player.input.pressingForward;
        }
        
        // Check if player can sprint
        shouldSprint = shouldSprint && 
                      !mc.player.isSneaking() && 
                      !mc.player.isSubmergedInWater() &&
                      mc.player.getHungerManager().getFoodLevel() > 6;
        
        if (shouldSprint) {
            mc.player.setSprinting(true);
        }
    }
}