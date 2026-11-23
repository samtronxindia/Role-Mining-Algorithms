package rolemining.miners.enumeration;

import java.util.Arrays;
import java.util.BitSet;
import rolemining.core.MinerOptions;
import rolemining.core.UpaMatrix;
import rolemining.miners.CompleteMiner;
import rolemining.miners.FastMiner;
import rolemining.testing.Check;

public class SettingsTest {
    public void defaultsAreTheLegacyConstants() {
        Settings s = Settings.parse(MinerOptions.empty(), false, false);
        Check.equal("[10, 20, 30, 40, 50, 60, 70, 80, 90, 100]", Arrays.toString(s.priorities()));
        Check.equal(100, s.rankPriority());
        Check.equal(Settings.Order.LEGACY, s.order());
        Check.equal(false, s.legacy());
        Check.equal(1_000_000, s.maxGenerated());
    }

    public void customPriorities() {
        Settings s = Settings.parse(MinerOptions.empty().set("minPriority", 0).set("priorityStep", 25)
                .set("maxPriority", 60), false, true);
        Check.equal("[0, 25, 50]", Arrays.toString(s.priorities()));
        // The legacy CompleteMiner shift applies only in legacy mode.
        Settings old = Settings.parse(MinerOptions.empty().set("legacy", true), false, true);
        Check.equal("[20, 30, 40, 50, 60, 70, 80, 90, 100, 110]", Arrays.toString(old.priorities()));
        Settings fastOld = Settings.parse(MinerOptions.empty().set("legacy", true), false, false);
        Check.equal("[10, 20, 30, 40, 50, 60, 70, 80, 90, 100]", Arrays.toString(fastOld.priorities()));
    }

    public void badValuesAreRejected() {
        Check.fails(IllegalArgumentException.class,
                () -> Settings.parse(MinerOptions.empty().set("legacy", "yes"), false, false));
        Check.fails(IllegalArgumentException.class,
                () -> Settings.parse(MinerOptions.empty().set("priorityStep", 0), false, false));
        Check.fails(IllegalArgumentException.class,
                () -> Settings.parse(MinerOptions.empty().set("minPriority", 200), false, false));
        Check.fails(IllegalArgumentException.class,
                () -> Settings.parse(MinerOptions.empty().set("order", "random"), false, false));
        Check.fails(IllegalArgumentException.class,
                () -> Settings.parse(MinerOptions.empty().set("maxGeneratedRoles", -1), false, true));
        UpaMatrix upa = UpaMatrix.of(new int[][] {{1, 0}, {1, 1}});
        Check.fails(IllegalArgumentException.class, () -> new FastMiner().mine(upa, MinerOptions.empty().set("delta", 3)));
        // maxGeneratedRoles is a CompleteMiner parameter only.
        Check.fails(IllegalArgumentException.class,
                () -> new FastMiner().mine(upa, MinerOptions.empty().set("maxGeneratedRoles", 3)));
        new CompleteMiner().mine(upa, MinerOptions.empty().set("maxGeneratedRoles", 3));
    }

    public void completeMinerStopsAtTheCap() {
        // 8 users, each missing one different permission: every subset of permissions
        // of size 1..6 is an intersection: C(8,2) + ... + C(8,7) = 246 non-empty generated roles.
        int n = 8;
        int[][] cells = new int[n][n];
        for (int u = 0; u < n; u++) for (int p = 0; p < n; p++) cells[u][p] = u == p ? 0 : 1;
        UpaMatrix upa = UpaMatrix.of(cells);
        EnumerationResult e = new CompleteMiner().enumerate(upa, MinerOptions.empty());
        Check.equal(246, e.generatedRoles().size());
        Check.fails(Enumerator.LimitExceededException.class,
                () -> new CompleteMiner().mine(upa, MinerOptions.empty().set("maxGeneratedRoles", 245)));
        Check.equal(246 + n, new CompleteMiner().mine(upa, MinerOptions.empty().set("maxGeneratedRoles", 246)).roles().size());
        Check.equal(246 + n, new CompleteMiner().mine(upa, MinerOptions.empty().set("maxGeneratedRoles", 0)).roles().size());
    }

    public void roleTableKeepsInsertionOrderAndGrows() {
        RoleTable t = new RoleTable(2);
        for (int i = 0; i < 1000; i++) {
            long[] k = {i, i * 31L};
            Check.equal(-1, t.indexOf(k));
            Check.equal(i, t.add(k));
        }
        for (int i = 0; i < 1000; i++) Check.equal(i, t.indexOf(new long[] {i, i * 31L}));
        Check.equal(1000, t.size());
        BitSet b = t.toBitSets().get(3);
        Check.equal(Bits.toBitSet(new long[] {3, 93}), b);
    }
}
