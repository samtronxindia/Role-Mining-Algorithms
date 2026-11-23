package rolemining.miners.enumeration;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import rolemining.core.MinerOptions;

/**
 * Parameters shared by CompleteMiner and FastMiner.
 *
 * <ul>
 * <li>{@code legacy} (default false): reproduce the original program exactly,
 *     including its defects (see NOTES-enum.md): the all-zero role seeded into the
 *     generated roles is kept, the merge of initial and generated roles skips the
 *     element after each merge (so some roles appear twice), and CompleteMiner's
 *     priorities are shifted by one step.</li>
 * <li>{@code minPriority} (10), {@code priorityStep} (10), {@code maxPriority} (100):
 *     the priorities p at which each candidate's rating
 *     {@code originalCount * p + count} is computed: min, min+step, ..., up to max.
 *     The legacy programs were run with maxPriority=100.</li>
 * <li>{@code order} (legacy): {@code legacy} returns candidates in the order the
 *     original program built them; {@code rating} sorts them by rating at
 *     {@code rankPriority}, highest first, ties kept in legacy order.</li>
 * <li>{@code rankPriority} (= maxPriority): the priority used by {@code order=rating}.</li>
 * <li>{@code maxGeneratedRoles} (1000000, CompleteMiner only): stop with an error
 *     instead of enumerating more generated roles than this. 0 means no limit.</li>
 * </ul>
 */
public final class Settings {
    public static final String LEGACY = "legacy";
    public static final String MIN_PRIORITY = "minPriority";
    public static final String PRIORITY_STEP = "priorityStep";
    public static final String MAX_PRIORITY = "maxPriority";
    public static final String ORDER = "order";
    public static final String RANK_PRIORITY = "rankPriority";
    public static final String MAX_GENERATED = "maxGeneratedRoles";

    public static final int DEFAULT_MIN_PRIORITY = 10;
    public static final int DEFAULT_PRIORITY_STEP = 10;
    /** The value the legacy programs passed to calcRating. */
    public static final int DEFAULT_MAX_PRIORITY = 100;
    public static final int DEFAULT_MAX_GENERATED = 1_000_000;

    /** How candidates are ordered. */
    public enum Order { LEGACY, RATING }

    private final boolean legacy;
    private final int[] priorities;
    private final Order order;
    private final int rankPriority;
    private final int maxGenerated;

    private Settings(boolean legacy, int[] priorities, Order order, int rankPriority, int maxGenerated) {
        this.legacy = legacy;
        this.priorities = priorities;
        this.order = order;
        this.rankPriority = rankPriority;
        this.maxGenerated = maxGenerated;
    }

    /**
     * @param legacyDefault value of {@code legacy} when the option is absent
     * @param shiftedPriorities true for CompleteMiner, whose legacy loop incremented
     *        the priority before using it; only applies when legacy is on
     */
    public static Settings parse(MinerOptions options, boolean legacyDefault, boolean shiftedPriorities) {
        boolean legacy = parseBoolean(options.get(LEGACY, String.valueOf(legacyDefault)), LEGACY);
        int min = options.getInt(MIN_PRIORITY, DEFAULT_MIN_PRIORITY);
        int step = options.getInt(PRIORITY_STEP, DEFAULT_PRIORITY_STEP);
        int max = options.getInt(MAX_PRIORITY, DEFAULT_MAX_PRIORITY);
        if (step <= 0) throw new IllegalArgumentException(PRIORITY_STEP + " must be positive, got " + step);
        if (min > max) throw new IllegalArgumentException(MIN_PRIORITY + " (" + min + ") exceeds " + MAX_PRIORITY + " (" + max + ")");
        if (min < 0) throw new IllegalArgumentException(MIN_PRIORITY + " must not be negative, got " + min);
        int shift = legacy && shiftedPriorities ? step : 0;
        int n = (max - min) / step + 1;
        int[] priorities = new int[n];
        for (int i = 0; i < n; i++) priorities[i] = min + i * step + shift;
        String o = options.get(ORDER, "legacy").trim().toLowerCase(Locale.ROOT);
        Order order = switch (o) {
            case "legacy" -> Order.LEGACY;
            case "rating" -> Order.RATING;
            default -> throw new IllegalArgumentException(ORDER + " must be legacy or rating, got '" + o + "'");
        };
        int rank = options.getInt(RANK_PRIORITY, max);
        int cap = options.getInt(MAX_GENERATED, DEFAULT_MAX_GENERATED);
        if (cap < 0) throw new IllegalArgumentException(MAX_GENERATED + " must not be negative, got " + cap);
        return new Settings(legacy, priorities, order, rank, cap);
    }

    /** Rejects option names this miner does not know, so that typos are not silently ignored. */
    public static void checkNames(MinerOptions options, boolean completeMiner) {
        Set<String> known = new TreeSet<>(Set.of(LEGACY, MIN_PRIORITY, PRIORITY_STEP, MAX_PRIORITY, ORDER, RANK_PRIORITY));
        if (completeMiner) known.add(MAX_GENERATED);
        for (String name : options.asMap().keySet()) {
            if (!known.contains(name)) {
                throw new IllegalArgumentException("unknown parameter '" + name + "'; known: " + known);
            }
        }
    }

    private static boolean parseBoolean(String v, String name) {
        String t = v.trim().toLowerCase(Locale.ROOT);
        if (t.equals("true")) return true;
        if (t.equals("false")) return false;
        throw new IllegalArgumentException("parameter " + name + " must be true or false, got '" + v + "'");
    }

    public boolean legacy() { return legacy; }

    /** The priorities at which ratings are computed, in increasing order. */
    public int[] priorities() { return priorities.clone(); }

    public Order order() { return order; }

    public int rankPriority() { return rankPriority; }

    /** 0 means unlimited. */
    public int maxGenerated() { return maxGenerated; }
}
