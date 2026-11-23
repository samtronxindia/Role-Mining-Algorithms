package rolemining.miners.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import rolemining.core.UpaMatrix;

/**
 * The subset-enumeration pipeline shared by CompleteMiner and FastMiner
 * (Vaidya, Atluri and Warner, CCS 2006), ported from the legacy
 * {@code initPhase}, {@code secondPhase}, {@code thirdPhase} and
 * {@code calcRating} methods. Stateless; every call works on its own data.
 *
 * <ol>
 * <li>Initial roles: the distinct user rows, in order of first appearance, each
 *     with its original count (users having exactly that row).</li>
 * <li>Generated roles. FastMiner: the intersections of every pair of initial roles
 *     (i &lt; j, in that loop order); a role's count is the total original count of
 *     the initial roles that produced it. CompleteMiner: the intersections of every
 *     subset of two or more initial roles, enumerated as the legacy code did (for
 *     each i: intersect with every later initial role, then with every generated
 *     role so far, including ones added in that sweep); a role's count is the
 *     number of users whose permissions are a proper superset of it.</li>
 * <li>Candidates: initial roles that are also generated roles are merged
 *     (in initial-role order), then the remaining initial roles, then the remaining
 *     generated roles, each in its own order.</li>
 * </ol>
 */
public final class Enumerator {
    private Enumerator() {}

    /** Which way generated roles are produced and counted. */
    public enum Kind { FAST, COMPLETE }

    /** Thrown when CompleteMiner would generate more roles than {@code maxGeneratedRoles}. */
    public static final class LimitExceededException extends IllegalArgumentException {
        private static final long serialVersionUID = 1L;

        LimitExceededException(String message) { super(message); }
    }

    public static EnumerationResult run(UpaMatrix upa, Settings settings, Kind kind) {
        boolean legacy = settings.legacy();
        int words = Bits.words(upa.permCount());

        // Phase 1: initial roles and original counts (legacy initPhase).
        RoleTable initial = new RoleTable(words);
        List<Integer> orig = new ArrayList<>();
        List<BitSet> initialUsers = new ArrayList<>();
        int noPermissions = 0;
        for (int u = 0; u < upa.userCount(); u++) {
            long[] row = Bits.of(upa.row(u), words);
            boolean empty = Bits.isEmpty(row);
            if (empty) noPermissions++;
            // An empty role grants nothing. The legacy code kept a user with no
            // permissions as an all-zero initial role; only legacy mode does so.
            if (empty && !legacy) continue;
            int idx = initial.indexOf(row);
            if (idx < 0) {
                idx = initial.add(row);
                orig.add(0);
                initialUsers.add(new BitSet());
            }
            orig.set(idx, orig.get(idx) + 1);
            initialUsers.get(idx).set(u);
        }
        int[] origCount = orig.stream().mapToInt(Integer::intValue).toArray();

        // Phase 2: generated roles and their counts.
        // The legacy code seeded the generated roles with an all-zero role (so that
        // gR.get(0) existed to read the width from); it stays only in legacy mode.
        boolean seedEmpty = legacy && initial.size() > 0;
        RoleTable generated = new RoleTable(words);
        if (seedEmpty) generated.add(new long[words]);
        int[] counts;
        List<BitSet> contributors = null;
        if (kind == Kind.FAST) {
            contributors = new ArrayList<>();
            counts = fastGenerate(initial, origCount, generated, contributors, seedEmpty);
        } else {
            completeGenerate(initial, generated, seedEmpty, settings.maxGenerated());
            counts = supersetCounts(initial, origCount, generated);
        }

        // Phase 3: merge into candidate roles (legacy calcRating).
        List<BitSet> roles = new ArrayList<>();
        List<CandidateSet.Origin> origins = new ArrayList<>();
        List<int[]> numbers = new ArrayList<>(); // exact, count, initialIndex, generatedIndex
        List<Integer> initialLeft = new ArrayList<>();
        for (int r = 0; r < initial.size(); r++) initialLeft.add(r);
        boolean[] generatedUsed = new boolean[generated.size()];
        int merged = 0;
        int i = 0;
        while (i < initialLeft.size()) {
            int r = initialLeft.get(i);
            int g = generated.indexOf(initial.get(r));
            if (g >= 0 && !generatedUsed[g]) {
                roles.add(Bits.toBitSet(initial.get(r)));
                origins.add(CandidateSet.Origin.BOTH);
                numbers.add(new int[] {origCount[r], counts[g], r, g});
                initialLeft.remove(i);
                generatedUsed[g] = true;
                merged++;
                // Legacy defect: calcRating removed iR[i] inside "for (i...; i++)",
                // so the role that moved into slot i was never checked.
                if (legacy) i++;
            } else {
                i++;
            }
        }
        for (int r : initialLeft) {
            roles.add(Bits.toBitSet(initial.get(r)));
            origins.add(CandidateSet.Origin.INITIAL);
            numbers.add(new int[] {origCount[r], 0, r, -1});
        }
        for (int g = 0; g < generated.size(); g++) {
            if (generatedUsed[g]) continue;
            roles.add(Bits.toBitSet(generated.get(g)));
            origins.add(CandidateSet.Origin.GENERATED);
            numbers.add(new int[] {0, counts[g], -1, g});
        }
        int n = roles.size();
        int[] exact = new int[n];
        int[] cnt = new int[n];
        int[] ii = new int[n];
        int[] gi = new int[n];
        for (int k = 0; k < n; k++) {
            int[] x = numbers.get(k);
            exact[k] = x[0];
            cnt[k] = x[1];
            ii[k] = x[2];
            gi[k] = x[3];
        }
        CandidateSet candidates = new CandidateSet(List.copyOf(roles),
                origins.toArray(new CandidateSet.Origin[0]), exact, cnt, ii, gi, settings.priorities());
        if (settings.order() == Settings.Order.RATING) {
            candidates = candidates.orderedByRating(settings.rankPriority());
        }
        return new EnumerationResult(initial.toBitSets(), origCount, initialUsers,
                generated.toBitSets(), counts, contributors, candidates, noPermissions, merged);
    }

    /** Legacy PhaseTwo.secondPhase (FastMiner). Returns the counts. */
    private static int[] fastGenerate(RoleTable initial, int[] origCount, RoleTable generated,
            List<BitSet> contributors, boolean seedEmpty) {
        List<Integer> counts = new ArrayList<>();
        if (seedEmpty) {
            counts.add(0);
            contributors.add(new BitSet());
        }
        long[] scratch = new long[initial.words()];
        for (int i = 0; i < initial.size(); i++) {
            for (int j = i + 1; j < initial.size(); j++) {
                Bits.and(initial.get(i), initial.get(j), scratch);
                if (!seedEmpty && Bits.isEmpty(scratch)) continue;
                int g = generated.indexOf(scratch);
                if (g < 0) {
                    generated.add(scratch);
                    counts.add(origCount[i] + origCount[j]);
                    BitSet c = new BitSet();
                    c.set(i);
                    c.set(j);
                    contributors.add(c);
                } else {
                    BitSet c = contributors.get(g);
                    int total = counts.get(g);
                    if (!c.get(i)) {
                        c.set(i);
                        total += origCount[i];
                    }
                    if (!c.get(j)) {
                        c.set(j);
                        total += origCount[j];
                    }
                    counts.set(g, total);
                }
            }
        }
        return counts.stream().mapToInt(Integer::intValue).toArray();
    }

    /** Legacy completeMiner.secondPhase: closes the initial roles under intersection. */
    private static void completeGenerate(RoleTable initial, RoleTable generated, boolean seedEmpty, int cap) {
        long[] scratch = new long[initial.words()];
        for (int i = 0; i < initial.size(); i++) {
            long[] a = initial.get(i);
            for (int j = i + 1; j < initial.size(); j++) {
                Bits.and(a, initial.get(j), scratch);
                addIfNew(generated, scratch, seedEmpty, cap);
            }
            // generated.size() grows during this sweep; the legacy loop re-read it too.
            for (int k = 0; k < generated.size(); k++) {
                Bits.and(a, generated.get(k), scratch);
                addIfNew(generated, scratch, seedEmpty, cap);
            }
        }
    }

    private static void addIfNew(RoleTable generated, long[] role, boolean keepEmpty, int cap) {
        if (!keepEmpty && Bits.isEmpty(role)) return;
        if (generated.indexOf(role) >= 0) return;
        if (cap > 0 && generated.size() >= cap) {
            throw new LimitExceededException("CompleteMiner generated more than " + cap
                    + " roles; raise " + Settings.MAX_GENERATED + " (0 = no limit) or use fastminer");
        }
        generated.add(role);
    }

    /** Legacy completeMiner.thirdPhase: users whose rows are proper supersets of each generated role. */
    private static int[] supersetCounts(RoleTable initial, int[] origCount, RoleTable generated) {
        int[] counts = new int[generated.size()];
        for (int g = 0; g < generated.size(); g++) {
            long[] role = generated.get(g);
            int c = 0;
            for (int j = 0; j < initial.size(); j++) {
                long[] row = initial.get(j);
                if (Bits.isSubset(role, row) && !Arrays.equals(role, row)) c += origCount[j];
            }
            counts[g] = c;
        }
        return counts;
    }
}
