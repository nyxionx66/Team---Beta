package dev.mindle.team.module.impl.client;

import dev.mindle.team.Team;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.BooleanSetting;
import dev.mindle.team.module.setting.NumberSetting;
import dev.mindle.team.gui.clickgui.ClickGuiScreen;
import net.minecraft.client.MinecraftClient;

public class ClickGUI extends Module {
    public static final BooleanSetting descriptions = new BooleanSetting("Descriptions", true);
    public static final BooleanSetting tips = new BooleanSetting("Tips", true);
    public static final NumberSetting moduleWidth = new NumberSetting("Module Width", 100, 80, 200, 1);
    public static final NumberSetting moduleHeight = new NumberSetting("Module Height", 15, 10, 25, 1);
    public static final NumberSetting catHeight = new NumberSetting("Category Height", 200, 100, 400, 1);
    
    private static ClickGuiScreen clickGuiScreen;

    public ClickGUI() {
        super("ClickGUI", "Opens the click GUI", ModuleCategory.CLIENT);
        addSettings(descriptions, tips, moduleWidth, moduleHeight, catHeight);
        setBind("'");
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