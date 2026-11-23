package rolemining.miners.rmp;

import static rolemining.miners.rmp.Fixtures.bits;

import java.util.List;
import java.util.Map;
import java.util.Random;
import rolemining.core.Metrics;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;
import rolemining.miners.BasicRmp;
import rolemining.testing.Check;

public class BasicRmpTest {
    private final BasicRmp miner = new BasicRmp();

    /**
     * The legacy test input. u0 {0,1,4,5,6}, u1 {3,4,5,6}, u2 {0,1,3,4}, u3 {0,1}.
     * First uncovered permission 0: ranges [0] (users 0,2,3, area 3), [0,1] (area 6),
     * [0..2] (nobody) -> [0,1]. Then 3: [3] 2, [3,4] 4, [3..5] 3 (u1 only), [3..6] 4
     * (not larger) -> [3,4] for u1,u2. Then 4: [4] 1, [4,5] 3, [4..6] 5 -> users 0,1.
     * (The legacy code, run on this file, printed the same PA and UA.)
     */
    public void legacyFixtureByHand() throws Exception {
        UpaMatrix upa = UpaMatrix.load(Fixtures.SMALL_4X7);
        MiningResult r = miner.mine(upa, MinerOptions.empty());
        Check.equal(List.of(bits(0, 1), bits(3, 4), bits(4, 5, 6)), r.roles());
        Check.equal(List.of(bits(0, 2, 3), bits(1, 2), bits(0, 1)), r.assignments());
        Check.equal(0, Metrics.compute(upa, r).get("distance"));
    }

    public void onlyContiguousRangesAreConsidered() {
        // the best tile overall is {0,2} for both users (area 4), but the range 0..2 is held by nobody
        UpaMatrix upa = UpaMatrix.of(new int[][] {{1, 0, 1}, {1, 0, 1}});
        MiningResult r = miner.mine(upa, MinerOptions.empty());
        Check.equal(List.of(bits(0), bits(2)), r.roles());
        Check.equal(List.of(bits(0, 1), bits(0, 1)), r.assignments());
    }

    public void reconstructsRandomMatricesExactly() {
        Random rnd = new Random(7);
        for (int i = 0; i < 100; i++) {
            UpaMatrix upa = Fixtures.random(rnd, rnd.nextInt(15), 1 + rnd.nextInt(10), rnd.nextDouble());
            MiningResult r = miner.mine(upa, MinerOptions.empty());
            Map<String, Object> m = Metrics.compute(upa, r);
            Check.equal(0, m.get("distance"), "case " + i);
            for (int k = 0; k < r.roles().size(); k++) {
                Check.isTrue(!r.roles().get(k).isEmpty() && !r.assignments().get(k).isEmpty(), "no empty tiles");
            }
        }
    }

    public void emptyAndAllZeroMatrices() {
        // the legacy loop stored one empty tile before noticing there was nothing to cover
        for (int[][] cells : new int[][][] {{}, {{0, 0, 0}, {0, 0, 0}}}) {
            UpaMatrix upa = UpaMatrix.of(cells);
            Check.equal(0, miner.mine(upa, MinerOptions.empty()).roles().size());
        }
        UpaMatrix someZero = UpaMatrix.of(new int[][] {{0, 0}, {1, 1}, {0, 0}});
        MiningResult r = miner.mine(someZero, MinerOptions.empty());
        Check.equal(List.of(bits(0, 1)), r.roles());
        Check.equal(List.of(bits(1)), r.assignments());
    }

    /** The legacy code run on this dataset also printed 173 roles, distance 0, with the same PA and UA. */
    public void android500() throws Exception {
        UpaMatrix upa = Fixtures.android();
        long start = System.nanoTime();
        MiningResult r = miner.mine(upa, MinerOptions.empty());
        long ms = (System.nanoTime() - start) / 1_000_000;
        Check.equal(173, r.roles().size());
        Check.equal(0, Metrics.compute(upa, r).get("distance"));
        Check.isTrue(ms < 10_000, "took " + ms + " ms");
    }
}
