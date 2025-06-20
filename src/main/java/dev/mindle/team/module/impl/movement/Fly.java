package dev.mindle.team.module.impl.movement;

import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.keybind.KeybindType;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class Fly extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private NumberSetting speed;
    private BooleanSetting noKick;

    public Fly() {
        super("Fly", "Allows you to fly", ModuleCategory.MOVEMENT, GLFW.GLFW_KEY_F, KeybindType.TOGGLE);
    }

    @Override
    protected void initializeSettings() {
        speed = (NumberSetting) addSetting(new NumberSetting(getName(), "Speed", "Flight speed", 1.0, 0.1, 10.0, 0.1));
        noKick = (BooleanSetting) addSetting(new BooleanSetting(getName(), "NoKick", "Prevents anti-cheat kicks", true));
    }

    @Override
    protected void onEnable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().allowFlying = true;
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null) {
            mc.player.getAbilities().flying = false;
            mc.player.getAbilities().allowFlying = mc.player.isCreative() || mc.player.isSpectator();
            mc.player.getAbilities().setFlySpeed(0.05f);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        if (mc.player == null) return;
        
        // Set fly speed
        mc.player.getAbilities().setFlySpeed(speed.getFloatValue() * 0.05f);
        
        // Enable flying
        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().allowFlying = true;
        
        // NoKick feature - small downward motion
        if (noKick.getValue() && mc.player.age % 40 == 0) {
            mc.player.setVelocity(mc.player.getVelocity().add(0, -0.04, 0));
        }
    }
}