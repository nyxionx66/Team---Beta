package dev.mindle.team.module.setting;

import dev.mindle.team.Team;

public abstract class Setting<T> {
    protected final String name;
    protected final String description;
    protected T value;
    protected final T defaultValue;
    protected final String module;

    public Setting(String module, String name, String description, T defaultValue) {
        this.module = module;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public abstract void setValue(T value);
    public abstract T getValue();
    public abstract String getValueAsString();
    public abstract void setValueFromString(String value);
    public abstract Setting<T> copy();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getModule() {
        return module;
    }

    public void resetToDefault() {
        setValue(defaultValue);
    }

    public boolean isDefault() {
        return value != null && value.equals(defaultValue);
    }

    protected String getConfigKey() {
        return "module." + module.toLowerCase() + "." + name.toLowerCase();
    }

    public void save() {
        try {
            String configKey = getConfigKey();
            String valueStr = getValueAsString();
            Team.getInstance().getConfig().setString(configKey, valueStr);
        } catch (Exception e) {
            Team.LOGGER.error("Failed to save setting {}.{}", module, name, e);
        }
    }

    public void load() {
        try {
            String configKey = getConfigKey();
            if (Team.getInstance().getConfig().hasKey(configKey)) {
                String valueStr = Team.getInstance().getConfig().getString(configKey);
                if (valueStr != null) {
                    setValueFromString(valueStr);
                }
            }
        } catch (Exception e) {
            Team.LOGGER.error("Failed to load setting {}.{}", module, name, e);
            resetToDefault();
        }
    }
}