package dev.mindle.team.mixin.input;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mindle.team.Team;
import dev.mindle.team.event.events.MouseEvent;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
        // Add null check to prevent crashes during initialization
        if (Team.getInstance() != null && Team.getInstance().getEventBus() != null) {
            Mouse mouse = (Mouse) (Object) this;
            MouseEvent event = new MouseEvent(button, action, modifiers, mouse.getX(), mouse.getY());
            Team.getInstance().getEventBus().post(event);

            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }
}