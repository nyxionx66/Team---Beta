package dev.mindle.team.mixin.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mindle.team.Team;
import dev.mindle.team.event.events.KeyEvent;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        // Add null check to prevent crashes during initialization
        if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
            KeyEvent event = new KeyEvent(key, scanCode, action, modifiers);
            Team.getInstance().getEventBus().post(event);

            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }
}