package dev.mindle.team.command.impl.module;

import dev.mindle.team.Team;
import dev.mindle.team.command.Command;
import dev.mindle.team.command.CommandManager;
import dev.mindle.team.module.keybind.Keybind;
import dev.mindle.team.module.keybind.KeybindManager;
import dev.mindle.team.module.keybind.KeybindType;
import dev.mindle.team.util.ChatUtil;
import dev.mindle.team.util.KeybindUtil;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeybindCommand extends Command {
    public KeybindCommand() {
        super("keybind", "Manage keybinds", CommandManager.PREFIX + "keybind <list|set|reset|clear> [name] [key]", "bind", "key");
    }

    @Override
    public void execute(String[] args) {
        validateArgs(args, 1);

        KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
        if (keybindManager == null) {
            ChatUtil.sendMessage("§cKeybind system not available");
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "list":
                listKeybinds();
                break;
            case "set":
                validateArgs(args, 3);
                setKeybind(args[1], args[2]);
                break;
            case "reset":
                if (args.length > 1) {
                    resetKeybind(args[1]);
                } else {
                    resetAllKeybinds();
                }
                break;
            case "clear":
                validateArgs(args, 2);
                clearKeybind(args[1]);
                break;
            default:
                ChatUtil.sendMessage("§cInvalid action: §f" + action);
                ChatUtil.sendMessage("§7Usage: §f" + getUsage());
        }
    }

    private void listKeybinds() {
        KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
        
        ChatUtil.sendMessage("§bKeybind List:");
        ChatUtil.sendMessage("§7Total: §f" + keybindManager.getTotalKeybinds() + 
                             " §7| Toggle: §f" + keybindManager.getKeybindCount(KeybindType.TOGGLE) + 
                             " §7| Hold: §f" + keybindManager.getKeybindCount(KeybindType.HOLD));
        ChatUtil.sendMessage("§7─────────────────────────");

        // Group by type
        for (KeybindType type : KeybindType.values()) {
            List<Keybind> keybinds = keybindManager.getKeybindsByType(type);
            if (!keybinds.isEmpty()) {
                ChatUtil.sendMessage("§e" + type.getName() + " Keybinds:");
                for (Keybind keybind : keybinds) {
                    String keyName = keybind.getKeyName();
                    String defaultMarker = keybind.isDefault() ? "" : " §7(modified)";
                    ChatUtil.sendMessage("  §f" + keybind.getName() + " §7-> §a" + keyName + defaultMarker);
                }
            }
        }
    }

    private void setKeybind(String name, String keyName) {
        KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
        Keybind keybind = keybindManager.getKeybind(name);
        
        if (keybind == null) {
            ChatUtil.sendMessage("§cKeybind not found: §f" + name);
            return;
        }

        int key = parseKey(keyName);
        if (key == GLFW.GLFW_KEY_UNKNOWN) {
            ChatUtil.sendMessage("§cInvalid key: §f" + keyName);
            ChatUtil.sendMessage("§7Examples: §fR, SPACE, LSHIFT, etc.");
            return;
        }

        // Check for conflicts
        List<Keybind> conflicts = keybindManager.getKeybindsForKey(key);
        if (!conflicts.isEmpty() && !conflicts.contains(keybind)) {
            ChatUtil.sendMessage("§6Warning: Key §f" + KeybindUtil.getKeyName(key) + " §6is already used by:");
            for (Keybind conflict : conflicts) {
                ChatUtil.sendMessage("  §f" + conflict.getName());
            }
        }

        keybind.setKey(key);
        keybindManager.saveKeybinds();
        
        ChatUtil.sendMessage("§7Set keybind §f" + name + " §7to §a" + KeybindUtil.getKeyName(key));
    }

    private void resetKeybind(String name) {
        KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
        Keybind keybind = keybindManager.getKeybind(name);
        
        if (keybind == null) {
            ChatUtil.sendMessage("§cKeybind not found: §f" + name);
            return;
        }

        keybind.resetToDefault();
        keybindManager.saveKeybinds();
        
        ChatUtil.sendMessage("§7Reset keybind §f" + name + " §7to default: §a" + keybind.getKeyName());
    }

    private void resetAllKeybinds() {
        KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
        keybindManager.resetAllKeybinds();
        
        ChatUtil.sendMessage("§aReset all keybinds to defaults");
    }

    private void clearKeybind(String name) {
        KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
        Keybind keybind = keybindManager.getKeybind(name);
        
        if (keybind == null) {
            ChatUtil.sendMessage("§cKeybind not found: §f" + name);
            return;
        }

        keybind.setKey(GLFW.GLFW_KEY_UNKNOWN);
        keybindManager.saveKeybinds();
        
        ChatUtil.sendMessage("§7Cleared keybind for §f" + name);
    }

    private int parseKey(String keyName) {
        String upper = keyName.toUpperCase();
        
        // Try common key mappings
        switch (upper) {
            case "SPACE": return GLFW.GLFW_KEY_SPACE;
            case "ENTER": return GLFW.GLFW_KEY_ENTER;
            case "TAB": return GLFW.GLFW_KEY_TAB;
            case "LSHIFT": return GLFW.GLFW_KEY_LEFT_SHIFT;
            case "RSHIFT": return GLFW.GLFW_KEY_RIGHT_SHIFT;
            case "LCTRL": return GLFW.GLFW_KEY_LEFT_CONTROL;
            case "RCTRL": return GLFW.GLFW_KEY_RIGHT_CONTROL;
            case "LALT": return GLFW.GLFW_KEY_LEFT_ALT;
            case "RALT": return GLFW.GLFW_KEY_RIGHT_ALT;
            case "ESC": case "ESCAPE": return GLFW.GLFW_KEY_ESCAPE;
            case "DELETE": return GLFW.GLFW_KEY_DELETE;
            case "BACKSPACE": return GLFW.GLFW_KEY_BACKSPACE;
            case "HOME": return GLFW.GLFW_KEY_HOME;
            case "END": return GLFW.GLFW_KEY_END;
            case "PAGEUP": return GLFW.GLFW_KEY_PAGE_UP;
            case "PAGEDOWN": return GLFW.GLFW_KEY_PAGE_DOWN;
            case "UP": return GLFW.GLFW_KEY_UP;
            case "DOWN": return GLFW.GLFW_KEY_DOWN;
            case "LEFT": return GLFW.GLFW_KEY_LEFT;
            case "RIGHT": return GLFW.GLFW_KEY_RIGHT;
        }
        
        // Try single characters
        if (upper.length() == 1) {
            char c = upper.charAt(0);
            if (c >= 'A' && c <= 'Z') {
                return GLFW.GLFW_KEY_A + (c - 'A');
            } else if (c >= '0' && c <= '9') {
                return GLFW.GLFW_KEY_0 + (c - '0');
            }
        }
        
        // Try F keys
        if (upper.startsWith("F") && upper.length() <= 3) {
            try {
                int num = Integer.parseInt(upper.substring(1));
                if (num >= 1 && num <= 25) {
                    return GLFW.GLFW_KEY_F1 + (num - 1);
                }
            } catch (NumberFormatException ignored) {}
        }
        
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            for (String action : new String[]{"list", "set", "reset", "clear"}) {
                if (action.startsWith(partial)) {
                    suggestions.add(action);
                }
            }
        } else if (args.length == 2 && !args[0].equalsIgnoreCase("list")) {
            KeybindManager keybindManager = Team.getInstance().getModuleManager().getKeybindManager();
            if (keybindManager != null) {
                String partial = args[1].toLowerCase();
                for (Keybind keybind : keybindManager.getAllKeybinds()) {
                    if (keybind.getName().toLowerCase().startsWith(partial)) {
                        suggestions.add(keybind.getName());
                    }
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            String partial = args[2].toUpperCase();
            for (String key : new String[]{"R", "T", "Y", "F", "G", "H", "V", "B", "N", "SPACE", "LSHIFT", "LCTRL", "LALT"}) {
                if (key.startsWith(partial)) {
                    suggestions.add(key);
                }
            }
        }

        return suggestions;
    }
}