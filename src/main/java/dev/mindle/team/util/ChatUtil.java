package dev.mindle.team.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), false);
        }
    }

    public static void sendActionBar(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(message), true);
        }
    }

    public static void sendChatMessage(String message) {
        if (mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendChatMessage(message);
        }
    }

    public static void sendCommand(String command) {
        if (mc.player != null && mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendChatCommand(command);
        }
    }
}