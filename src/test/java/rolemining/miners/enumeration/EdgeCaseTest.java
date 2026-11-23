package rolemining.miners.enumeration;

import java.util.BitSet;
import java.util.List;
import rolemining.core.Metrics;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.RoleMiner;
import rolemining.core.UpaMatrix;
import rolemining.miners.CompleteMiner;
import rolemining.miners.FastMiner;
import rolemining.testing.Check;

public class EdgeCaseTest {
    private static final List<RoleMiner> MINERS = List.of(new FastMiner(), new CompleteMiner());

    private static BitSet set(int... bits) {
        return HandCheckedTest.set(bits);
    }

    public void emptyMatrix() {
        UpaMatrix upa = UpaMatrix.of(new int[0][]);
        for (RoleMiner m : MINERS) {
            for (String legacy : List.of("false", "true")) {
                MiningResult r = m.mine(upa, MinerOptions.empty().set("legacy", legacy));
                Check.equal(0, r.roles().size(), m.name() + " legacy=" + legacy);
                Check.equal(0, Metrics.compute(upa, r).get("distance"));
            }
        }
        Check.equal(List.of(), new FastMiner().candidateRoles(upa, MinerOptions.empty()));
    }

    public void usersWithoutPermissions() {
        UpaMatrix upa = UpaMatrix.of(new int[][] {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}});
        for (RoleMiner m : MINERS) {
            MiningResult r = m.mine(upa, MinerOptions.empty());
            Check.equal(0, r.roles().size(), m.name());
            Check.equal(3, r.stats().get("usersWithoutPermissions"));
            Check.equal(0, r.stats().get("initialRoles"));
        }
        // Legacy keeps the all-zero row as an initial role, merged with the seeded empty role.
        // (The legacy CompleteMiner crashed here: its count list stayed empty.)
        for (RoleMiner m : MINERS) {
            MiningResult r = m.mine(upa, MinerOptions.empty().set("legacy", true));
            Check.equal(List.of(new BitSet()), r.roles(), m.name());
            Check.equal(1, r.stats().get("mergedRoles"));
        }
    }

    public void allZeroRowsAmongOthers() {
        UpaMatrix upa = UpaMatrix.of(new int[][] {{0, 0, 0}, {1, 1, 0}, {0, 0, 0}, {0, 1, 1}});
        for (RoleMiner m : MINERS) {
            MiningResult r = m.mine(upa, MinerOptions.empty());
            Check.equal(List.of(set(0, 1), set(1, 2), set(1)), r.roles(), m.name());
            Check.equal(2, r.stats().get("usersWithoutPermissions"));
            Check.equal(0, Metrics.compute(upa, r).get("distance"));
        }
        // Legacy: initial roles {}, {0,1}, {1,2}; {} is merged with the seed, then the
        // skip passes over {0,1} (harmless here: it is not generated).
        List<BitSet> legacy = new FastMiner().candidateRoles(upa, MinerOptions.empty());
        Check.equal(List.of(new BitSet(), set(0, 1), set(1, 2), set(1)), legacy);
    }

    public void duplicateRows() {
        UpaMatrix upa = UpaMatrix.of(new int[][] {{1, 0, 1}, {1, 0, 1}, {1, 0, 1}});
        for (RoleMiner m : MINERS) {
            MiningResult r = m.mine(upa, MinerOptions.empty());
            Check.equal(List.of(set(0, 2)), r.roles(), m.name());
            Check.equal(0, r.stats().get("generatedRoles"));
            Check.equal(List.of(set(0, 1, 2)), r.assignments());
        }
        CandidateSet c = new FastMiner().enumerate(upa, MinerOptions.empty()).candidates();
        Check.equal(3, c.exactUsers(0));
        Check.equal(300, c.rating(0, 100));
    }

    public void singleUser() {
        UpaMatrix upa = UpaMatrix.of(new int[][] {{0, 1, 1, 0}});
        for (RoleMiner m : MINERS) {
            MiningResult r = m.mine(upa, MinerOptions.empty());
            Check.equal(List.of(set(1, 2)), r.roles(), m.name());
            MiningResult old = m.mine(upa, MinerOptions.empty().set("legacy", true));
            Check.equal(List.of(set(1, 2), new BitSet()), old.roles(), m.name() + " legacy");
        }
    }

    public void identicalUsersAndSubsetUsers() {
        // Every row is a subset of the next: the generated roles are the rows themselves.
        UpaMatrix upa = UpaMatrix.of(new int[][] {{1, 0, 0, 0}, {1, 1, 0, 0}, {1, 1, 1, 0}, {1, 1, 1, 1}});
        for (RoleMiner m : MINERS) {
            MiningResult r = m.mine(upa, MinerOptions.empty());
            Check.equal(List.of(set(0), set(0, 1), set(0, 1, 2), set(0, 1, 2, 3)), r.roles(), m.name());
            Check.equal(3, r.stats().get("mergedRoles"));
        }
    }

    public void widthAboveOneWord() {
        int[][] cells = new int[3][130];
        cells[0][0] = cells[0][64] = cells[0][129] = 1;
        cells[1][64] = cells[1][129] = 1;
        cells[2][129] = cells[2][5] = 1;
        UpaMatrix upa = UpaMatrix.of(cells);
        MiningResult r = new CompleteMiner().mine(upa, MinerOptions.empty());
        Check.equal(List.of(set(64, 129), set(0, 64, 129), set(5, 129), set(129)), r.roles());
    }
}
