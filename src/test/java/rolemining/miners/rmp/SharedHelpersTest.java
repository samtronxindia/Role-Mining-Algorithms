package rolemining.miners.rmp;

import static rolemining.miners.rmp.Fixtures.bits;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import rolemining.core.MinerOptions;
import rolemining.core.UpaMatrix;
import rolemining.testing.Check;

/** CandidateSources, RankedRoles, Coverage and GreedyTiler. */
public class SharedHelpersTest {
    // u0 {0,1,2}, u1 {0,1,2}, u2 {0,1}, u3 {3}
    private static final UpaMatrix UPA = UpaMatrix.of(new int[][] {
        {1, 1, 1, 0},
        {1, 1, 1, 0},
        {1, 1, 0, 0},
        {0, 0, 0, 1},
    });

    public void candidatesAreDistinctRowsThenNewPairwiseIntersections() {
        List<BitSet> c = CandidateSources.rowsAndPairwiseIntersections(UPA, MinerOptions.empty());
        // rows {0,1,2}, {0,1}, {3}; intersections {0,1,2}&{0,1} = {0,1} (already there), others empty
        Check.equal(List.of(bits(0, 1, 2), bits(0, 1), bits(3)), c);
        UpaMatrix m = UpaMatrix.of(new int[][] {{1, 1, 0}, {0, 1, 1}, {1, 0, 1}});
        Check.equal(List.of(bits(0, 1), bits(1, 2), bits(0, 2), bits(1), bits(0), bits(2)),
                CandidateSources.rowsAndPairwiseIntersections(m, MinerOptions.empty()));
    }

    public void defaultSourceIsTheGenerator() {
        Check.equal(CandidateSources.rowsAndPairwiseIntersections(UPA, MinerOptions.empty()),
                CandidateSources.defaultSource().apply(UPA, MinerOptions.empty()));
    }

    public void fixedSourceReturnsFreshCopies() {
        List<BitSet> in = new ArrayList<>(List.of(bits(1)));
        var src = CandidateSources.fixed(in);
        in.get(0).set(2);
        List<BitSet> first = src.apply(UPA, MinerOptions.empty());
        Check.equal(List.of(bits(1)), first);
        first.get(0).set(3);
        Check.equal(List.of(bits(1)), src.apply(UPA, MinerOptions.empty()));
    }

    public void rankingByAreaKeepsCandidateOrderOnTiesAndDropsDuplicatesAndZeroArea() {
        // areas: {0,1}: 2x3 = 6, {0,1,2}: 3x2 = 6, {3}: 1, {2,3}: 0, {} : 0
        List<RankedRoles.Role> r = RankedRoles.rank(UPA,
                List.of(bits(3), bits(0, 1), bits(2, 3), bits(0, 1, 2), new BitSet(), bits(0, 1)));
        Check.equal(3, r.size());
        Check.equal(bits(0, 1), r.get(0).permissions());
        Check.equal(bits(0, 1, 2), r.get(1).permissions());
        Check.equal(bits(3), r.get(2).permissions());
        Check.equal(6, r.get(0).area());
        Check.equal(bits(0, 1, 2), r.get(0).users());
        Check.equal(bits(0, 1), r.get(1).users());
    }

    public void rankingRejectsPermissionsOutsideTheMatrix() {
        Check.fails(IllegalArgumentException.class, () -> RankedRoles.rank(UPA, List.of(bits(4))));
    }

    public void coverageCountsUnderAndOverAndPrivilege() {
        Coverage c = new Coverage(UPA);
        Check.equal(9, c.delta());
        c.grant(bits(0, 1, 2), bits(0, 1, 2)); // u2 gets 2 extra
        Check.equal(1, c.underAssigned());
        Check.equal(1, c.overAssigned());
        Check.equal(3, c.permissionCoverage());
        double[] pr = c.privilegePercent();
        Check.equal(25.0, pr[0]); // only u3 misses its one permission
        Check.isTrue(Math.abs(pr[1] - 100.0 / 12) < 1e-12, "over = (1/3)/4 users, got " + pr[1]);
        Check.equal(4, Coverage.heldPermissions(UPA));
    }

    public void privilegeIsZeroNotNaNForEmptyUsersAndEmptyMatrix() {
        Coverage c = new Coverage(UpaMatrix.of(new int[][] {{0, 0}, {1, 0}}));
        double[] pr = c.privilegePercent();
        Check.equal(50.0, pr[0]);
        Check.equal(0.0, pr[1]);
        double[] none = new Coverage(UpaMatrix.of(new int[0][])).privilegePercent();
        Check.equal(0.0, none[0]);
        Check.equal(0.0, none[1]);
    }

    public void lazyGreedyMatchesFullScanIncludingTiesAndExternalGrants() {
        Random rnd = new Random(42);
        for (int trial = 0; trial < 200; trial++) {
            UpaMatrix upa = Fixtures.random(rnd, 2 + rnd.nextInt(12), 2 + rnd.nextInt(8), 0.2 + rnd.nextDouble() * 0.5);
            List<BitSet> cands = CandidateSources.rowsAndPairwiseIntersections(upa, MinerOptions.empty());
            RoleSteps.addSingletons(cands, upa.permCount());
            List<RankedRoles.Role> ranked = RankedRoles.rank(upa, cands);
            Coverage lazy = new Coverage(upa);
            Coverage full = new Coverage(upa);
            GreedyTiler tiler = new GreedyTiler(ranked);
            boolean[] taken = new boolean[ranked.size()];
            while (true) {
                if (rnd.nextInt(4) == 0 && upa.userCount() > 0 && upa.permCount() > 0) {
                    // a grant from outside the tiler, as MinNoise's association step makes
                    int u = rnd.nextInt(upa.userCount());
                    BitSet p = bits(rnd.nextInt(upa.permCount()));
                    lazy.grant(p, u);
                    full.grant(p, u);
                }
                int best = -1;
                int bestArea = 0;
                for (int i = 0; i < ranked.size(); i++) {
                    if (taken[i]) continue;
                    int a = full.uncoveredArea(ranked.get(i).permissions(), ranked.get(i).users());
                    if (a > bestArea) {
                        bestArea = a;
                        best = i;
                    }
                }
                int got = tiler.next(lazy);
                Check.equal(best, got, "trial " + trial);
                if (got < 0) break;
                taken[got] = true;
                lazy.grant(ranked.get(got).permissions(), ranked.get(got).users());
                full.grant(ranked.get(got).permissions(), ranked.get(got).users());
            }
            Check.equal(0, lazy.underAssigned(), "singletons let the greedy cover everything");
        }
    }

    public void paramsParseBooleansAndLists() {
        MinerOptions o = MinerOptions.empty().set("a", "false").set("b", "1, 5,3").set("c", "none").set("d", "maybe");
        Check.equal(false, Params.getBoolean(o, "a", true));
        Check.equal(true, Params.getBoolean(o, "zz", true));
        Check.fails(IllegalArgumentException.class, () -> Params.getBoolean(o, "d", true));
        Check.equal(java.util.Set.of(1, 3, 5), Params.getIntSet(o, "b", ""));
        Check.equal(java.util.Set.of(), Params.getIntSet(o, "c", "7"));
        Check.equal(java.util.Set.of(7), Params.getIntSet(o, "zz", "7"));
        Check.fails(IllegalArgumentException.class, () -> Params.getIntSet(o, "d", ""));
    }
}
