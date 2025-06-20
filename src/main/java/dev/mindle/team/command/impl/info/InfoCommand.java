package dev.mindle.team.command.impl.info;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.util.ChatUtil;
import net.minecraft.client.MinecraftClient;

public class InfoCommand extends Command {
    public InfoCommand() {
        super("info", "Shows mod information", CommandManager.PREFIX + "info", "about", "version");
    }

    @Override
    public void execute(String[] args) {
        ChatUtil.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        ChatUtil.sendMessage("§b§l" + Team.MOD_NAME + " §7v" + Team.VERSION);
        ChatUtil.sendMessage("§7Package: §f" + Team.class.getPackage().getName());
        ChatUtil.sendMessage("");
        ChatUtil.sendMessage("§9System Information:");
        ChatUtil.sendMessage("§7Minecraft: §f" + MinecraftClient.getInstance().getGameVersion());
        ChatUtil.sendMessage("§7Fabric Loader: §f" + net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString());
        ChatUtil.sendMessage("§7Java: §f" + System.getProperty("java.version"));
        ChatUtil.sendMessage("");
        ChatUtil.sendMessage("§aMod Statistics:");

        // Add null checks
        int commandCount = Team.getInstance().getCommandManager() != null ?
                Team.getInstance().getCommandManager().getCommands().size() : 0;
        ChatUtil.sendMessage("§7Commands: §f" + commandCount);

        int listenerCount = Team.getInstance().getEventBus() != null ?
                Team.getInstance().getEventBus().getListenerCount() : 0;
        ChatUtil.sendMessage("§7Event Listeners: §f" + listenerCount);

        int configKeys = Team.getInstance().getConfig() != null ?
                Team.getInstance().getConfig().getKeys().size() : 0;
        ChatUtil.sendMessage("§7Config Keys: §f" + configKeys);

        ChatUtil.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}