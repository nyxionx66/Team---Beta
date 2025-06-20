package dev.mindle.team.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mindle.team.Team;
import dev.mindle.team.event.events.UpdateEvent;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onPreTick(CallbackInfo ci) {
        // Add comprehensive null checks to prevent crashes during initialization
        try {
            if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
                Team.getInstance().getEventBus().post(new UpdateEvent.Pre());
            }
        } catch (Exception e) {
            // Log error but don't crash the game
            Team.LOGGER.error("Error in MinecraftClientMixin.onPreTick", e);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(CallbackInfo ci) {
        // Add comprehensive null checks to prevent crashes during initialization
        try {
            if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
                Team.getInstance().getEventBus().post(new UpdateEvent.Post());
            }
        } catch (Exception e) {
            // Log error but don't crash the game
            Team.LOGGER.error("Error in MinecraftClientMixin.onPostTick", e);
        }
    }
}