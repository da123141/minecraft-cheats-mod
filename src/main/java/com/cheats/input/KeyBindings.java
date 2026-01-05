package com.cheats.input;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.input.Keyboard;

/**
 * KeyBindings class for managing keyboard keybinds in the Minecraft cheats mod.
 * Handles registration, storage, and retrieval of keyboard shortcuts for various cheat features.
 * 
 * @author da123141
 * @since 2026-01-05
 */
public class KeyBindings {
    
    private static final Map<String, Integer> KEY_BINDINGS = new HashMap<>();
    private static final Map<String, String> KEY_DESCRIPTIONS = new HashMap<>();
    
    // Default keybind constants
    public static final int DEFAULT_TOGGLE_FLY = Keyboard.KEY_F;
    public static final int DEFAULT_TOGGLE_SPEED = Keyboard.KEY_G;
    public static final int DEFAULT_TOGGLE_NOCLIP = Keyboard.KEY_N;
    public static final int DEFAULT_TOGGLE_GODMODE = Keyboard.KEY_H;
    public static final int DEFAULT_OPEN_MENU = Keyboard.KEY_M;
    
    static {
        // Register default keybindings
        registerKeyBinding("toggle_fly", DEFAULT_TOGGLE_FLY, "Toggle Flight Mode");
        registerKeyBinding("toggle_speed", DEFAULT_TOGGLE_SPEED, "Toggle Speed Boost");
        registerKeyBinding("toggle_noclip", DEFAULT_TOGGLE_NOCLIP, "Toggle No-Clip Mode");
        registerKeyBinding("toggle_godmode", DEFAULT_TOGGLE_GODMODE, "Toggle God Mode");
        registerKeyBinding("open_menu", DEFAULT_OPEN_MENU, "Open Cheat Menu");
    }
    
    /**
     * Registers a new keybinding with the given identifier, key code, and description.
     * 
     * @param identifier The unique identifier for the keybinding
     * @param keyCode The LWJGL Keyboard key code
     * @param description A human-readable description of the keybinding
     */
    public static void registerKeyBinding(String identifier, int keyCode, String description) {
        KEY_BINDINGS.put(identifier, keyCode);
        KEY_DESCRIPTIONS.put(identifier, description);
    }
    
    /**
     * Updates an existing keybinding with a new key code.
     * 
     * @param identifier The unique identifier for the keybinding
     * @param keyCode The new LWJGL Keyboard key code
     * @return true if the keybinding was updated, false if not found
     */
    public static boolean updateKeyBinding(String identifier, int keyCode) {
        if (KEY_BINDINGS.containsKey(identifier)) {
            KEY_BINDINGS.put(identifier, keyCode);
            return true;
        }
        return false;
    }
    
    /**
     * Retrieves the key code for a registered keybinding.
     * 
     * @param identifier The unique identifier for the keybinding
     * @return The LWJGL Keyboard key code, or -1 if not found
     */
    public static int getKeyCode(String identifier) {
        return KEY_BINDINGS.getOrDefault(identifier, -1);
    }
    
    /**
     * Retrieves the description for a registered keybinding.
     * 
     * @param identifier The unique identifier for the keybinding
     * @return The description string, or empty string if not found
     */
    public static String getDescription(String identifier) {
        return KEY_DESCRIPTIONS.getOrDefault(identifier, "");
    }
    
    /**
     * Checks if a specific keybinding is currently pressed.
     * 
     * @param identifier The unique identifier for the keybinding
     * @return true if the keybinding is pressed, false otherwise
     */
    public static boolean isKeyPressed(String identifier) {
        int keyCode = getKeyCode(identifier);
        if (keyCode != -1) {
            return Keyboard.isKeyDown(keyCode);
        }
        return false;
    }
    
    /**
     * Checks if a keybinding was just pressed (one-time trigger).
     * 
     * @param identifier The unique identifier for the keybinding
     * @return true if the keybinding was just pressed, false otherwise
     */
    public static boolean isKeyJustPressed(String identifier) {
        int keyCode = getKeyCode(identifier);
        if (keyCode != -1) {
            return Keyboard.getEventKey() == keyCode && Keyboard.getEventKeyState();
        }
        return false;
    }
    
    /**
     * Checks if a keybinding is registered.
     * 
     * @param identifier The unique identifier for the keybinding
     * @return true if the keybinding exists, false otherwise
     */
    public static boolean isKeyBindingRegistered(String identifier) {
        return KEY_BINDINGS.containsKey(identifier);
    }
    
    /**
     * Removes a keybinding from the registry.
     * 
     * @param identifier The unique identifier for the keybinding
     * @return true if the keybinding was removed, false if not found
     */
    public static boolean removeKeyBinding(String identifier) {
        if (KEY_BINDINGS.containsKey(identifier)) {
            KEY_BINDINGS.remove(identifier);
            KEY_DESCRIPTIONS.remove(identifier);
            return true;
        }
        return false;
    }
    
    /**
     * Resets all keybindings to their default values.
     */
    public static void resetToDefaults() {
        KEY_BINDINGS.clear();
        KEY_DESCRIPTIONS.clear();
        
        registerKeyBinding("toggle_fly", DEFAULT_TOGGLE_FLY, "Toggle Flight Mode");
        registerKeyBinding("toggle_speed", DEFAULT_TOGGLE_SPEED, "Toggle Speed Boost");
        registerKeyBinding("toggle_noclip", DEFAULT_TOGGLE_NOCLIP, "Toggle No-Clip Mode");
        registerKeyBinding("toggle_godmode", DEFAULT_TOGGLE_GODMODE, "Toggle God Mode");
        registerKeyBinding("open_menu", DEFAULT_OPEN_MENU, "Open Cheat Menu");
    }
    
    /**
     * Returns a map of all registered keybindings and their key codes.
     * 
     * @return A copy of the keybindings map
     */
    public static Map<String, Integer> getAllKeyBindings() {
        return new HashMap<>(KEY_BINDINGS);
    }
    
    /**
     * Returns a map of all registered keybindings and their descriptions.
     * 
     * @return A copy of the descriptions map
     */
    public static Map<String, String> getAllDescriptions() {
        return new HashMap<>(KEY_DESCRIPTIONS);
    }
    
    /**
     * Converts a key code to a human-readable key name.
     * 
     * @param keyCode The LWJGL Keyboard key code
     * @return A string representation of the key name
     */
    public static String getKeyName(int keyCode) {
        return Keyboard.getKeyName(keyCode);
    }
    
    /**
     * Prints all registered keybindings to the console.
     */
    public static void printAllBindings() {
        System.out.println("=== Registered Keybindings ===");
        for (Map.Entry<String, Integer> entry : KEY_BINDINGS.entrySet()) {
            String keyName = getKeyName(entry.getValue());
            String description = KEY_DESCRIPTIONS.get(entry.getKey());
            System.out.println(entry.getKey() + ": " + keyName + " (" + description + ")");
        }
    }
}
