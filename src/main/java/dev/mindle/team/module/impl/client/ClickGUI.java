package dev.mindle.team.module.impl.client;

import dev.mindle.team.Team;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import dev.mindle.team.gui.clickgui.ClickGuiScreen;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ClickGUI extends Module {
    public static BooleanSetting descriptions;
    public static BooleanSetting tips;
    public static NumberSetting moduleWidth;
    public static NumberSetting moduleHeight;
    public static NumberSetting catHeight;
    
    private static ClickGuiScreen clickGuiScreen;

    public ClickGUI() {
        super("ClickGUI", "Opens the click GUI", ModuleCategory.CLIENT, GLFW.GLFW_KEY_APOSTROPHE);
    }

    @Override
    protected void initializeSettings() {
        descriptions = addSetting(new BooleanSetting("Descriptions", true));
        tips = addSetting(new BooleanSetting("Tips", true));
        moduleWidth = addSetting(new NumberSetting("Module Width", 100, 80, 200, 1));
        moduleHeight = addSetting(new NumberSetting("Module Height", 15, 10, 25, 1));
        catHeight = addSetting(new NumberSetting("Category Height", 200, 100, 400, 1));
    }

    @Override
    public void onEnable() {
        if (MinecraftClient.getInstance().currentScreen == null) {
            if (clickGuiScreen == null) {
                clickGuiScreen = new ClickGuiScreen();
            }
            MinecraftClient.getInstance().setScreen(clickGuiScreen);
        }
        disable();
    }

    @Override
    public void onDisable() {
        // ClickGUI should not stay enabled
    }

    public static ClickGuiScreen getClickGuiScreen() {
        if (clickGuiScreen == null) {
            clickGuiScreen = new ClickGuiScreen();
        }
        return clickGuiScreen;
    }
}