package rolemining.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Measures common to all miners, so their results can be compared. */
public final class Metrics {
    private Metrics() {}

    /**
     * The result's user assignment, or, when the miner gave none, the exact one:
     * a user holds every role whose permissions are all in the user's row.
     */
    public static List<BitSet> assignments(UpaMatrix upa, MiningResult result) {
        List<BitSet> roles = result.roles();
        List<BitSet> given = result.assignments();
        for (int r = 0; r < roles.size(); r++) {
            if (roles.get(r).length() > upa.permCount()) {
                throw new IllegalArgumentException("role " + (r + 1) + " uses permission index "
                        + (roles.get(r).length() - 1) + ", but the matrix has " + upa.permCount() + " permissions");
            }
            if (given != null && given.get(r).length() > upa.userCount()) {
                throw new IllegalArgumentException("role " + (r + 1) + " is assigned to user index "
                        + (given.get(r).length() - 1) + ", but the matrix has " + upa.userCount() + " users");
            }
        }
        if (given != null) return given;
        List<BitSet> ua = new ArrayList<>();
        for (BitSet role : roles) {
            BitSet users = new BitSet(upa.userCount());
            if (!role.isEmpty()) {
                for (int u = 0; u < upa.userCount(); u++) {
                    BitSet missing = (BitSet) role.clone();
                    missing.andNot(upa.row(u));
                    if (missing.isEmpty()) users.set(u);
                }
            }
            ua.add(users);
        }
        return ua;
    }

    /** Rebuilds each user's permissions from the roles they hold. */
    public static List<BitSet> reconstruct(UpaMatrix upa, MiningResult result) {
        List<BitSet> ua = assignments(upa, result);
        List<BitSet> rows = new ArrayList<>();
        for (int u = 0; u < upa.userCount(); u++) rows.add(new BitSet(upa.permCount()));
        List<BitSet> roles = result.roles();
        for (int r = 0; r < roles.size(); r++) {
            BitSet users = ua.get(r);
            for (int u = users.nextSetBit(0); u >= 0; u = users.nextSetBit(u + 1)) {
                rows.get(u).or(roles.get(r));
            }
        }
        return rows;
    }

    /**
     * roles, upaCells, coveredCells, underAssigned (in UPA, not granted by roles),
     * overAssigned (granted by roles, not in UPA), distance (their sum),
     * permissionsUsed (distinct permissions in any role), permissions, users.
     */
    public static Map<String, Object> compute(UpaMatrix upa, MiningResult result) {
        List<BitSet> rebuilt = reconstruct(upa, result);
        int under = 0;
        int over = 0;
        int covered = 0;
        for (int u = 0; u < upa.userCount(); u++) {
            BitSet row = upa.row(u);
            BitSet hit = (BitSet) row.clone();
            hit.and(rebuilt.get(u));
            covered += hit.cardinality();
            BitSet missing = (BitSet) row.clone();
            missing.andNot(rebuilt.get(u));
            under += missing.cardinality();
            BitSet extra = (BitSet) rebuilt.get(u).clone();
            extra.andNot(row);
            over += extra.cardinality();
        }
        BitSet used = new BitSet(upa.permCount());
        for (BitSet role : result.roles()) used.or(role);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("users", upa.userCount());
        m.put("permissions", upa.permCount());
        m.put("upaCells", upa.cardinality());
        m.put("roles", result.roles().size());
        m.put("coveredCells", covered);
        m.put("underAssigned", under);
        m.put("overAssigned", over);
        m.put("distance", under + over);
        m.put("permissionsUsed", used.cardinality());
        return m;
    }
}
