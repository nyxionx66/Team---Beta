package dev.mindle.team.gui.components;

import dev.mindle.team.Team;
import dev.mindle.team.gui.ClickGUIScreen;
import dev.mindle.team.gui.theme.ThemeManager;
import dev.mindle.team.gui.utils.AnimationUtils;
import dev.mindle.team.gui.utils.RenderUtils;
import dev.mindle.team.module.Module;
import dev.mindle.team.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModulePanel extends BasePanel {
    private static final int GRID_COLUMNS = 3;
    private static final int CARD_WIDTH = 180;
    private static final int CARD_HEIGHT = ThemeManager.ComponentSize.MODULE_CARD_HEIGHT;
    private static final int CARD_SPACING = 12;
    private static final int LIST_ITEM_HEIGHT = 48;
    
    private final Map<Module, AnimationUtils.HoverAnimation> moduleAnimations = new HashMap<>();
    private final Map<Module, AnimationUtils.ScaleAnimation> moduleScaleAnimations = new HashMap<>();
    private float scrollOffset = 0.0f;
    private float targetScrollOffset = 0.0f;
    private boolean isGridView = true;
    
    public ModulePanel(ClickGUIScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void init() {
        // Initialize animations for modules
        List<Module> allModules = Team.getInstance().getModuleManager().getAllModules();
        for (Module module : allModules) {
            moduleAnimations.put(module, new AnimationUtils.HoverAnimation());
            moduleScaleAnimations.put(module, new AnimationUtils.ScaleAnimation());
        }
    }
    
    @Override
    protected void renderContent(DrawContext context, int mouseX, int mouseY, float delta, float alpha) {
        // Update scroll animation
        scrollOffset = AnimationUtils.updateAnimation(scrollOffset, targetScrollOffset, delta * 6.0f);
        
        // Get filtered modules
        List<Module> modules = parent.getFilteredModules();
        
        if (modules.isEmpty()) {
            renderEmptyState(context, alpha);
            return;
        }
        
        // Setup scissor for scrolling
        context.enableScissor(x, y, x + width, y + height);
        
        if (isGridView) {
            renderGridView(context, modules, mouseX, mouseY, delta, alpha);
        } else {
            renderListView(context, modules, mouseX, mouseY, delta, alpha);
        }
        
        context.disableScissor();
        
        // Render scrollbar if needed
        renderScrollbar(context, modules.size(), alpha);
    }
    
    private void renderGridView(DrawContext context, List<Module> modules, int mouseX, int mouseY, float delta, float alpha) {
        int startX = x + ThemeManager.Spacing.PANEL_PADDING;
        int startY = y + ThemeManager.Spacing.PANEL_PADDING + (int) scrollOffset;
        
        int availableWidth = width - 2 * ThemeManager.Spacing.PANEL_PADDING;
        int actualColumns = Math.max(1, availableWidth / (CARD_WIDTH + CARD_SPACING));
        int cardWidth = (availableWidth - (actualColumns - 1) * CARD_SPACING) / actualColumns;
        
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            
            int col = i % actualColumns;
            int row = i / actualColumns;
            
            int cardX = startX + col * (cardWidth + CARD_SPACING);
            int cardY = startY + row * (CARD_HEIGHT + CARD_SPACING);
            
            // Skip if card is not visible
            if (cardY + CARD_HEIGHT < y || cardY > y + height) continue;
            
            renderModuleCard(context, module, cardX, cardY, cardWidth, CARD_HEIGHT, mouseX, mouseY, delta, alpha);
        }
    }
    
    private void renderListView(DrawContext context, List<Module> modules, int mouseX, int mouseY, float delta, float alpha) {
        int startX = x + ThemeManager.Spacing.PANEL_PADDING;
        int startY = y + ThemeManager.Spacing.PANEL_PADDING + (int) scrollOffset;
        int itemWidth = width - 2 * ThemeManager.Spacing.PANEL_PADDING;
        
        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);
            int itemY = startY + i * (LIST_ITEM_HEIGHT + ThemeManager.Spacing.SMALL);
            
            // Skip if item is not visible
            if (itemY + LIST_ITEM_HEIGHT < y || itemY > y + height) continue;
            
            renderModuleListItem(context, module, startX, itemY, itemWidth, LIST_ITEM_HEIGHT, mouseX, mouseY, delta, alpha);
        }
    }
    
    private void renderModuleCard(DrawContext context, Module module, int cardX, int cardY, int cardWidth, int cardHeight, 
                                  int mouseX, int mouseY, float delta, float alpha) {
        boolean isSelected = parent.getSelectedModule() == module;
        boolean isHovered = RenderUtils.isPointInRect(mouseX, mouseY, cardX, cardY, cardWidth, cardHeight);
        boolean isEnabled = module.isEnabled();
        
        // Update animations
        AnimationUtils.HoverAnimation hoverAnim = moduleAnimations.get(module);
        AnimationUtils.ScaleAnimation scaleAnim = moduleScaleAnimations.get(module);
        
        if (hoverAnim != null) hoverAnim.update(isHovered, delta);
        if (scaleAnim != null) scaleAnim.update(false, isHovered, delta);
        
        // Calculate colors and transforms
        float hoverProgress = hoverAnim != null ? hoverAnim.getEasedProgress() : 0;
        float scale = scaleAnim != null ? scaleAnim.getScale() : 1.0f;
        
        // Apply scale transform
        int scaledWidth = (int) (cardWidth * scale);
        int scaledHeight = (int) (cardHeight * scale);
        int scaledX = cardX - (scaledWidth - cardWidth) / 2;
        int scaledY = cardY - (scaledHeight - cardHeight) / 2;
        
        // Background color
        int backgroundColor;
        if (isSelected) {
            backgroundColor = theme.applyAlpha(theme.getPrimary(), alpha * 0.3f);
        } else {
            backgroundColor = theme.applyAlpha(
                theme.interpolate(theme.getSurface(), theme.getHover(), hoverProgress), 
                alpha
            );
        }
        
        // Render shadow
        if (isHovered) {
            RenderUtils.drawShadow(context, scaledX, scaledY, scaledWidth, scaledHeight, 4, theme.getShadow());
        }
        
        // Render card background
        RenderUtils.drawRoundedRect(context, scaledX, scaledY, scaledWidth, scaledHeight, 
            ThemeManager.BorderRadius.MEDIUM, backgroundColor);
        
        // Render border
        int borderColor = isSelected ? theme.getPrimary() : theme.getBorder();
        RenderUtils.drawRoundedBorder(context, scaledX, scaledY, scaledWidth, scaledHeight, 
            ThemeManager.BorderRadius.MEDIUM, 1, theme.applyAlpha(borderColor, alpha));
        
        // Content area
        int contentX = scaledX + ThemeManager.Spacing.MEDIUM;
        int contentY = scaledY + ThemeManager.Spacing.MEDIUM;
        int contentWidth = scaledWidth - 2 * ThemeManager.Spacing.MEDIUM;
        
        // Module name
        String name = module.getName();
        int nameColor = isEnabled ? theme.getSuccess() : theme.getTextPrimary();
        context.drawText(context.getTextRenderer(), name, contentX, contentY, 
            theme.applyAlpha(nameColor, alpha), false);
        
        // Module description
        String description = module.getDescription();
        String truncatedDesc = RenderUtils.truncateText(context.getTextRenderer(), description, contentWidth);
        int descY = contentY + context.getTextRenderer().fontHeight + 4;
        context.drawText(context.getTextRenderer(), truncatedDesc, contentX, descY, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        
        // Status indicator
        int statusSize = 8;
        int statusX = scaledX + scaledWidth - statusSize - ThemeManager.Spacing.SMALL;
        int statusY = scaledY + ThemeManager.Spacing.SMALL;
        int statusColor = isEnabled ? theme.getSuccess() : theme.getTextDisabled();
        
        RenderUtils.drawRoundedRect(context, statusX, statusY, statusSize, statusSize, 
            statusSize / 2, theme.applyAlpha(statusColor, alpha));
        
        // Keybind display
        if (module.hasKeybind()) {
            String keybindText = module.getKeybind().getKeyName();
            int keybindY = scaledY + scaledHeight - context.getTextRenderer().fontHeight - ThemeManager.Spacing.SMALL;
            RenderUtils.drawRightAlignedText(context, keybindText, contentX, keybindY, contentWidth, 
                theme.applyAlpha(theme.getTextDisabled(), alpha));
        }
    }
    
    private void renderModuleListItem(DrawContext context, Module module, int itemX, int itemY, int itemWidth, int itemHeight,
                                      int mouseX, int mouseY, float delta, float alpha) {
        boolean isSelected = parent.getSelectedModule() == module;
        boolean isHovered = RenderUtils.isPointInRect(mouseX, mouseY, itemX, itemY, itemWidth, itemHeight);
        boolean isEnabled = module.isEnabled();
        
        // Update animations
        AnimationUtils.HoverAnimation hoverAnim = moduleAnimations.get(module);
        if (hoverAnim != null) hoverAnim.update(isHovered, delta);
        
        float hoverProgress = hoverAnim != null ? hoverAnim.getEasedProgress() : 0;
        
        // Background
        int backgroundColor;
        if (isSelected) {
            backgroundColor = theme.applyAlpha(theme.getPrimary(), alpha * 0.3f);
        } else {
            backgroundColor = theme.applyAlpha(
                theme.interpolate(theme.getSurface(), theme.getHover(), hoverProgress), 
                alpha
            );
        }
        
        RenderUtils.drawRoundedRect(context, itemX, itemY, itemWidth, itemHeight, 
            ThemeManager.BorderRadius.SMALL, backgroundColor);
        
        // Content
        int contentX = itemX + ThemeManager.Spacing.MEDIUM;
        int contentY = itemY + (itemHeight - context.getTextRenderer().fontHeight) / 2;
        
        // Module name
        String name = module.getName();
        int nameColor = isEnabled ? theme.getSuccess() : theme.getTextPrimary();
        context.drawText(context.getTextRenderer(), name, contentX, contentY, 
            theme.applyAlpha(nameColor, alpha), false);
        
        // Module description
        String description = module.getDescription();
        String truncatedDesc = RenderUtils.truncateText(context.getTextRenderer(), description, 
            itemWidth - 200); // Leave space for toggle and keybind
        context.drawText(context.getTextRenderer(), truncatedDesc, contentX + 120, contentY, 
            theme.applyAlpha(theme.getTextSecondary(), alpha), false);
        
        // Toggle switch
        int toggleX = itemX + itemWidth - ThemeManager.ComponentSize.TOGGLE_WIDTH - ThemeManager.Spacing.MEDIUM;
        int toggleY = itemY + (itemHeight - ThemeManager.ComponentSize.TOGGLE_HEIGHT) / 2;
        
        boolean toggleHovered = RenderUtils.isPointInRect(mouseX, mouseY, toggleX, toggleY, 
            ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT);
        
        RenderUtils.drawToggle(context, toggleX, toggleY, 
            ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT, 
            isEnabled, toggleHovered);
        
        // Keybind
        if (module.hasKeybind()) {
            String keybindText = module.getKeybind().getKeyName();
            int keybindX = toggleX - 60;
            context.drawText(context.getTextRenderer(), keybindText, keybindX, contentY, 
                theme.applyAlpha(theme.getTextDisabled(), alpha), false);
        }
    }
    
    private void renderEmptyState(DrawContext context, float alpha) {
        String emptyText = parent.getSearchQuery().isEmpty() ? 
            "No modules in this category" : 
            "No modules found for '" + parent.getSearchQuery() + "'";
        
        int textWidth = context.getTextRenderer().getWidth(emptyText);
        int textX = x + (width - textWidth) / 2;
        int textY = y + height / 2;
        
        context.drawText(context.getTextRenderer(), emptyText, textX, textY, 
            theme.applyAlpha(theme.getTextDisabled(), alpha), false);
    }
    
    private void renderScrollbar(DrawContext context, int itemCount, float alpha) {
        if (!needsScrollbar(itemCount)) return;
        
        int scrollbarWidth = 6;
        int scrollbarX = x + width - scrollbarWidth - 2;
        int scrollbarHeight = height - 4;
        int scrollbarY = y + 2;
        
        // Track
        RenderUtils.drawRoundedRect(context, scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight, 
            scrollbarWidth / 2, theme.applyAlpha(theme.getSurface(), alpha * 0.5f));
        
        // Thumb
        float maxScroll = getMaxScrollOffset(itemCount);
        if (maxScroll > 0) {
            float scrollProgress = Math.abs(targetScrollOffset) / maxScroll;
            int thumbHeight = Math.max(20, (int) (scrollbarHeight * 0.3f));
            int thumbY = scrollbarY + (int) ((scrollbarHeight - thumbHeight) * scrollProgress);
            
            RenderUtils.drawRoundedRect(context, scrollbarX, thumbY, scrollbarWidth, thumbHeight, 
                scrollbarWidth / 2, theme.applyAlpha(theme.getTextSecondary(), alpha));
        }
    }
    
    private boolean needsScrollbar(int itemCount) {
        return getMaxScrollOffset(itemCount) > 0;
    }
    
    private float getMaxScrollOffset(int itemCount) {
        if (itemCount == 0) return 0;
        
        int totalHeight;
        if (isGridView) {
            int availableWidth = width - 2 * ThemeManager.Spacing.PANEL_PADDING;
            int actualColumns = Math.max(1, availableWidth / (CARD_WIDTH + CARD_SPACING));
            int rows = (itemCount + actualColumns - 1) / actualColumns;
            totalHeight = rows * (CARD_HEIGHT + CARD_SPACING) - CARD_SPACING;
        } else {
            totalHeight = itemCount * (LIST_ITEM_HEIGHT + ThemeManager.Spacing.SMALL) - ThemeManager.Spacing.SMALL;
        }
        
        int availableHeight = height - 2 * ThemeManager.Spacing.PANEL_PADDING;
        return Math.max(0, totalHeight - availableHeight);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        
        List<Module> modules = parent.getFilteredModules();
        
        if (button == 0) { // Left click
            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);
                
                boolean clicked;
                if (isGridView) {
                    // Calculate grid position
                    int availableWidth = width - 2 * ThemeManager.Spacing.PANEL_PADDING;
                    int actualColumns = Math.max(1, availableWidth / (CARD_WIDTH + CARD_SPACING));
                    int cardWidth = (availableWidth - (actualColumns - 1) * CARD_SPACING) / actualColumns;
                    
                    int col = i % actualColumns;
                    int row = i / actualColumns;
                    
                    int cardX = x + ThemeManager.Spacing.PANEL_PADDING + col * (cardWidth + CARD_SPACING);
                    int cardY = y + ThemeManager.Spacing.PANEL_PADDING + (int) scrollOffset + row * (CARD_HEIGHT + CARD_SPACING);
                    
                    clicked = RenderUtils.isPointInRect(mouseX, mouseY, cardX, cardY, cardWidth, CARD_HEIGHT);
                } else {
                    // Calculate list position
                    int itemX = x + ThemeManager.Spacing.PANEL_PADDING;
                    int itemY = y + ThemeManager.Spacing.PANEL_PADDING + (int) scrollOffset + i * (LIST_ITEM_HEIGHT + ThemeManager.Spacing.SMALL);
                    int itemWidth = width - 2 * ThemeManager.Spacing.PANEL_PADDING;
                    
                    clicked = RenderUtils.isPointInRect(mouseX, mouseY, itemX, itemY, itemWidth, LIST_ITEM_HEIGHT);
                    
                    // Check if toggle was clicked
                    int toggleX = itemX + itemWidth - ThemeManager.ComponentSize.TOGGLE_WIDTH - ThemeManager.Spacing.MEDIUM;
                    int toggleY = itemY + (LIST_ITEM_HEIGHT - ThemeManager.ComponentSize.TOGGLE_HEIGHT) / 2;
                    
                    if (RenderUtils.isPointInRect(mouseX, mouseY, toggleX, toggleY, 
                        ThemeManager.ComponentSize.TOGGLE_WIDTH, ThemeManager.ComponentSize.TOGGLE_HEIGHT)) {
                        module.toggle();
                        return true;
                    }
                }
                
                if (clicked) {
                    parent.selectModule(module);
                    return true;
                }
            }
        } else if (button == 1) { // Right click - toggle module
            // Similar logic but for toggling
            for (int i = 0; i < modules.size(); i++) {
                Module module = modules.get(i);
                // ... position calculation same as above ...
                // module.toggle();
            }
        }
        
        return false;
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!isMouseOver(mouseX, mouseY)) return false;
        
        List<Module> modules = parent.getFilteredModules();
        float maxScroll = getMaxScrollOffset(modules.size());
        
        if (maxScroll > 0) {
            float scrollAmount = (float) (verticalAmount * 30); // Scroll speed
            targetScrollOffset = Math.max(-maxScroll, Math.min(0, targetScrollOffset + scrollAmount));
            return true;
        }
        
        return false;
    }
    
    public void onCategoryChanged(ModuleCategory category) {
        // Reset scroll when category changes
        scrollOffset = 0;
        targetScrollOffset = 0;
    }
    
    public void onSearchChanged(String query) {
        // Reset scroll when search changes
        scrollOffset = 0;
        targetScrollOffset = 0;
    }
    
    public void setGridView(boolean gridView) {
        if (this.isGridView != gridView) {
            this.isGridView = gridView;
            // Reset scroll when view mode changes
            scrollOffset = 0;
            targetScrollOffset = 0;
        }
    }
}