package rolemining.miners.rmp;

import static rolemining.miners.rmp.Fixtures.bits;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import rolemining.core.Metrics;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;
import rolemining.miners.DeltaRmp;
import rolemining.testing.Check;

public class DeltaRmpTest {
    // u0 {0,1,2}, u1 {0,1,2}, u2 {0,1}, u3 {3}; 9 cells
    private static final UpaMatrix UPA = UpaMatrix.of(new int[][] {
        {1, 1, 1, 0},
        {1, 1, 1, 0},
        {1, 1, 0, 0},
        {0, 0, 0, 1},
    });
    private static final BitSet A = bits(0, 1, 2); // area 3 x 2 users = 6
    private static final BitSet B = bits(0, 1);    // area 2 x 3 users = 6
    private static final BitSet C = bits(3);       // area 1

    private static MiningResult mine(List<BitSet> candidates, Object delta, boolean nonOverlapping) {
        return new DeltaRmp(CandidateSources.fixed(candidates)).mine(UPA,
                MinerOptions.empty().set("delta", delta).set("nonOverlapping", nonOverlapping));
    }

    public void greedyByUncoveredAreaUntilDeltaIsReached() {
        // A first (tie with B, listed first): covers 6, delta 3. Then B covers u2's 2 cells: delta 1.
        MiningResult r = mine(List.of(A, B, C), 1, false);
        Check.equal(List.of(A, B), r.roles());
        Check.equal(1, r.stats().get("delta"));
        Check.equal(2, r.stats().get("rolesSelected"));
        Check.equal(true, r.stats().get("deltaReached"));
        // delta 3 is reached after A (the legacy rule "stop when delta < 3" would also add B)
        Check.equal(List.of(A), mine(List.of(A, B, C), 3, false).roles());
        // nothing needed (the legacy code always added the first role)
        Check.equal(0, mine(List.of(A, B, C), 9, false).roles().size());
    }

    public void tiesGoToTheEarlierCandidate() {
        MiningResult r = mine(List.of(B, A, C), 0, false);
        // B first covers 6 (u0..u2 x {0,1}); then A covers {2} for u0,u1 (2) before C (1)
        Check.equal(List.of(B, A, C), r.roles());
    }

    public void nonOverlappingPostProcessing() {
        MiningResult r = mine(List.of(B, A, C), 0, true);
        Check.equal(List.of(B, bits(2), C), r.roles());
        Check.equal(3, r.stats().get("rolesSelected"));
        // A then B: B loses everything and is dropped
        MiningResult s = mine(List.of(A, B, C), 0, true);
        Check.equal(List.of(A, C), s.roles());
        Check.equal(3, s.stats().get("rolesSelected"));
        // the statistics describe the tiling before post-processing
        Check.equal(0, s.stats().get("delta"));
        Check.equal(List.of(bits(0, 1), bits(2)), RoleSteps.removeEarlierPermissions(
                List.of(bits(0, 1), bits(0, 1), bits(0, 1, 2))));
    }

    public void privilegeAndCoverageStats() {
        MiningResult r = mine(List.of(A, B, C), 3, false);
        // after A: u2 misses 2 of 2, u3 misses 1 of 1 -> (1 + 1) / 4 = 50%; never any extra permission
        Check.equal(50.0, r.stats().get("underPrivilegePercent"));
        Check.equal(0.0, r.stats().get("overPrivilegePercent"));
        Check.equal(3, r.stats().get("permissionCoverage"));
        Check.equal(4, r.stats().get("permissionsHeld"));
        Check.equal(3, r.stats().get("candidates"));
    }

    public void traceListsStepsBelowTheThreshold() {
        MiningResult r = new DeltaRmp(CandidateSources.fixed(List.of(A, B, C))).mine(UPA,
                MinerOptions.empty().set("delta", 0).set("traceBelow", 4));
        Check.equal("1:3 2:1 3:0", r.stats().get("deltaTrace"));
    }

    /** Without post-processing, delta is exactly the distance the core metrics compute. */
    public void deltaEqualsMetricsDistanceWithoutPostProcessing() {
        Random rnd = new Random(3);
        for (int i = 0; i < 100; i++) {
            UpaMatrix upa = Fixtures.random(rnd, rnd.nextInt(14), 1 + rnd.nextInt(9), rnd.nextDouble());
            int target = rnd.nextInt(upa.cardinality() + 1);
            MiningResult r = new DeltaRmp().mine(upa,
                    MinerOptions.empty().set("delta", target).set("nonOverlapping", false));
            int delta = (Integer) r.stats().get("delta");
            Check.equal(delta, Metrics.compute(upa, r).get("distance"), "case " + i);
            Check.equal(0, Metrics.compute(upa, r).get("overAssigned"));
            Check.isTrue(delta <= target, "the default candidates contain every row, so any target is reachable");
        }
    }

    public void unreachableTargetStopsWithoutRepeatingRoles() {
        // only A is offered; u2 and u3 can never be covered (legacy: re-added the top role)
        MiningResult r = mine(List.of(A), 0, false);
        Check.equal(List.of(A), r.roles());
        Check.equal(3, r.stats().get("delta"));
        Check.equal(false, r.stats().get("deltaReached"));
    }

    public void emptyAndAllZeroMatrices() {
        for (int[][] cells : new int[][][] {{}, {{0, 0}, {0, 0}}}) {
            MiningResult r = new DeltaRmp().mine(UpaMatrix.of(cells), MinerOptions.empty().set("delta", 0));
            Check.equal(0, r.roles().size());
            Check.equal(0, r.stats().get("delta"));
            Check.equal(0.0, r.stats().get("underPrivilegePercent"));
        }
    }

    public void rejectsNegativeDelta() {
        Check.fails(IllegalArgumentException.class,
                () -> new DeltaRmp().mine(UPA, MinerOptions.empty().set("delta", -1)));
    }

    /**
     * Legacy output Delta RMP roles.csv (74 roles, without the post-processing)
     * is the first 74 roles of this run; delta is 488 at that point. With the
     * default delta=350 the run stops after 87 roles at delta 349.
     */
    public void android500MatchesLegacyDeltaRoles() throws Exception {
        UpaMatrix upa = Fixtures.android();
        List<BitSet> legacy = Fixtures.loadRoleNames(Fixtures.RESOURCES.resolve("delta-rmp-roles-74.txt"), upa);
        Check.equal(74, legacy.size());
        long start = System.nanoTime();
        MiningResult r = new DeltaRmp().mine(upa, MinerOptions.empty().set("nonOverlapping", false));
        long ms = (System.nanoTime() - start) / 1_000_000;
        Check.equal(87, r.roles().size());
        Check.equal(349, r.stats().get("delta"));
        Check.equal(legacy, r.roles().subList(0, 74));
        Check.isTrue(((String) r.stats().get("deltaTrace")).contains(" 74:488 "), "delta 488 after 74 roles");
        Check.equal(349, Metrics.compute(upa, r).get("distance"));
        Check.isTrue(ms < 10_000, "took " + ms + " ms");
        MiningResult post = new DeltaRmp().mine(upa, MinerOptions.empty());
        Check.equal(87, post.stats().get("rolesSelected"));
        Check.equal(349, post.stats().get("delta"));
        Check.equal(161, post.stats().get("permissionsHeld"));
    }
}
