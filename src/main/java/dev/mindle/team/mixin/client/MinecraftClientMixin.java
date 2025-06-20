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
        // Add null check to prevent crashes during initialization
        if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
            Team.getInstance().getEventBus().post(new UpdateEvent.Pre());
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onPostTick(CallbackInfo ci) {
        // Add null check to prevent crashes during initialization
        if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
            Team.getInstance().getEventBus().post(new UpdateEvent.Post());
        }
    }
}