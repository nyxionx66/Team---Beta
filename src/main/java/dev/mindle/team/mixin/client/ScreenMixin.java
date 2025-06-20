package dev.mindle.team.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mindle.team.screen.ScreenManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

@Mixin(MinecraftClient.class)
public class ScreenMixin {
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        try {
            ScreenManager.setScreenOpen(screen);
        } catch (Exception e) {
            // Don't crash the game if there's an error
        }
    }
}