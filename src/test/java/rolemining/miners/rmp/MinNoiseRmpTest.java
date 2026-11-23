package rolemining.miners.rmp;

import static rolemining.miners.rmp.Fixtures.bits;

import java.util.BitSet;
import java.util.List;
import java.util.Random;
import rolemining.core.Metrics;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;
import rolemining.miners.MinNoiseRmp;
import rolemining.testing.Check;

public class MinNoiseRmpTest {
    // u0 {0,1,2}, u1 {0,1,2}, u2 {0,1}, u3 {3}; 9 cells
    private static final UpaMatrix UPA = UpaMatrix.of(new int[][] {
        {1, 1, 1, 0},
        {1, 1, 1, 0},
        {1, 1, 0, 0},
        {0, 0, 0, 1},
    });
    private static final BitSet A = bits(0, 1, 2);
    private static final BitSet B = bits(0, 1);
    private static final BitSet C = bits(3);

    private static MiningResult mine(UpaMatrix upa, List<BitSet> candidates, MinerOptions o) {
        return new MinNoiseRmp(CandidateSources.fixed(candidates)).mine(upa, o);
    }

    /**
     * One role: A (area 6, tied with B, listed first). Before association u2 misses
     * {0,1} and u3 misses {3}: delta 3. u2 holds 2 of A's 3 permissions (> 1.5), so it
     * gets A too: {0,1} covered, {2} extra. Final delta: 1 missing + 1 extra = 2.
     */
    public void oneRoleWithAssociationByHand() {
        MiningResult r = mine(UPA, List.of(A, B, C), MinerOptions.empty().set("roles", 1));
        Check.equal(List.of(A), r.roles());
        Check.equal(List.of(bits(0, 1, 2)), r.assignments());
        Check.equal(3, r.stats().get("deltaBeforeAssociation"));
        Check.equal(2, r.stats().get("delta"));
        Check.equal(1, Metrics.compute(UPA, r).get("underAssigned"));
        Check.equal(1, Metrics.compute(UPA, r).get("overAssigned"));
        Check.equal(1, r.stats().get("associatedAssignments"));
        Check.equal(25.0, r.stats().get("underPrivilegePercent"));          // u3: 1 of 1 missing
        Check.isTrue(Math.abs((Double) r.stats().get("overPrivilegePercent") - 100.0 / 12) < 1e-12,
                "u2: 1 extra of 3 granted, averaged over 4 users");
        Check.equal(2, Metrics.compute(UPA, r).get("distance"));
        // singletons {0}, {1}, {2} were added ({3} = C was already a candidate)
        Check.equal(3, r.stats().get("singletonCandidatesAdded"));
        Check.equal(6, r.stats().get("candidates"));
    }

    public void thresholdOneDisablesAssociation() {
        MiningResult r = mine(UPA, List.of(A, B, C),
                MinerOptions.empty().set("roles", 1).set("associateThreshold", 1));
        Check.equal(3, r.stats().get("delta"));
        Check.equal(List.of(bits(0, 1)), r.assignments());
    }

    public void singletonsCompleteTheCover() {
        // no candidates at all: only singletons, by holder count {0}: 3, {1}: 3, {2}: 2, {3}: 1
        MiningResult r = mine(UPA, List.of(), MinerOptions.empty().set("roles", 10));
        Check.equal(List.of(bits(0), bits(1), bits(2), bits(3)), r.roles());
        Check.equal(0, r.stats().get("delta"));
    }

    public void neverMoreRolesThanAskedOrThanCandidates() {
        Random rnd = new Random(11);
        for (int i = 0; i < 100; i++) {
            UpaMatrix upa = Fixtures.random(rnd, rnd.nextInt(12), 1 + rnd.nextInt(8), rnd.nextDouble());
            int k = rnd.nextInt(8);
            MiningResult r = new MinNoiseRmp().mine(upa, MinerOptions.empty().set("roles", k));
            Check.isTrue(r.roles().size() <= k, "asked " + k + ", got " + r.roles().size());
            Check.isTrue(r.roles().size() <= (Integer) r.stats().get("candidates"), "more roles than candidates");
            Check.equal(r.stats().get("delta"), Metrics.compute(upa, r).get("distance"), "delta is the distance");
            Check.equal(java.util.Set.copyOf(r.roles()).size(), r.roles().size(), "no repeated roles");
        }
        Check.equal(0, new MinNoiseRmp().mine(UPA, MinerOptions.empty().set("roles", 0)).roles().size());
    }

    public void roleCapLimitsTheRoleCount() {
        MiningResult r = mine(UPA, List.of(A, B, C), MinerOptions.empty().set("roles", 3).set("roleCap", 1));
        Check.equal(1, r.roles().size());
        Check.equal(3, r.stats().get("rolesRequested"));
    }

    /**
     * u0..u2 {0,1,2,4}, u3 u4 {0,1,2,8}, u5 {5,6,7}; candidates R = {0,1,2,4} (area 12),
     * X = {0,1,2,8} (8), Y = {5,6,7} (3). R is picked first; X then has 8 uncovered
     * cells, Y 3. The interim association at 1 role gives R to u3 and u4 (3 of 4 > 2),
     * which leaves X only 2. A fresh pick takes Y; the legacy stale pick takes X.
     */
    public void interimAssociationFreshAndStalePick() {
        UpaMatrix upa = UpaMatrix.of(new int[][] {
            {1, 1, 1, 0, 1, 0, 0, 0, 0},
            {1, 1, 1, 0, 1, 0, 0, 0, 0},
            {1, 1, 1, 0, 1, 0, 0, 0, 0},
            {1, 1, 1, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 1, 1, 1, 0},
        });
        BitSet r = bits(0, 1, 2, 4);
        BitSet x = bits(0, 1, 2, 8);
        BitSet y = bits(5, 6, 7);
        List<BitSet> cands = List.of(r, x, y);
        MinerOptions base = MinerOptions.empty().set("roles", 2).set("interimAssociations", "1");
        MiningResult fresh = mine(upa, cands, base);
        Check.equal(List.of(r, y), fresh.roles());
        Check.equal(1, fresh.stats().get("interimAssociationRuns"));
        // R goes to u3 and u4 in the interim step; at the end Y (fresh) gains nobody
        Check.equal(2, fresh.stats().get("associatedAssignments"));
        Check.equal(List.of(bits(0, 1, 2, 3, 4), bits(5)), fresh.assignments());
        MiningResult stale = mine(upa, cands, MinerOptions.empty().set("roles", 2).set("interimAssociations", "1")
                .set("refreshAfterAssociation", false));
        Check.equal(List.of(r, x), stale.roles());
        // without the interim step both settings pick X second (8 > 3)
        MiningResult none = mine(upa, cands, MinerOptions.empty().set("roles", 2).set("interimAssociations", "none"));
        Check.equal(List.of(r, x), none.roles());
        Check.equal(0, none.stats().get("interimAssociationRuns"));
        // an interim point at the requested role count never runs (the legacy loop stops first)
        MiningResult atEnd = mine(upa, cands, MinerOptions.empty().set("roles", 1).set("interimAssociations", "1"));
        Check.equal(0, atEnd.stats().get("interimAssociationRuns"));
    }

    public void emptyAndAllZeroMatrices() {
        for (int[][] cells : new int[][][] {{}, {{0, 0}, {0, 0}}}) {
            MiningResult r = new MinNoiseRmp().mine(UpaMatrix.of(cells), MinerOptions.empty().set("roles", 3));
            Check.equal(0, r.roles().size());
            Check.equal(0, r.stats().get("delta"));
        }
    }

    public void rejectsBadParameters() {
        Check.fails(IllegalArgumentException.class,
                () -> new MinNoiseRmp().mine(UPA, MinerOptions.empty().set("roles", -1)));
        Check.fails(IllegalArgumentException.class,
                () -> new MinNoiseRmp().mine(UPA, MinerOptions.empty().set("interimAssociations", "a,b")));
        Check.fails(IllegalArgumentException.class,
                () -> new MinNoiseRmp().mine(UPA, MinerOptions.empty().set("associateThreshold", -0.5)));
    }

    /** Legacy outputs PA_R_5_Delta_4491 and PA_R_20_Delta_7075, reproduced role for role. */
    public void android500MatchesLegacyOutputs() throws Exception {
        UpaMatrix upa = Fixtures.android();
        for (String file : new String[] {"PA_R_5_Delta_4491.txt", "PA_R_20_Delta_7075.txt"}) {
            List<BitSet> legacy = Fixtures.loadRoleNames(Fixtures.RESOURCES.resolve(file), upa);
            int delta = Integer.parseInt(file.replaceAll(".*_Delta_(\\d+)\\.txt", "$1"));
            long start = System.nanoTime();
            MiningResult r = new MinNoiseRmp().mine(upa, MinerOptions.empty().set("roles", legacy.size()));
            long ms = (System.nanoTime() - start) / 1_000_000;
            Check.equal(legacy, r.roles(), file);
            Check.equal(delta, r.stats().get("delta"), file);
            Check.equal(delta, Metrics.compute(upa, r).get("distance"), file);
            Check.isTrue(ms < 10_000, "took " + ms + " ms");
        }
    }

    /**
     * PA_R_10_Delta_5461 came from a run that had also associated at 5 roles and
     * then picked on stale areas; only refreshAfterAssociation=false reproduces it.
     */
    public void android500StaleInterimAssociation() throws Exception {
        UpaMatrix upa = Fixtures.android();
        List<BitSet> legacy = Fixtures.loadRoleNames(Fixtures.RESOURCES.resolve("PA_R_10_Delta_5461.txt"), upa);
        MinerOptions o = MinerOptions.empty().set("roles", 10).set("interimAssociations", "5");
        MiningResult stale = new MinNoiseRmp().mine(upa, o.set("refreshAfterAssociation", false));
        Check.equal(legacy, stale.roles());
        Check.equal(5461, stale.stats().get("delta"));
        MiningResult fresh = new MinNoiseRmp().mine(upa,
                MinerOptions.empty().set("roles", 10).set("interimAssociations", "5"));
        Check.isTrue(!legacy.equals(fresh.roles()), "a fresh pick differs from the legacy file");
    }
}
