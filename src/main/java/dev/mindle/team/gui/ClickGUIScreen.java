package dev.mindle.team.gui;

import dev.mindle.team.Team;
import dev.mindle.team.gui.components.*;
import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import dev.mindle.team.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends Screen {
    private static final String TITLE = "ClickGUI - " + Team.MOD_NAME;
    
    // Layout constants
    private static final int CATEGORY_PANEL_WIDTH = 200;
    private static final int SETTINGS_PANEL_WIDTH = 250;
    private static final int HEADER_HEIGHT = 50;
    private static final int PADDING = 16;
    
    // Panels
    private CategoryPanel categoryPanel;
    private ModulePanel modulePanel;
    private SettingsPanel settingsPanel;
    private HeaderPanel headerPanel;
    
    // State
    private ModuleCategory selectedCategory = ModuleCategory.COMBAT;
    private Module selectedModule = null;
    private String searchQuery = "";
    private boolean isGridView = true;
    private float animationProgress = 0.0f;
    
    // Theme
    private final ThemeManager theme = ThemeManager.getInstance();
    
    public ClickGUIScreen() {
        super(Text.literal(TITLE));
        initializePanels();
    }
    
    private void initializePanels() {
        // Initialize panels with proper dimensions
        categoryPanel = new CategoryPanel(this, 0, HEADER_HEIGHT, CATEGORY_PANEL_WIDTH, 0);
        modulePanel = new ModulePanel(this, CATEGORY_PANEL_WIDTH, HEADER_HEIGHT, 0, 0);
        settingsPanel = new SettingsPanel(this, 0, HEADER_HEIGHT, SETTINGS_PANEL_WIDTH, 0);
        headerPanel = new HeaderPanel(this, 0, 0, 0, HEADER_HEIGHT);
    }
    
    @Override
    public void init() {
        super.init();
        
        // Calculate panel dimensions based on screen size
        int screenWidth = this.width;
        int screenHeight = this.height;
        
        // Update panel dimensions
        categoryPanel.updateDimensions(0, HEADER_HEIGHT, CATEGORY_PANEL_WIDTH, screenHeight - HEADER_HEIGHT);
        
        int modulePanelWidth = screenWidth - CATEGORY_PANEL_WIDTH - SETTINGS_PANEL_WIDTH;
        modulePanel.updateDimensions(CATEGORY_PANEL_WIDTH, HEADER_HEIGHT, modulePanelWidth, screenHeight - HEADER_HEIGHT);
        
        settingsPanel.updateDimensions(screenWidth - SETTINGS_PANEL_WIDTH, HEADER_HEIGHT, SETTINGS_PANEL_WIDTH, screenHeight - HEADER_HEIGHT);
        
        headerPanel.updateDimensions(0, 0, screenWidth, HEADER_HEIGHT);
        
        // Initialize all panels
        categoryPanel.init();
        modulePanel.init();
        settingsPanel.init();
        headerPanel.init();
        
        Team.LOGGER.info("ClickGUI initialized with dimensions {}x{}", width, height);
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Update animation
        animationProgress = AnimationUtils.updateAnimation(animationProgress, 1.0f, delta * 4.0f);
        
        // Apply fade-in animation
        float alpha = AnimationUtils.easeOutCubic(animationProgress);
        
        // Render background
        renderBackground(context, alpha);
        
        // Render panels
        categoryPanel.render(context, mouseX, mouseY, delta, alpha);
        modulePanel.render(context, mouseX, mouseY, delta, alpha);
        settingsPanel.render(context, mouseX, mouseY, delta, alpha);
        headerPanel.render(context, mouseX, mouseY, delta, alpha);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderBackground(DrawContext context, float alpha) {
        // Render main background
        int backgroundColor = theme.applyAlpha(theme.getBackground(), alpha);
        context.fill(0, 0, width, height, backgroundColor);
        
        // Render subtle pattern or gradient if needed
        RenderUtils.renderGradientOverlay(context, 0, 0, width, height, 
            theme.applyAlpha(theme.getBackground(), alpha * 0.1f), 
            theme.applyAlpha(theme.getSurface(), alpha * 0.05f));
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (headerPanel.mouseClicked(mouseX, mouseY, button)) return true;
        if (categoryPanel.mouseClicked(mouseX, mouseY, button)) return true;
        if (modulePanel.mouseClicked(mouseX, mouseY, button)) return true;
        if (settingsPanel.mouseClicked(mouseX, mouseY, button)) return true;
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        headerPanel.mouseReleased(mouseX, mouseY, button);
        categoryPanel.mouseReleased(mouseX, mouseY, button);
        modulePanel.mouseReleased(mouseX, mouseY, button);
        settingsPanel.mouseReleased(mouseX, mouseY, button);
        
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        settingsPanel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (modulePanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        if (settingsPanel.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle search input
        if (headerPanel.keyPressed(keyCode, scanCode, modifiers)) return true;
        
        // Handle settings input
        if (settingsPanel.keyPressed(keyCode, scanCode, modifiers)) return true;
        
        // Handle navigation shortcuts
        if (handleNavigationShortcuts(keyCode, modifiers)) return true;
        
        // Close GUI on ESC
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private boolean handleNavigationShortcuts(int keyCode, int modifiers) {
        // Ctrl+F for search focus
        if (keyCode == GLFW.GLFW_KEY_F && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            headerPanel.focusSearch();
            return true;
        }
        
        // Tab to cycle through categories
        if (keyCode == GLFW.GLFW_KEY_TAB && modifiers == 0) {
            cycleCategory(1);
            return true;
        }
        
        // Shift+Tab to cycle backwards
        if (keyCode == GLFW.GLFW_KEY_TAB && (modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
            cycleCategory(-1);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (headerPanel.charTyped(chr, modifiers)) return true;
        if (settingsPanel.charTyped(chr, modifiers)) return true;
        
        return super.charTyped(chr, modifiers);
    }
    
    // Category management
    public void selectCategory(ModuleCategory category) {
        if (this.selectedCategory != category) {
            this.selectedCategory = category;
            this.selectedModule = null; // Clear selected module when changing category
            modulePanel.onCategoryChanged(category);
            settingsPanel.clearSettings();
            Team.LOGGER.debug("Selected category: {}", category.getName());
        }
    }
    
    private void cycleCategory(int direction) {
        ModuleCategory[] categories = ModuleCategory.values();
        int currentIndex = selectedCategory.ordinal();
        int newIndex = (currentIndex + direction + categories.length) % categories.length;
        selectCategory(categories[newIndex]);
    }
    
    // Module management
    public void selectModule(Module module) {
        if (this.selectedModule != module) {
            this.selectedModule = module;
            settingsPanel.setModule(module);
            Team.LOGGER.debug("Selected module: {}", module != null ? module.getName() : "none");
        }
    }
    
    // Search management
    public void setSearchQuery(String query) {
        if (!this.searchQuery.equals(query)) {
            this.searchQuery = query;
            modulePanel.onSearchChanged(query);
            Team.LOGGER.debug("Search query changed: '{}'", query);
        }
    }
    
    // View management
    public void toggleViewMode() {
        this.isGridView = !this.isGridView;
        modulePanel.setGridView(isGridView);
    }
    
    // Getters
    public ModuleCategory getSelectedCategory() {
        return selectedCategory;
    }
    
    public Module getSelectedModule() {
        return selectedModule;
    }
    
    public String getSearchQuery() {
        return searchQuery;
    }
    
    public boolean isGridView() {
        return isGridView;
    }
    
    public List<Module> getFilteredModules() {
        List<Module> modules = Team.getInstance().getModuleManager().getModulesByCategory(selectedCategory);
        
        if (searchQuery.isEmpty()) {
            return modules;
        }
        
        List<Module> filtered = new ArrayList<>();
        String query = searchQuery.toLowerCase();
        
        for (Module module : modules) {
            if (module.getName().toLowerCase().contains(query) ||
                module.getDescription().toLowerCase().contains(query)) {
                filtered.add(module);
            }
        }
        
        return filtered;
    }
    
    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }
    
    @Override
    public void close() {
        // Save GUI preferences
        saveGUIPreferences();
        super.close();
    }
    
    private void saveGUIPreferences() {
        try {
            Team.getInstance().getConfig().setString("gui.selected_category", selectedCategory.name());
            Team.getInstance().getConfig().setBoolean("gui.grid_view", isGridView);
            Team.LOGGER.debug("Saved GUI preferences");
        } catch (Exception e) {
            Team.LOGGER.error("Failed to save GUI preferences", e);
        }
    }
    
    private void loadGUIPreferences() {
        try {
            if (Team.getInstance().getConfig().hasKey("gui.selected_category")) {
                String categoryName = Team.getInstance().getConfig().getString("gui.selected_category");
                try {
                    selectedCategory = ModuleCategory.valueOf(categoryName);
                } catch (IllegalArgumentException ignored) {
                    // Invalid category name, use default
                }
            }
            
            if (Team.getInstance().getConfig().hasKey("gui.grid_view")) {
                isGridView = Team.getInstance().getConfig().getBoolean("gui.grid_view");
            }
            
            Team.LOGGER.debug("Loaded GUI preferences");
        } catch (Exception e) {
            Team.LOGGER.error("Failed to load GUI preferences", e);
        }
    }
}