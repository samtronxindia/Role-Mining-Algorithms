package rolemining.core;

import java.util.LinkedHashMap;
import java.util.Map;

/** Algorithm parameters given as name=value pairs (for example delta=350). */
public final class MinerOptions {
    private final Map<String, String> values = new LinkedHashMap<>();

    public static MinerOptions empty() { return new MinerOptions(); }

    public MinerOptions set(String name, Object value) {
        values.put(name, String.valueOf(value));
        return this;
    }

    public boolean has(String name) { return values.containsKey(name); }

    public String get(String name, String fallback) { return values.getOrDefault(name, fallback); }

    public int getInt(String name, int fallback) {
        String v = values.get(name);
        if (v == null) return fallback;
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("parameter " + name + " must be an integer, got '" + v + "'");
        }
    }

    public double getDouble(String name, double fallback) {
        String v = values.get(name);
        if (v == null) return fallback;
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("parameter " + name + " must be a number, got '" + v + "'");
        }
    }

    public Map<String, String> asMap() { return Map.copyOf(values); }
}
