package rolemining.miners.rmp;

import java.util.Set;
import java.util.TreeSet;
import rolemining.core.MinerOptions;

/** Option parsing that {@link MinerOptions} does not provide itself. */
public final class Params {
    private Params() {}

    public static boolean getBoolean(MinerOptions options, String name, boolean fallback) {
        if (!options.has(name)) return fallback;
        String v = options.get(name, "").strip().toLowerCase(java.util.Locale.ROOT);
        return switch (v) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new IllegalArgumentException("parameter " + name + " must be true or false, got '" + v + "'");
        };
    }

    /** A comma-separated list of non-negative integers; empty or "none" gives the empty set. */
    public static Set<Integer> getIntSet(MinerOptions options, String name, String fallback) {
        String v = options.get(name, fallback).strip();
        Set<Integer> out = new TreeSet<>();
        if (v.isEmpty() || v.equalsIgnoreCase("none")) return out;
        for (String t : v.split(",")) {
            String s = t.strip();
            if (s.isEmpty()) continue;
            try {
                int n = Integer.parseInt(s);
                if (n < 0) throw new NumberFormatException();
                out.add(n);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("parameter " + name
                        + " must be a comma-separated list of non-negative integers, got '" + v + "'");
            }
        }
        return out;
    }

    public static int getNonNegativeInt(MinerOptions options, String name, int fallback) {
        int v = options.getInt(name, fallback);
        if (v < 0) throw new IllegalArgumentException("parameter " + name + " must be >= 0, got " + v);
        return v;
    }
}
