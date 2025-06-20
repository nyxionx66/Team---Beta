# Screen-Aware Keybind System - Technical Implementation

## Problem Solved
The original keybind system was triggering module toggles even when screens (chat, inventory, pause menu) were open, causing unwanted behavior.

## Solution Implemented

### 1. ScreenManager - Central Screen State Management
**File**: `/app/src/main/java/dev/mindle/team/screen/ScreenManager.java`

```java
public class ScreenManager {
    private static volatile boolean screenOpen = false;
    private static Screen currentScreen = null;
    
    public static void setScreenOpen(Screen screen) {
        screenOpen = screen != null;
        currentScreen = screen;
        // Logging for debug purposes
    }
    
    public static boolean isScreenOpen() {
        return screenOpen;
    }
    
    // Specific screen type detection
    public static boolean isChatOpen() { ... }
    public static boolean isInventoryOpen() { ... }
    public static boolean isPauseMenuOpen() { ... }
}
```

### 2. ScreenMixin - Mixin-Based Screen Detection
**File**: `/app/src/main/java/dev/mindle/team/mixin/client/ScreenMixin.java`

```java
@Mixin(MinecraftClient.class)
public class ScreenMixin {
    
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        ScreenManager.setScreenOpen(screen);
    }
}
```

**Registration**: Updated `/app/src/main/resources/team.mixins.json`
```json
{
    "client": [
        "input.KeyboardMixin",
        "input.MouseMixin", 
        "client.MinecraftClientMixin",
        "client.ChatScreenMixin",
        "client.ScreenMixin"
    ]
}
```

### 3. Enhanced KeybindUtil - Screen-Aware Detection
**File**: `/app/src/main/java/dev/mindle/team/util/KeybindUtil.java`

```java
public static boolean shouldProcessKeybinds() {
    // Use our ScreenManager for more reliable screen detection
    return !ScreenManager.isScreenOpen();
}

public static boolean isInGame() {
    MinecraftClient mc = MinecraftClient.getInstance();
    return mc.player != null && mc.world != null && !ScreenManager.isScreenOpen();
}

// Additional utility methods
public static boolean isChatOpen() { return ScreenManager.isChatOpen(); }
public static boolean isInventoryOpen() { return ScreenManager.isInventoryOpen(); }
public static boolean isPauseMenuOpen() { return ScreenManager.isPauseMenuOpen(); }
```

### 4. Updated Keybind Processing - Enhanced Logic
**File**: `/app/src/main/java/dev/mindle/team/module/keybind/Keybind.java`

```java
public void update() {
    this.lastPressed = this.pressed;
    this.pressed = KeybindUtil.isKeyPressed(key);

    // Check if we should process keybinds (no screen open)
    if (!KeybindUtil.shouldProcessKeybinds()) {
        // Debug logging when keybinds are blocked
        if (this.pressed) {
            Team.LOGGER.debug("Keybind {} blocked due to open screen: {}", 
                             name, KeybindUtil.getCurrentScreenType());
        }
        this.pressed = false; // Reset state to prevent delayed activation
        return;
    }
    
    // Process keybind normally...
}
```

### 5. KeybindManager Updates - Manager-Level Blocking
**File**: `/app/src/main/java/dev/mindle/team/module/keybind/KeybindManager.java`

```java
@Subscribe
public void onUpdate(UpdateEvent.Pre event) {
    // Only process keybinds if no screen is open
    if (!KeybindUtil.shouldProcessKeybinds()) {
        // Optional debug logging (rate-limited)
        return;
    }
    
    // Update all keybinds normally
    for (Keybind keybind : updateList) {
        keybind.update();
    }
}
```

## How It Works

### Detection Flow
1. **Screen Opening**: When any screen opens, `MinecraftClient.setScreen()` is called
2. **Mixin Intercept**: Our `ScreenMixin` intercepts this call via `@Inject(method = "setScreen")`
3. **State Update**: `ScreenManager.setScreenOpen(screen)` updates the global screen state
4. **Keybind Check**: All keybind processing checks `KeybindUtil.shouldProcessKeybinds()`
5. **Blocking**: If screen is open, keybinds are completely blocked

### Screen Types Detected
- **Chat Screen**: When T key opens chat
- **Inventory Screen**: When E key opens inventory  
- **Pause Menu**: When ESC opens pause menu
- **Any GUI**: Any custom screen/interface
- **Container Screens**: Chests, furnaces, etc.

### State Management
- **Immediate Detection**: Screen state updates immediately when screen opens/closes
- **State Reset**: Keybind pressed state is reset when screen opens to prevent delayed activation
- **Thread Safe**: Uses `volatile` for thread-safe screen state access
- **Debug Logging**: Comprehensive logging for troubleshooting

## Testing Commands

### Status Command
Use `.status screen` to check screen detection:
```
Screen Detection Status:
- Screen Open: No/Yes
- Current Screen: none/ChatScreen/InventoryScreen
- Chat Open: No/Yes
- Inventory Open: No/Yes 
- Pause Menu Open: No/Yes
```

### Test Command  
Use `.test` to check keybind status:
```
Screen Status:
- Screen Open: No/Yes
- Current Screen: none/ChatScreen
- Keybinds Allowed: Yes/No
```

## Verification Steps

1. **Test Chat**: Open chat (T key) → Module keybinds should NOT work
2. **Test Inventory**: Open inventory (E key) → Module keybinds should NOT work
3. **Test Pause**: Open pause menu (ESC key) → Module keybinds should NOT work
4. **Test Normal**: Close all screens → Module keybinds should work normally
5. **Check Logs**: Debug logs show when keybinds are blocked and why

## Benefits

1. **Reliable Detection**: Mixin-based detection is more reliable than polling
2. **Immediate Response**: State updates immediately when screens open/close
3. **Comprehensive Coverage**: Detects ALL screen types, not just specific ones
4. **Thread Safe**: Proper synchronization for multi-threaded access
5. **Debug Friendly**: Extensive logging for troubleshooting
6. **Performance**: Minimal overhead with early returns when screens are open