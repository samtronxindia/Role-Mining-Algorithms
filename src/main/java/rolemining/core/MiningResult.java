package rolemining.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Output of a miner: the roles (PA), optionally the user assignment (UA),
 * and any algorithm-specific numbers worth reporting.
 */
public final class MiningResult {
    private final List<BitSet> roles;
    private final List<BitSet> assignments;
    private final Map<String, Object> stats = new LinkedHashMap<>();

    /** Roles only. The user assignment is derived: see {@link Metrics#assignments}. */
    public MiningResult(List<BitSet> roles) { this(roles, null); }

    /**
     * @param roles       role i is the set of permission indices it grants
     * @param assignments assignments.get(i) is the set of user indices holding role i, or null
     */
    public MiningResult(List<BitSet> roles, List<BitSet> assignments) {
        if (assignments != null && assignments.size() != roles.size()) {
            throw new IllegalArgumentException("need one assignment set per role");
        }
        this.roles = copy(roles);
        this.assignments = assignments == null ? null : copy(assignments);
    }

    /** A fresh copy on every call; changing it does not change this result. */
    public List<BitSet> roles() { return copy(roles); }

    /** May be null when the miner produces roles only. */
    public List<BitSet> assignments() { return assignments == null ? null : copy(assignments); }

    /** Algorithm-specific numbers, printed with the metrics. Insertion ordered. */
    public Map<String, Object> stats() { return stats; }

    public MiningResult stat(String name, Object value) {
        stats.put(name, value);
        return this;
    }

    private static List<BitSet> copy(List<BitSet> in) {
        List<BitSet> out = new ArrayList<>(in.size());
        for (BitSet b : in) out.add((BitSet) b.clone());
        return List.copyOf(out);
    }
}
