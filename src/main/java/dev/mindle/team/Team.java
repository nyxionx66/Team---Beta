package dev.mindle.team;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.mindle.team.event.EventBus;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.config.TeamConfig;
import dev.mindle.team.module.ModuleManager;

public class Team implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "team";
    public static final String MOD_NAME = "Team";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    private static Team instance;
    private CommandManager commandManager;
    private EventBus eventBus;
    private TeamConfig config;
    private ModuleManager moduleManager;

    public Team() {
        instance = this;
        // Early initialization of config to prevent null access
        try {
            this.config = new TeamConfig();
            LOGGER.debug("Early config initialization successful");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize config early", e);
        }
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {} v{}", MOD_NAME, VERSION);
        
        try {
            // Initialize core systems (config already initialized in constructor)
            if (this.config == null) {
                this.config = new TeamConfig();
            }
            this.eventBus = new EventBus();
            
            LOGGER.info("{} initialized successfully!", MOD_NAME);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize {}", MOD_NAME, e);
            throw new RuntimeException("Failed to initialize " + MOD_NAME, e);
        }
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {} client-side features", MOD_NAME);
        
        try {
            // Initialize client-side systems
            this.commandManager = new CommandManager();
            this.commandManager.registerCommands();
            
            // Initialize module system
            this.moduleManager = new ModuleManager();
            
            LOGGER.info("{} client initialization complete!", MOD_NAME);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize {} client features", MOD_NAME, e);
            throw new RuntimeException("Failed to initialize " + MOD_NAME + " client features", e);
        }
    }

    public static Team getInstance() {
        if (instance == null) {
            LOGGER.warn("Team instance requested before initialization!");
        }
        return instance;
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public CommandManager getCommandManager() {
        if (commandManager == null) {
            LOGGER.warn("CommandManager was null, initializing new instance");
            commandManager = new CommandManager();
            commandManager.registerCommands();
        }
        return commandManager;
    }

    public EventBus getEventBus() {
        if (eventBus == null) {
            LOGGER.warn("EventBus was null, initializing new instance");
            eventBus = new EventBus();
        }
        return eventBus;
    }

    public TeamConfig getConfig() {
        if (config == null) {
            LOGGER.warn("Config was null, initializing new config instance");
            config = new TeamConfig();
        }
        return config;
    }

    public void setConfig(TeamConfig config) {
        this.config = config;
    }
}