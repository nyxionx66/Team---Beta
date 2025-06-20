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

        // Add comprehensive null checks
        try {
            CommandManager commandManager = Team.getInstance().getCommandManager();
            int commandCount = (commandManager != null && commandManager.getCommands() != null) ?
                    commandManager.getCommands().size() : 0;
            ChatUtil.sendMessage("§7Commands: §f" + commandCount);
        } catch (Exception e) {
            ChatUtil.sendMessage("§7Commands: §cError accessing");
            Team.LOGGER.debug("Error accessing command manager in info", e);
        }

        try {
            EventBus eventBus = Team.getInstance().getEventBus();
            int listenerCount = (eventBus != null) ? eventBus.getListenerCount() : 0;
            ChatUtil.sendMessage("§7Event Listeners: §f" + listenerCount);
        } catch (Exception e) {
            ChatUtil.sendMessage("§7Event Listeners: §cError accessing");
            Team.LOGGER.debug("Error accessing event bus in info", e);
        }

        try {
            TeamConfig config = Team.getInstance().getConfig();
            int configKeys = (config != null && config.getKeys() != null) ? config.getKeys().size() : 0;
            ChatUtil.sendMessage("§7Config Keys: §f" + configKeys);
        } catch (Exception e) {
            ChatUtil.sendMessage("§7Config Keys: §cError accessing");
            Team.LOGGER.debug("Error accessing config in info", e);
        }

        ChatUtil.sendMessage("§8§l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
    }
}