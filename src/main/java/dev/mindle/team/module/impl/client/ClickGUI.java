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
        descriptions = addSetting(new BooleanSetting("ClickGUI", "Descriptions", "Show module descriptions", true));
        tips = addSetting(new BooleanSetting("ClickGUI", "Tips", "Show usage tips", true));
        moduleWidth = addSetting(new NumberSetting("ClickGUI", "Module Width", "Width of module categories", 100.0, 80.0, 200.0, 1.0));
        moduleHeight = addSetting(new NumberSetting("ClickGUI", "Module Height", "Height of module buttons", 15.0, 10.0, 25.0, 1.0));
        catHeight = addSetting(new NumberSetting("ClickGUI", "Category Height", "Maximum height of categories", 200.0, 100.0, 400.0, 1.0));
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