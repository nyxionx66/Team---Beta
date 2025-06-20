package dev.mindle.team.module.setting;

public class NumberSetting extends Setting<Double> {
    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(String module, String name, String description, Double defaultValue, double min, double max, double increment) {
        super(module, name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public void setValue(Double value) {
        if (value != null) {
            this.value = Math.max(min, Math.min(max, value));
            save();
        }
    }

    @Override
    public Double getValue() {
        return value != null ? value : defaultValue;
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(getValue());
    }

    @Override
    public void setValueFromString(String value) {
        if (value != null) {
            try {
                setValue(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                // Invalid number, reset to default
                resetToDefault();
            }
        }
    }

    @Override
    public Setting<Double> copy() {
        NumberSetting copy = new NumberSetting(module, name, description, defaultValue, min, max, increment);
        copy.value = this.value;
        return copy;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    public int getIntValue() {
        return getValue().intValue();
    }

    public float getFloatValue() {
        return getValue().floatValue();
    }

    public void increment() {
        setValue(getValue() + increment);
    }

    public void decrement() {
        setValue(getValue() - increment);
    }

    public double getPercentage() {
        return (getValue() - min) / (max - min);
    }

    public void setFromPercentage(double percentage) {
        setValue(min + (max - min) * Math.max(0, Math.min(1, percentage)));
    }
}