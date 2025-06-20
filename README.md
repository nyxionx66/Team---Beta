# Team - Minecraft Client Mod

A comprehensive Minecraft client modification built for Fabric, providing enhanced gameplay features and utilities.

## 🚀 Features

### Core Systems
- **Module System**: Modular architecture for easy feature management
- **Command System**: Comprehensive command interface with auto-discovery
- **Keybind System**: Smart keybind management with screen awareness
- **Event System**: Robust event handling for mod interactions
- **Configuration**: Persistent settings and preferences

### Combat Modules
- **KillAura**: Automatic targeting and attacking of nearby entities
  - Player targeting with configurable settings
  - Mob targeting with hostile entity detection
  - Animal targeting (optional)
  - Configurable range and attack delay
  - Smart target selection

### Movement Modules
- **Fly**: Enhanced flight capabilities
  - Configurable flight speed
  - Anti-kick protection for servers
  - Smooth flight controls
  
- **Speed**: Movement speed enhancement
  - Configurable speed multiplier
  - Safe speed adjustments
  - Server-friendly options

### Player Modules
- **AutoSprint**: Automatic sprinting system
  - Keep sprint while attacking
  - Multi-directional sprinting
  - Hunger level awareness
  - Smart sprint conditions

### Miscellaneous Modules
- **AutoClicker**: Automated clicking functionality
  - Configurable clicks per second (CPS)
  - Left and right click support
  - Hold and toggle modes
  - Safe clicking patterns

### Render Modules
- **Fullbright**: Enhanced visibility system
  - Adjustable brightness levels
  - Smooth brightness transitions
  - No lighting calculations bypass

## 🎮 Commands

### Information Commands
- `.help` - Shows all available commands (auto-discovering)
- `.help <command>` - Shows detailed help for specific command
- `.info` - Displays mod information and statistics

### Configuration Commands
- `.config` - Configuration management system
- `.config <key> <value>` - Set configuration values
- `.config list` - List all configuration options

### Utility Commands
- `.toggle <module>` - Toggle module on/off
- `.reload` - Reload mod configuration
- `.debug` - Debug information and diagnostics
- `.test` - Test command for verification

### Module Commands
- `.module <name>` - Module-specific commands
- `.keybind <module> <key>` - Set module keybinds

## 🔧 Recent Improvements

### ✅ Enhanced Help Command (Latest)
- **Auto-Discovery**: Automatically discovers and displays all registered commands
- **Dynamic Categories**: Shows all command categories with color coding
- **Scalable System**: New commands automatically appear without manual updates
- **Statistics Display**: Shows total commands and category breakdown
- **Professional Formatting**: Clean, organized display with color coding

### ✅ Screen-Aware Keybind System (Latest)
- **Smart Detection**: Uses mixins to detect screen opening/closing
- **Complete Blocking**: Keybinds disabled when any screen is open
- **Screen Types Supported**:
  - Chat screen
  - Inventory/container screens
  - Pause menu
  - Any GUI/screen
- **State Management**: Proper keybind state reset when screens open/close
- **Debug Logging**: Comprehensive logging for troubleshooting

### ✅ Previous Fixes
- **Java Compilation Issues**: Fixed 17 compilation errors
- **Type Safety**: Added proper type casting for setting classes
- **API Compatibility**: Updated to use correct Minecraft Fabric API methods
- **Generic Type Issues**: Resolved Setting<T> compatibility problems

## 🛠️ Technical Architecture

### Module System
```java
public abstract class Module {
    // Core module functionality
    protected abstract void initializeSettings();
    protected abstract void onEnable();
    protected abstract void onDisable();
}
```

### Command System
```java
public abstract class Command {
    // Auto-discoverable command system
    public abstract void execute(String[] args);
    public List<String> getSuggestions(String[] args);
}
```

### Screen Detection System
```java
public class ScreenManager {
    // Mixin-based screen detection
    public static boolean isScreenOpen();
    public static Screen getCurrentScreen();
}
```

### Keybind System
```java
public class Keybind {
    // Screen-aware keybind processing
    public void update() {
        if (!KeybindUtil.shouldProcessKeybinds()) return;
        // Process keybind...
    }
}
```

## 📁 Project Structure

```
/app/
├── src/main/java/dev/mindle/team/
│   ├── command/               # Command system
│   │   ├── impl/             # Command implementations
│   │   │   ├── info/         # Information commands
│   │   │   ├── config/       # Configuration commands
│   │   │   ├── util/         # Utility commands
│   │   │   └── module/       # Module commands
│   │   ├── Command.java      # Base command class
│   │   └── CommandManager.java # Command registration/execution
│   ├── module/               # Module system
│   │   ├── impl/            # Module implementations
│   │   │   ├── combat/      # Combat modules
│   │   │   ├── movement/    # Movement modules
│   │   │   ├── player/      # Player modules
│   │   │   ├── misc/        # Miscellaneous modules
│   │   │   └── render/      # Render modules
│   │   ├── keybind/         # Keybind system
│   │   ├── setting/         # Settings system
│   │   └── Module.java      # Base module class
│   ├── screen/              # Screen detection system
│   │   └── ScreenManager.java # Screen state management
│   ├── mixin/               # Mixin integrations
│   │   ├── client/          # Client-side mixins
│   │   └── input/           # Input handling mixins
│   ├── event/               # Event system
│   ├── config/              # Configuration system
│   ├── util/                # Utility classes
│   └── Team.java            # Main mod class
├── src/main/resources/
│   ├── team.mixins.json     # Mixin configuration
│   └── fabric.mod.json      # Fabric mod metadata
├── build.gradle             # Build configuration
└── README.md               # This file
```

## 🔨 Development Status

### ✅ Completed Features
- [x] Core module system with proper initialization
- [x] Command system with auto-discovery
- [x] Keybind system with screen awareness
- [x] Event system for mod communication
- [x] Configuration system with persistence
- [x] All basic combat modules (KillAura)
- [x] All basic movement modules (Fly, Speed)
- [x] All basic player modules (AutoSprint)
- [x] All basic utility modules (AutoClicker)
- [x] All basic render modules (Fullbright)
- [x] Mixin integration for screen detection
- [x] Java compilation issues resolved
- [x] Type safety improvements
- [x] API compatibility updates

### 🚧 In Progress
- [ ] Advanced GUI system
- [ ] Plugin/addon system
- [ ] Advanced configuration UI
- [ ] Performance optimizations

### 📋 Planned Features
- [ ] World interaction modules
- [ ] Advanced combat features
- [ ] Social features (friends, etc.)
- [ ] Advanced rendering features
- [ ] Server-specific optimizations

## 🧪 Testing

### Manual Testing Checklist
- [ ] Help command shows all commands automatically
- [ ] New commands appear in help without code changes
- [ ] Keybinds don't work when chat is open (T key)
- [ ] Keybinds don't work when inventory is open (E key)
- [ ] Keybinds don't work when pause menu is open (ESC key)
- [ ] Keybinds work normally when no screens are open
- [ ] All modules can be toggled via keybinds
- [ ] All modules save their enabled state
- [ ] Configuration persists between sessions

### Automated Testing
```bash
# Compile the project
./gradlew compileJava

# Run fabric client (when available)
./gradlew runClient
```

## 📝 Contributing

1. Follow the existing code structure
2. Use proper Java naming conventions
3. Add appropriate logging for debugging
4. Test all changes thoroughly
5. Update documentation as needed

## 🔧 Build Requirements

- Java 21+
- Gradle 8.0+
- Minecraft 1.21
- Fabric Loader 0.15+
- Fabric API

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Fabric team for the modding framework
- Minecraft modding community
- Contributors and testers

---

**Latest Update**: Enhanced help command with auto-discovery and screen-aware keybind system with mixin-based detection.