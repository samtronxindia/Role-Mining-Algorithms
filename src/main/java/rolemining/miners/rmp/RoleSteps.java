package rolemining.miners.rmp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import rolemining.core.UpaMatrix;

/** Steps specific to one of the legacy miners, kept here so the miners stay short. */
public final class RoleSteps {
    private RoleSteps() {}

    /**
     * Legacy {@code UpdateCR.addIndividualPermToCR}: appends the role {p} for each
     * permission p, in permission order, unless the candidates already contain it.
     * Returns how many were added.
     */
    public static int addSingletons(List<BitSet> candidates, int permCount) {
        Set<Integer> present = new HashSet<>();
        for (BitSet c : candidates) if (c.cardinality() == 1) present.add(c.nextSetBit(0));
        int added = 0;
        for (int p = 0; p < permCount; p++) {
            if (present.contains(p)) continue;
            BitSet single = new BitSet(permCount);
            single.set(p);
            candidates.add(single);
            added++;
        }
        return added;
    }

    /**
     * Legacy {@code associateAdditionalRoles}: role i is also given to every user
     * holding more than {@code threshold * |role i|} of its permissions, granting
     * the whole role (missing permissions become over-assignment). Returns the
     * number of assignments added.
     */
    public static int associate(UpaMatrix upa, List<BitSet> roles, List<BitSet> users, Coverage coverage,
            double threshold) {
        int added = 0;
        for (int i = 0; i < roles.size(); i++) {
            BitSet role = roles.get(i);
            int size = role.cardinality();
            for (int u = 0; u < upa.userCount(); u++) {
                BitSet common = upa.row(u);
                common.and(role);
                if (common.cardinality() > threshold * size) {
                    if (!users.get(i).get(u)) {
                        users.get(i).set(u);
                        added++;
                    }
                    coverage.grant(role, u);
                }
            }
        }
        return added;
    }

    /**
     * The legacy post-processing in {@code DeltaRMPAlgorithm.calcFinalCR}: a
     * permission stays only in the first role (in selection order) that has it.
     * Roles left empty are dropped (the legacy code printed them as empty lines).
     */
    public static List<BitSet> removeEarlierPermissions(List<BitSet> roles) {
        List<BitSet> out = new ArrayList<>();
        BitSet seen = new BitSet();
        for (BitSet role : roles) {
            BitSet kept = (BitSet) role.clone();
            kept.andNot(seen);
            seen.or(role);
            if (!kept.isEmpty()) out.add(kept);
        }
        return out;
    }
}
