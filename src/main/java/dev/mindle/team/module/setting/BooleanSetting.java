package dev.mindle.team.module.setting;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String module, String name, String description, Boolean defaultValue) {
        super(module, name, description, defaultValue);
    }

    @Override
    public void setValue(Boolean value) {
        if (value != null) {
            this.value = value;
            save();
        }
    }

    @Override
    public Boolean getValue() {
        return value != null ? value : defaultValue;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @Override
    public void setValueFromString(String value) {
        if (value != null) {
            setValue(Boolean.parseBoolean(value));
        }
    }

    @Override
    public Setting<Boolean> copy() {
        BooleanSetting copy = new BooleanSetting(module, name, description, defaultValue);
        copy.value = this.value;
        return copy;
    }

    public void toggle() {
        setValue(!getValue());
    }
}