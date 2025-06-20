package dev.mindle.team.module.impl.combat;

import dev.mindle.team.event.Subscribe;
import dev.mindle.team.event.events.UpdateEvent;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class KillAura extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();
    
    private BooleanSetting players;
    private BooleanSetting mobs;
    private BooleanSetting animals;
    private NumberSetting range;
    private NumberSetting delay;
    
    private long lastAttack = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities", ModuleCategory.COMBAT, GLFW.GLFW_KEY_R);
    }

    @Override
    protected void initializeSettings() {
        players = (BooleanSetting) addSetting(new BooleanSetting(getName(), "Players", "Target players", true));
        mobs = (BooleanSetting) addSetting(new BooleanSetting(getName(), "Mobs", "Target hostile mobs", true));
        animals = (BooleanSetting) addSetting(new BooleanSetting(getName(), "Animals", "Target animals", false));
        range = (NumberSetting) addSetting(new NumberSetting(getName(), "Range", "Attack range", 4.0, 1.0, 6.0, 0.1));
        delay = (NumberSetting) addSetting(new NumberSetting(getName(), "Delay", "Attack delay in milliseconds", 100.0, 0.0, 1000.0, 10.0));
    }

    @Override
    protected void onEnable() {
        // KillAura enabled
    }

    @Override
    protected void onDisable() {
        // KillAura disabled
    }

    @Subscribe
    public void onUpdate(UpdateEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;
        
        // Check delay
        if (System.currentTimeMillis() - lastAttack < delay.getValue()) {
            return;
        }
        
        // Find targets
        LivingEntity target = findTarget();
        if (target != null) {
            // Attack the target
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(mc.player.getActiveHand());
            lastAttack = System.currentTimeMillis();
        }
    }

    private LivingEntity findTarget() {
        double rangeSq = range.getValue() * range.getValue();
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        
        Box searchBox = mc.player.getBoundingBox().expand(range.getValue());
        List<Entity> entities = mc.world.getOtherEntities(mc.player, searchBox);
        
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity living)) continue;
            if (living.isDead() || living.getHealth() <= 0) continue;
            
            // Check if we should target this entity type
            if (!shouldTarget(living)) continue;
            
            double dist = mc.player.squaredDistanceTo(living);
            if (dist < rangeSq && dist < closestDist) {
                closest = living;
                closestDist = dist;
            }
        }
        
        return closest;
    }

    private boolean shouldTarget(LivingEntity entity) {
        if (entity instanceof PlayerEntity && !players.getValue()) {
            return false;
        }
        
        // Simple classification - this could be expanded
        if (entity instanceof PlayerEntity) {
            return players.getValue();
        } else {
            // For simplicity, treating all non-player living entities as mobs
            return mobs.getValue();
        }
    }
}