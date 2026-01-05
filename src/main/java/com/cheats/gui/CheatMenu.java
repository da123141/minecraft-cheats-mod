package com.cheats.gui;

import java.util.*;
import org.lwjgl.glfw.GLFW;

/**
 * CheatMenu - A comprehensive GUI menu system for the Minecraft Cheats Mod
 * Features: Expandable categories, toggle buttons, draggable window, smooth scrolling, color-coded categories
 */
public class CheatMenu {
    
    // Window properties
    private float windowX;
    private float windowY;
    private static final float WINDOW_WIDTH = 250.0f;
    private static final float WINDOW_HEIGHT = 400.0f;
    private static final float TITLE_HEIGHT = 25.0f;
    private static final float CATEGORY_HEIGHT = 20.0f;
    private static final float TOGGLE_HEIGHT = 18.0f;
    
    // Dragging
    private boolean isDragging = false;
    private float dragOffsetX = 0.0f;
    private float dragOffsetY = 0.0f;
    
    // Scrolling
    private float scrollOffset = 0.0f;
    private float scrollVelocity = 0.0f;
    private static final float SCROLL_SMOOTHING = 0.85f;
    private static final float SCROLL_SPEED = 15.0f;
    private static final float MAX_SCROLL_VELOCITY = 20.0f;
    
    // UI State
    private boolean isVisible = true;
    private Map<String, CategorySection> categories;
    private float contentHeight = 0.0f;
    
    // Color scheme (RGB)
    private static final int[] COLOR_TITLE = {0x2C, 0x3E, 0x50};      // Dark slate
    private static final int[] COLOR_BACKGROUND = {0x34, 0x49, 0x5E};  // Dark blue-gray
    private static final int[] COLOR_BORDER = {0x16, 0x27, 0x3F};      // Darker border
    private static final int[] COLOR_TEXT = {0xFF, 0xFF, 0xFF};        // White
    
    // Category colors (RGB)
    private static final Map<String, int[]> CATEGORY_COLORS = Map.ofEntries(
        Map.entry("Movement", new int[]{0x3498, 0xDB}),      // Blue
        Map.entry("Combat", new int[]{0xE7, 0x4C, 0x3C}),    // Red
        Map.entry("Utility", new int[]{0x2E, 0xCC, 0x71}),   // Green
        Map.entry("Render", new int[]{0xF3, 0x9C, 0x12}),    // Orange
        Map.entry("World", new int[]{0x95, 0x4A, 0x28}),     // Brown
        Map.entry("Player", new int[]{0x8E, 0x44, 0xAD})     // Purple
    );
    
    // Inner class for category sections
    public static class CategorySection {
        public String name;
        public boolean isExpanded;
        public List<CheatToggle> toggles;
        public int[] categoryColor;
        
        public CategorySection(String name, int[] color) {
            this.name = name;
            this.isExpanded = true;
            this.toggles = new ArrayList<>();
            this.categoryColor = color;
        }
        
        public void addToggle(CheatToggle toggle) {
            this.toggles.add(toggle);
        }
        
        public float getHeight() {
            float height = CATEGORY_HEIGHT;
            if (isExpanded) {
                height += toggles.size() * TOGGLE_HEIGHT;
            }
            return height;
        }
    }
    
    // Inner class for toggle buttons
    public static class CheatToggle {
        public String label;
        public boolean isEnabled;
        public Runnable onToggle;
        
        public CheatToggle(String label, boolean defaultState, Runnable onToggle) {
            this.label = label;
            this.isEnabled = defaultState;
            this.onToggle = onToggle;
        }
        
        public void toggle() {
            this.isEnabled = !this.isEnabled;
            if (onToggle != null) {
                onToggle.run();
            }
        }
    }
    
    // Constructor
    public CheatMenu() {
        this.windowX = 20.0f;
        this.windowY = 20.0f;
        this.categories = new LinkedHashMap<>();
        initializeCategories();
    }
    
    /**
     * Initialize default cheat categories
     */
    private void initializeCategories() {
        // Movement Category
        CategorySection movement = new CategorySection("Movement", new int[]{0x3A, 0x9E, 0xD5});
        movement.addToggle(new CheatToggle("Flight", false, () -> System.out.println("Flight toggled")));
        movement.addToggle(new CheatToggle("Speed", false, () -> System.out.println("Speed toggled")));
        movement.addToggle(new CheatToggle("NoClip", false, () -> System.out.println("NoClip toggled")));
        movement.addToggle(new CheatToggle("Glide", false, () -> System.out.println("Glide toggled")));
        categories.put("Movement", movement);
        
        // Combat Category
        CategorySection combat = new CategorySection("Combat", new int[]{0xE7, 0x4C, 0x3C});
        combat.addToggle(new CheatToggle("KillAura", false, () -> System.out.println("KillAura toggled")));
        combat.addToggle(new CheatToggle("Reach", false, () -> System.out.println("Reach toggled")));
        combat.addToggle(new CheatToggle("AutoClicker", false, () -> System.out.println("AutoClicker toggled")));
        categories.put("Combat", combat);
        
        // Utility Category
        CategorySection utility = new CategorySection("Utility", new int[]{0x2E, 0xCC, 0x71});
        utility.addToggle(new CheatToggle("FullBright", false, () -> System.out.println("FullBright toggled")));
        utility.addToggle(new CheatToggle("FreeCam", false, () -> System.out.println("FreeCam toggled")));
        utility.addToggle(new CheatToggle("AutoEat", false, () -> System.out.println("AutoEat toggled")));
        utility.addToggle(new CheatToggle("AutoFish", false, () -> System.out.println("AutoFish toggled")));
        categories.put("Utility", utility);
        
        // Render Category
        CategorySection render = new CategorySection("Render", new int[]{0xF3, 0x9C, 0x12});
        render.addToggle(new CheatToggle("ESP", false, () -> System.out.println("ESP toggled")));
        render.addToggle(new CheatToggle("Tracers", false, () -> System.out.println("Tracers toggled")));
        render.addToggle(new CheatToggle("Chams", false, () -> System.out.println("Chams toggled")));
        categories.put("Render", render);
        
        // World Category
        CategorySection world = new CategorySection("World", new int[]{0x95, 0x4A, 0x28});
        world.addToggle(new CheatToggle("FastBreak", false, () -> System.out.println("FastBreak toggled")));
        world.addToggle(new CheatToggle("InfiniteBlocks", false, () -> System.out.println("InfiniteBlocks toggled")));
        categories.put("World", world);
        
        // Player Category
        CategorySection player = new CategorySection("Player", new int[]{0x8E, 0x44, 0xAD});
        player.addToggle(new CheatToggle("God Mode", false, () -> System.out.println("God Mode toggled")));
        player.addToggle(new CheatToggle("InfiniteJump", false, () -> System.out.println("InfiniteJump toggled")));
        categories.put("Player", player);
        
        calculateContentHeight();
    }
    
    /**
     * Calculate total content height
     */
    private void calculateContentHeight() {
        contentHeight = TITLE_HEIGHT;
        for (CategorySection category : categories.values()) {
            contentHeight += category.getHeight() + 5.0f; // 5px padding
        }
    }
    
    /**
     * Handle mouse click events
     */
    public void handleMouseClick(float mouseX, float mouseY, int button) {
        if (!isVisible) return;
        
        // Check close button (top-right corner)
        if (isMouseInCloseButton(mouseX, mouseY) && button == 0) {
            isVisible = false;
            return;
        }
        
        // Check title bar for dragging
        if (isMouseInTitleBar(mouseX, mouseY) && button == 0) {
            isDragging = true;
            dragOffsetX = mouseX - windowX;
            dragOffsetY = mouseY - windowY;
            return;
        }
        
        // Check category headers and toggles
        float currentY = windowY + TITLE_HEIGHT - scrollOffset;
        
        for (CategorySection category : categories.values()) {
            // Check category header click
            if (mouseX >= windowX && mouseX <= windowX + WINDOW_WIDTH &&
                mouseY >= currentY && mouseY <= currentY + CATEGORY_HEIGHT) {
                
                if (button == 0) {
                    category.isExpanded = !category.isExpanded;
                    calculateContentHeight();
                }
                return;
            }
            
            currentY += CATEGORY_HEIGHT;
            
            // Check toggle clicks
            if (category.isExpanded) {
                for (CheatToggle toggle : category.toggles) {
                    if (mouseX >= windowX + 10 && mouseX <= windowX + WINDOW_WIDTH - 10 &&
                        mouseY >= currentY && mouseY <= currentY + TOGGLE_HEIGHT &&
                        button == 0) {
                        toggle.toggle();
                        return;
                    }
                    currentY += TOGGLE_HEIGHT;
                }
            }
            
            currentY += 5.0f; // padding
        }
    }
    
    /**
     * Handle mouse drag
     */
    public void handleMouseDrag(float mouseX, float mouseY) {
        if (isDragging && isVisible) {
            windowX = mouseX - dragOffsetX;
            windowY = mouseY - dragOffsetY;
        }
    }
    
    /**
     * Handle mouse release
     */
    public void handleMouseRelease(int button) {
        if (button == 0) {
            isDragging = false;
        }
    }
    
    /**
     * Handle scroll wheel input
     */
    public void handleScroll(float scrollAmount) {
        if (!isVisible) return;
        
        float mouseX = getMouseX();
        float mouseY = getMouseY();
        
        if (isMouseInWindow(mouseX, mouseY)) {
            scrollVelocity = Math.max(-MAX_SCROLL_VELOCITY, 
                                     Math.min(MAX_SCROLL_VELOCITY, 
                                             scrollVelocity + scrollAmount * SCROLL_SPEED));
        }
    }
    
    /**
     * Update smooth scrolling with velocity
     */
    public void updateScrolling() {
        if (Math.abs(scrollVelocity) > 0.1f) {
            scrollOffset += scrollVelocity;
            scrollVelocity *= SCROLL_SMOOTHING;
        } else {
            scrollVelocity = 0.0f;
        }
        
        // Clamp scroll offset
        float maxScroll = Math.max(0, contentHeight - WINDOW_HEIGHT);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
    }
    
    /**
     * Render the menu
     */
    public void render(float partialTicks) {
        if (!isVisible) return;
        
        updateScrolling();
        
        // Draw window background
        drawRect(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, COLOR_BACKGROUND);
        
        // Draw border
        drawBorder(windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, COLOR_BORDER, 1.5f);
        
        // Draw title bar
        drawRect(windowX, windowY, WINDOW_WIDTH, TITLE_HEIGHT, COLOR_TITLE);
        drawText("Cheats Menu", windowX + 10, windowY + 6, COLOR_TEXT);
        
        // Draw close button
        drawCloseButton();
        
        // Setup scissor test for scrollable content area
        enableScissor((int)windowX, (int)(windowY + TITLE_HEIGHT), 
                     (int)WINDOW_WIDTH, (int)(WINDOW_HEIGHT - TITLE_HEIGHT));
        
        // Draw categories and toggles
        float currentY = windowY + TITLE_HEIGHT - scrollOffset;
        
        for (CategorySection category : categories.values()) {
            // Draw category header
            drawCategoryHeader(windowX, currentY, category);
            currentY += CATEGORY_HEIGHT;
            
            // Draw toggles
            if (category.isExpanded) {
                for (CheatToggle toggle : category.toggles) {
                    drawToggleButton(windowX + 10, currentY, toggle);
                    currentY += TOGGLE_HEIGHT;
                }
            }
            
            currentY += 5.0f; // padding
        }
        
        disableScissor();
    }
    
    /**
     * Draw category header
     */
    private void drawCategoryHeader(float x, float y, CategorySection category) {
        int[] headerColor = category.categoryColor;
        drawRect(x, y, WINDOW_WIDTH, CATEGORY_HEIGHT, headerColor);
        
        String expandedIndicator = category.isExpanded ? "▼" : "▶";
        drawText(expandedIndicator + " " + category.name, x + 5, y + 4, COLOR_TEXT);
    }
    
    /**
     * Draw toggle button
     */
    private void drawToggleButton(float x, float y, CheatToggle toggle) {
        // Background
        int[] bgColor = toggle.isEnabled ? new int[]{0x27, 0xAE, 0x60} : new int[]{0x2C, 0x3E, 0x50};
        drawRect(x, y, WINDOW_WIDTH - 20, TOGGLE_HEIGHT, bgColor);
        
        // Toggle indicator
        String indicator = toggle.isEnabled ? "[ON]" : "[OFF]";
        int[] indicatorColor = toggle.isEnabled ? new int[]{0x2E, 0xFF, 0x71} : new int[]{0xFF, 0x7F, 0x7F};
        
        drawText(toggle.label, x + 5, y + 4, COLOR_TEXT);
        drawText(indicator, x + WINDOW_WIDTH - 55, y + 4, indicatorColor);
    }
    
    /**
     * Draw close button (X in top-right)
     */
    private void drawCloseButton() {
        float closeX = windowX + WINDOW_WIDTH - 20;
        float closeY = windowY + 3;
        drawRect(closeX, closeY, 16, 16, new int[]{0xE7, 0x4C, 0x3C});
        drawText("X", closeX + 5, closeY + 3, COLOR_TEXT);
    }
    
    /**
     * Check if mouse is in close button
     */
    private boolean isMouseInCloseButton(float mouseX, float mouseY) {
        float closeX = windowX + WINDOW_WIDTH - 20;
        float closeY = windowY + 3;
        return mouseX >= closeX && mouseX <= closeX + 16 &&
               mouseY >= closeY && mouseY <= closeY + 16;
    }
    
    /**
     * Check if mouse is in title bar
     */
    private boolean isMouseInTitleBar(float mouseX, float mouseY) {
        return mouseX >= windowX && mouseX <= windowX + WINDOW_WIDTH - 20 &&
               mouseY >= windowY && mouseY <= windowY + TITLE_HEIGHT;
    }
    
    /**
     * Check if mouse is in window
     */
    private boolean isMouseInWindow(float mouseX, float mouseY) {
        return mouseX >= windowX && mouseX <= windowX + WINDOW_WIDTH &&
               mouseY >= windowY && mouseY <= windowY + WINDOW_HEIGHT;
    }
    
    /**
     * Draw rectangle (placeholder for actual rendering)
     */
    private void drawRect(float x, float y, float width, float height, int[] color) {
        // Implementation depends on rendering engine (Minecraft/LWJGL)
        // Example: RenderSystem.setShaderColor(r, g, b, a)
        // Fill quad at (x, y) with size (width, height)
    }
    
    /**
     * Draw border around rectangle
     */
    private void drawBorder(float x, float y, float width, float height, int[] color, float thickness) {
        // Draw top, bottom, left, right borders
        drawRect(x, y, width, thickness, color);                          // Top
        drawRect(x, y + height - thickness, width, thickness, color);     // Bottom
        drawRect(x, y, thickness, height, color);                         // Left
        drawRect(x + width - thickness, y, thickness, height, color);     // Right
    }
    
    /**
     * Draw text (placeholder for actual rendering)
     */
    private void drawText(String text, float x, float y, int[] color) {
        // Implementation depends on text rendering system
        // Usually uses Minecraft's font renderer
    }
    
    /**
     * Enable scissor test for clipping (placeholder)
     */
    private void enableScissor(int x, int y, int width, int height) {
        // Enable OpenGL scissor test to clip content
        // GL11.glEnable(GL11.GL_SCISSOR_TEST);
        // GL11.glScissor(x, screenHeight - (y + height), width, height);
    }
    
    /**
     * Disable scissor test
     */
    private void disableScissor() {
        // GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
    
    /**
     * Get current mouse X position (placeholder)
     */
    private float getMouseX() {
        // return (float) MouseHelper.getX();
        return 0.0f;
    }
    
    /**
     * Get current mouse Y position (placeholder)
     */
    private float getMouseY() {
        // return (float) MouseHelper.getY();
        return 0.0f;
    }
    
    // Getters and setters
    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }
    
    public boolean isVisible() {
        return isVisible;
    }
    
    public void addCategory(String name, int[] color) {
        categories.put(name, new CategorySection(name, color));
        calculateContentHeight();
    }
    
    public void addToggleToCategory(String categoryName, CheatToggle toggle) {
        CategorySection section = categories.get(categoryName);
        if (section != null) {
            section.addToggle(toggle);
            calculateContentHeight();
        }
    }
    
    public CategorySection getCategory(String name) {
        return categories.get(name);
    }
    
    public void setWindowPosition(float x, float y) {
        this.windowX = x;
        this.windowY = y;
    }
    
    public float getWindowX() {
        return windowX;
    }
    
    public float getWindowY() {
        return windowY;
    }
}
