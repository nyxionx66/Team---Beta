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
        // Add comprehensive null checks to prevent crashes during initialization
        try {
            if (Team.getInstance() != null && 
                Team.getInstance().getCommandManager() != null && 
                chatText != null && 
                !chatText.isEmpty()) {
                
                // Handle command execution
                if (Team.getInstance().getCommandManager().executeCommand(chatText)) {
                    ci.cancel();
                }
            }
        } catch (Exception e) {
            // Log error but don't crash the game
            Team.LOGGER.error("Error in ChatScreenMixin.onSendMessage", e);
        }
    }
}