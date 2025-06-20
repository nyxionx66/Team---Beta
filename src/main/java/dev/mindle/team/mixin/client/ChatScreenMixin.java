package dev.mindle.team.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.mindle.team.Team;
import net.minecraft.client.gui.screen.ChatScreen;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfo ci) {
        // Add null check to prevent crashes during initialization
        if (Team.getInstance() != null && Team.getInstance().getCommandManager() != null) {
            // Handle command execution
            if (Team.getInstance().getCommandManager().executeCommand(chatText)) {
                ci.cancel();
            }
        }
    }
}