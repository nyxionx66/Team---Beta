package dev.mindle.team.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtil {
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void sendMessage(String message) {
        try {
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal(message), false);
            } else {
                // Fallback to logger if player is not available
                System.out.println("[Team] " + message.replaceAll("§[0-9a-fk-or]", ""));
            }
        } catch (Exception e) {
            // Fallback to system output if all else fails
            System.out.println("[Team] " + message.replaceAll("§[0-9a-fk-or]", ""));
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