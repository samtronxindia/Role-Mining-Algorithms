package rolemining.miners.enumeration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Everything one run of the enumeration computed, in the legacy order: initial
 * roles with their original counts, generated roles with their counts, and the
 * candidate roles. Immutable; getters return copies.
 */
public final class EnumerationResult {
    private final List<BitSet> initialRoles;
    private final int[] originalCounts;
    private final List<BitSet> initialUsers;
    private final List<BitSet> generatedRoles;
    private final int[] generatedCounts;
    private final List<BitSet> contributors;
    private final CandidateSet candidates;
    private final int usersWithoutPermissions;
    private final int mergedRoles;

    EnumerationResult(List<BitSet> initialRoles, int[] originalCounts, List<BitSet> initialUsers,
            List<BitSet> generatedRoles, int[] generatedCounts, List<BitSet> contributors,
            CandidateSet candidates, int usersWithoutPermissions, int mergedRoles) {
        this.initialRoles = initialRoles;
        this.originalCounts = originalCounts;
        this.initialUsers = initialUsers;
        this.generatedRoles = generatedRoles;
        this.generatedCounts = generatedCounts;
        this.contributors = contributors;
        this.candidates = candidates;
        this.usersWithoutPermissions = usersWithoutPermissions;
        this.mergedRoles = mergedRoles;
    }

    /** Distinct user rows in order of first appearance (the legacy "IR"). */
    public List<BitSet> initialRoles() { return copy(initialRoles); }

    /** Users having exactly initial role i (the legacy "origCount"). */
    public int[] originalCounts() { return originalCounts.clone(); }

    /** The users (row indices) whose row is exactly initial role i. */
    public List<BitSet> initialUsers() { return copy(initialUsers); }

    /** Generated roles in the order the legacy code added them (the legacy "GR"). */
    public List<BitSet> generatedRoles() { return copy(generatedRoles); }

    /** The legacy "count" of each generated role, before the merge step. */
    public int[] generatedCounts() { return generatedCounts.clone(); }

    /**
     * FastMiner only: for each generated role, the indices of the initial roles
     * whose pairwise intersections produced it (the legacy "listOfContributors").
     * Null for CompleteMiner.
     */
    public List<BitSet> contributors() { return contributors == null ? null : copy(contributors); }

    /** The candidate roles (the legacy "CR"), in legacy order. */
    public CandidateSet candidates() { return candidates; }

    /** Users with no permission at all. */
    public int usersWithoutPermissions() { return usersWithoutPermissions; }

    /** Initial roles that were also generated and so became one candidate (legacy "commonElements"). */
    public int mergedRoles() { return mergedRoles; }

    private static List<BitSet> copy(List<BitSet> in) {
        List<BitSet> out = new ArrayList<>(in.size());
        for (BitSet b : in) out.add((BitSet) b.clone());
        return out;
    }
}
