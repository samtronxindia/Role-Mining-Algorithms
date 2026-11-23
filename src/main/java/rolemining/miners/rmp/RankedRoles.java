package rolemining.miners.rmp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rolemining.core.UpaMatrix;

/**
 * Candidate roles ranked by area, the first step shared by the legacy Delta RMP
 * and MinNoise RMP ({@code CalculateAreaAndSort.calcArea}/{@code sortRoles} and
 * {@code CalcAreaAndSortRoles}, which are identical).
 *
 * <p>The tile of a role is the set of users whose row contains all of the role's
 * permissions; the area of the role is |permissions| x |tile users|. Roles are
 * sorted by area, largest first; equal areas keep the candidate order.
 *
 * <p>Two things differ from the legacy code without changing which roles the
 * greedy step selects: duplicate candidates are dropped (the first copy is kept),
 * and roles of area 0 are dropped. The legacy selection sort started each search
 * at {@code maxArea = 0, maxAreaIndex = 0}, so every zero-area candidate was
 * replaced by another copy of candidate 0; a duplicate is never selected before
 * the earlier copy, and after it has uncovered area 0.
 */
public final class RankedRoles {
    private RankedRoles() {}

    /** A candidate role with its tile. Fields are not copied; do not modify them. */
    public record Role(BitSet permissions, BitSet users, int area) {}

    public static List<Role> rank(UpaMatrix upa, List<BitSet> candidates) {
        List<BitSet> columns = columns(upa);
        Set<BitSet> seen = new HashSet<>();
        List<Role> roles = new ArrayList<>();
        for (BitSet candidate : candidates) {
            if (candidate.length() > upa.permCount()) {
                throw new IllegalArgumentException("candidate role uses permission " + (candidate.length() - 1)
                        + " but the matrix has " + upa.permCount() + " permissions");
            }
            BitSet perms = (BitSet) candidate.clone();
            if (perms.isEmpty() || !seen.add(perms)) continue;
            BitSet users = tileUsers(upa, columns, perms);
            int area = perms.cardinality() * users.cardinality();
            if (area > 0) roles.add(new Role(perms, users, area));
        }
        roles.sort(Comparator.comparingInt(Role::area).reversed()); // stable: ties keep candidate order
        return roles;
    }

    /** Users holding every permission of {@code perms} (all users when {@code perms} is empty). */
    public static BitSet tileUsers(UpaMatrix upa, List<BitSet> columns, BitSet perms) {
        BitSet users = new BitSet(upa.userCount());
        users.set(0, upa.userCount());
        for (int p = perms.nextSetBit(0); p >= 0 && !users.isEmpty(); p = perms.nextSetBit(p + 1)) {
            users.and(columns.get(p));
        }
        return users;
    }

    /** columns.get(p) is the set of users holding permission p. */
    public static List<BitSet> columns(UpaMatrix upa) {
        List<BitSet> cols = new ArrayList<>(upa.permCount());
        for (int p = 0; p < upa.permCount(); p++) cols.add(new BitSet(upa.userCount()));
        for (int u = 0; u < upa.userCount(); u++) {
            BitSet row = upa.row(u);
            for (int p = row.nextSetBit(0); p >= 0; p = row.nextSetBit(p + 1)) cols.get(p).set(u);
        }
        return cols;
    }
}
