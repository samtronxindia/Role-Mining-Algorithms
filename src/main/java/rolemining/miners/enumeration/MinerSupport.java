package rolemining.miners.enumeration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;

/** Turns an {@link EnumerationResult} into a {@link MiningResult} with the legacy statistics. */
public final class MinerSupport {
    private MinerSupport() {}

    public static MiningResult toResult(UpaMatrix upa, EnumerationResult e, Settings s) {
        CandidateSet c = e.candidates();
        List<BitSet> roles = c.roles();
        MiningResult result = new MiningResult(roles, exactAssignments(upa, roles));
        int[] p = s.priorities();
        result.stat("legacyMode", s.legacy())
                .stat("usersWithoutPermissions", e.usersWithoutPermissions())
                .stat("initialRoles", e.initialRoles().size())
                .stat("generatedRoles", e.generatedRoles().size())
                .stat("mergedRoles", e.mergedRoles())
                .stat("candidateRoles", c.size())
                .stat("duplicateCandidates", c.duplicateRoles())
                .stat("initialPermissionsUsed", permissionsUsed(e.initialRoles()))
                .stat("generatedPermissionsUsed", permissionsUsed(e.generatedRoles()))
                .stat("priorities", p[0] + ".." + p[p.length - 1] + " step "
                        + (p.length > 1 ? p[1] - p[0] : 0))
                .stat("order", s.order() == Settings.Order.LEGACY ? "legacy" : "rating@" + s.rankPriority());
        return result;
    }

    /**
     * The exact assignment: role r is held by every user whose permissions include
     * all of r's (an empty role by nobody). The same definition
     * {@code Metrics.assignments} applies when none is given, computed here with
     * word operations because CompleteMiner returns hundreds of thousands of roles.
     */
    public static List<BitSet> exactAssignments(UpaMatrix upa, List<BitSet> roles) {
        int words = Bits.words(upa.permCount());
        long[][] rows = new long[upa.userCount()][];
        for (int u = 0; u < rows.length; u++) rows[u] = Bits.of(upa.row(u), words);
        List<BitSet> out = new ArrayList<>(roles.size());
        for (BitSet role : roles) {
            BitSet users = new BitSet(rows.length);
            if (!role.isEmpty()) {
                long[] r = Bits.of(role, words);
                for (int u = 0; u < rows.length; u++) if (Bits.isSubset(r, rows[u])) users.set(u);
            }
            out.add(users);
        }
        return out;
    }

    static int permissionsUsed(List<BitSet> roles) {
        BitSet used = new BitSet();
        for (BitSet r : roles) used.or(r);
        return used.cardinality();
    }
}
