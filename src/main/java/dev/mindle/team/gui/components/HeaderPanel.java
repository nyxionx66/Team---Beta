package dev.mindle.team.gui.components;

import dev.mindle.team.gui.ClickGUIScreen;
import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.glfw.GLFW;

public class HeaderPanel extends BasePanel {
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 32;
    private static final int SEARCH_WIDTH = 200;
    
    private TextFieldWidget searchField;
    private final AnimationUtils.HoverAnimation gridViewAnimation = new AnimationUtils.HoverAnimation();
    private final AnimationUtils.HoverAnimation listViewAnimation = new AnimationUtils.HoverAnimation();
    private final AnimationUtils.HoverAnimation settingsAnimation = new AnimationUtils.HoverAnimation();
    
    private boolean gridViewHovered = false;
    private boolean listViewHovered = false;
    private boolean settingsHovered = false;
    
    public HeaderPanel(ClickGUIScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void init() {
        // Create search field
        if (searchField != null) {
            searchField.setTextColor(theme.getTextPrimary());
        }
    }
    
    @Override
    protected void renderBackground(DrawContext context, float alpha) {
        // Header background with subtle gradient
        int backgroundColor = theme.applyAlpha(theme.getSurfaceVariant(), alpha);
        RenderUtils.drawRoundedRect(context, x, y, width, height, 0, backgroundColor);
        
        // Bottom border
        int borderColor = theme.applyAlpha(theme.getBorder(), alpha);
        context.fill(x, y + height - 1, x + width, y + height, borderColor);
    }
    
    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        // Update animations
        gridViewAnimation.update(gridViewHovered, delta);
        listViewAnimation.update(listViewHovered, delta);
        settingsAnimation.update(settingsHovered, delta);
        
        // Render title
        renderTitle(context, alpha);
        
        // Render search field
        renderSearchField(context, mouseX, mouseY, alpha);
        
        // Render view controls
        renderViewControls(context, mouseX, mouseY, alpha);
        
        // Render action buttons
        renderActionButtons(context, mouseX, mouseY, alpha);
    }
    
    private void renderTitle(DrawContext context, float alpha) {
        String title = "ClickGUI";
        int titleX = x + ThemeManager.Spacing.PANEL_PADDING;
        int titleY = y + (height - context.getTextRenderer().fontHeight) / 2;
        
        context.drawText(context.getTextRenderer(), title, titleX, titleY, 
            theme.applyAlpha(theme.getTextPrimary(), alpha), false);
    }
    
    private void renderSearchField(DrawContext context, double mouseX, double mouseY, float alpha) {
        int searchX = x + width / 2 - SEARCH_WIDTH / 2;
        int searchY = y + (height - ThemeManager.ComponentSize.INPUT_HEIGHT) / 2;
        
        // Search field background
        boolean searchHovered = RenderUtils.isPointInRect(mouseX, mouseY, searchX, searchY, SEARCH_WIDTH, ThemeManager.ComponentSize.INPUT_HEIGHT);
        int searchBg = searchHovered ? theme.getHover() : theme.getSurface();
        
        RenderUtils.drawRoundedRect(context, searchX, searchY, SEARCH_WIDTH, ThemeManager.ComponentSize.INPUT_HEIGHT,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(searchBg, alpha));
        
        RenderUtils.drawRoundedBorder(context, searchX, searchY, SEARCH_WIDTH, ThemeManager.ComponentSize.INPUT_HEIGHT,
            ThemeManager.BorderRadius.SMALL, 1, theme.applyAlpha(theme.getBorder(), alpha));
        
        // Search icon
        String searchIcon = "🔍";
        int iconX = searchX + ThemeManager.Spacing.MEDIUM;
        int iconY = searchY + (ThemeManager.ComponentSize.INPUT_HEIGHT - context.getTextRenderer().fontHeight) / 2;
        context.drawText(context.getTextRenderer(), searchIcon, iconX, iconY, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        
        // Search text
        String searchText = parent.getSearchQuery();
        if (searchText.isEmpty()) {
            searchText = "Search modules...";
            int placeholderColor = theme.applyAlpha(theme.getTextDisabled(), alpha);
            context.drawText(context.getTextRenderer(), searchText, iconX + 20, iconY, placeholderColor, false);
        } else {
            int textColor = theme.applyAlpha(theme.getTextPrimary(), alpha);
            context.drawText(context.getTextRenderer(), searchText, iconX + 20, iconY, textColor, false);
        }
    }
    
    private void renderViewControls(DrawContext context, int mouseX, int mouseY, float alpha) {
        int controlsX = x + width - 200; // Position from right
        int controlsY = y + (height - BUTTON_HEIGHT) / 2;
        
        // Grid view button
        int gridX = controlsX;
        gridViewHovered = RenderUtils.isPointInRect(mouseX, mouseY, gridX, controlsY, BUTTON_WIDTH, BUTTON_HEIGHT);
        
        boolean isGridView = parent.isGridView();
        int gridBg = isGridView ? theme.getPrimary() : 
            theme.interpolate(theme.getSurface(), theme.getHover(), gridViewAnimation.getEasedProgress());
        
        RenderUtils.drawRoundedRect(context, gridX, controlsY, BUTTON_WIDTH, BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(gridBg, alpha));
        
        String gridText = "Grid";
        int gridTextColor = isGridView ? theme.getTextPrimary() : RenderUtils.getTextColor(true, gridViewHovered);
        RenderUtils.drawCenteredText(context, gridText, gridX, 
            controlsY + (BUTTON_HEIGHT - context.getTextRenderer().fontHeight) / 2, 
            BUTTON_WIDTH, theme.applyAlpha(gridTextColor, alpha));
        
        // List view button
        int listX = controlsX + BUTTON_WIDTH + ThemeManager.Spacing.SMALL;
        listViewHovered = RenderUtils.isPointInRect(mouseX, mouseY, listX, controlsY, BUTTON_WIDTH, BUTTON_HEIGHT);
        
        boolean isListView = !parent.isGridView();
        int listBg = isListView ? theme.getPrimary() : 
            theme.interpolate(theme.getSurface(), theme.getHover(), listViewAnimation.getEasedProgress());
        
        RenderUtils.drawRoundedRect(context, listX, controlsY, BUTTON_WIDTH, BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(listBg, alpha));
        
        String listText = "List";
        int listTextColor = isListView ? theme.getTextPrimary() : RenderUtils.getTextColor(true, listViewHovered);
        RenderUtils.drawCenteredText(context, listText, listX, 
            controlsY + (BUTTON_HEIGHT - context.getTextRenderer().fontHeight) / 2, 
            BUTTON_WIDTH, theme.applyAlpha(listTextColor, alpha));
    }
    
    private void renderActionButtons(DrawContext context, int mouseX, int mouseY, float alpha) {
        int buttonX = x + width - BUTTON_WIDTH - ThemeManager.Spacing.PANEL_PADDING;
        int buttonY = y + (height - BUTTON_HEIGHT) / 2;
        
        // Settings button
        settingsHovered = RenderUtils.isPointInRect(mouseX, mouseY, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT);
        
        int settingsBg = theme.interpolate(theme.getSurface(), theme.getHover(), settingsAnimation.getEasedProgress());
        RenderUtils.drawRoundedRect(context, buttonX, buttonY, BUTTON_WIDTH, BUTTON_HEIGHT,
            ThemeManager.BorderRadius.SMALL, theme.applyAlpha(settingsBg, alpha));
        
        String settingsText = "⚙ Settings";
        int settingsTextColor = RenderUtils.getTextColor(true, settingsHovered);
        RenderUtils.drawCenteredText(context, settingsText, buttonX, 
            buttonY + (BUTTON_HEIGHT - context.getTextRenderer().fontHeight) / 2, 
            BUTTON_WIDTH, theme.applyAlpha(settingsTextColor, alpha));
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        if (button == 0) { // Left click
            int controlsX = x + width - 200;
            int controlsY = y + (height - BUTTON_HEIGHT) / 2;
            
            // Grid view button
            if (RenderUtils.isPointInRect(mouseX, mouseY, controlsX, controlsY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                if (!parent.isGridView()) {
                    parent.toggleViewMode();
                }
                return true;
            }
            
            // List view button
            int listX = controlsX + BUTTON_WIDTH + ThemeManager.Spacing.SMALL;
            if (RenderUtils.isPointInRect(mouseX, mouseY, listX, controlsY, BUTTON_WIDTH, BUTTON_HEIGHT)) {
                if (parent.isGridView()) {
                    parent.toggleViewMode();
                }
                return true;
            }
            
            // Search field
            int searchX = x + width / 2 - SEARCH_WIDTH / 2;
            int searchY = y + (height - ThemeManager.ComponentSize.INPUT_HEIGHT) / 2;
            if (RenderUtils.isPointInRect(mouseX, mouseY, searchX, searchY, SEARCH_WIDTH, ThemeManager.ComponentSize.INPUT_HEIGHT)) {
                // Focus search field (handled by the field itself)
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle search input
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            String currentQuery = parent.getSearchQuery();
            if (!currentQuery.isEmpty()) {
                parent.setSearchQuery(currentQuery.substring(0, currentQuery.length() - 1));
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean charTyped(char chr, int modifiers) {
        // Add character to search
        if (Character.isLetterOrDigit(chr) || Character.isSpaceChar(chr)) {
            String currentQuery = parent.getSearchQuery();
            parent.setSearchQuery(currentQuery + chr);
            return true;
        }
        
        return false;
    }
    
    public void focusSearch() {
        // Implementation for focusing search field
        // Could set a flag or directly manipulate the search field
    }
}