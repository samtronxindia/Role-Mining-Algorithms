package rolemining.miners.enumeration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import rolemining.core.MinerOptions;
import rolemining.core.UpaMatrix;
import rolemining.miners.CompleteMiner;
import rolemining.miners.FastMiner;
import rolemining.testing.Check;

/**
 * Random small matrices checked against brute-force versions of the definitions:
 * every subset (CompleteMiner) or every pair (FastMiner) of distinct rows.
 */
public class DefinitionTest {
    private static UpaMatrix random(Random rnd, int users, int perms, double density) {
        int[][] cells = new int[users][perms];
        for (int[] row : cells) for (int p = 0; p < perms; p++) row[p] = rnd.nextDouble() < density ? 1 : 0;
        return UpaMatrix.of(cells);
    }

    private static Map<BitSet, Integer> exactCounts(UpaMatrix upa) {
        Map<BitSet, Integer> m = new HashMap<>();
        for (int u = 0; u < upa.userCount(); u++) {
            BitSet row = upa.row(u);
            if (!row.isEmpty()) m.merge(row, 1, Integer::sum);
        }
        return m;
    }

    private static boolean properSubset(BitSet a, BitSet b) {
        BitSet x = (BitSet) a.clone();
        x.andNot(b);
        return x.isEmpty() && !a.equals(b);
    }

    private static void checkCandidates(EnumerationResult e, Map<BitSet, Integer> exact, Set<BitSet> generated) {
        CandidateSet c = e.candidates();
        Set<BitSet> all = new HashSet<>(exact.keySet());
        all.addAll(generated);
        Check.equal(all, new HashSet<>(c.roles()), "candidates = initial + generated");
        Check.equal(all.size(), c.size(), "no duplicate candidates");
        for (int i = 0; i < c.size(); i++) {
            BitSet r = c.role(i);
            CandidateSet.Origin want = exact.containsKey(r)
                    ? (generated.contains(r) ? CandidateSet.Origin.BOTH : CandidateSet.Origin.INITIAL)
                    : CandidateSet.Origin.GENERATED;
            Check.equal(want, c.origin(i), "origin of " + r);
            Check.equal(exact.getOrDefault(r, 0), c.exactUsers(i), "exact users of " + r);
        }
    }

    public void completeMinerMatchesAllSubsetIntersections() {
        Random rnd = new Random(20061030);
        for (int trial = 0; trial < 40; trial++) {
            UpaMatrix upa = random(rnd, 1 + rnd.nextInt(10), 1 + rnd.nextInt(9), 0.2 + 0.6 * rnd.nextDouble());
            Map<BitSet, Integer> exact = exactCounts(upa);
            List<BitSet> distinct = new ArrayList<>(exact.keySet());
            Set<BitSet> generated = new HashSet<>();
            for (int mask = 1; mask < (1 << distinct.size()); mask++) {
                if (Integer.bitCount(mask) < 2) continue;
                BitSet x = null;
                for (int k = 0; k < distinct.size(); k++) {
                    if ((mask & (1 << k)) == 0) continue;
                    if (x == null) x = (BitSet) distinct.get(k).clone();
                    else x.and(distinct.get(k));
                }
                if (!x.isEmpty()) generated.add(x);
            }
            EnumerationResult e = new CompleteMiner().enumerate(upa, MinerOptions.empty());
            Check.equal(generated, new HashSet<>(e.generatedRoles()), "trial " + trial);
            Check.equal(generated.size(), e.generatedRoles().size(), "generated roles are distinct");
            int[] counts = e.generatedCounts();
            for (int g = 0; g < counts.length; g++) {
                BitSet role = e.generatedRoles().get(g);
                int want = 0;
                for (Map.Entry<BitSet, Integer> en : exact.entrySet()) if (properSubset(role, en.getKey())) want += en.getValue();
                Check.equal(want, counts[g], "superset count of " + role);
            }
            checkCandidates(e, exact, generated);
        }
    }

    public void fastMinerMatchesPairIntersections() {
        Random rnd = new Random(42);
        for (int trial = 0; trial < 40; trial++) {
            UpaMatrix upa = random(rnd, 1 + rnd.nextInt(14), 1 + rnd.nextInt(9), 0.2 + 0.6 * rnd.nextDouble());
            Map<BitSet, Integer> exact = exactCounts(upa);
            List<BitSet> distinct = new ArrayList<>(exact.keySet());
            Map<BitSet, Set<BitSet>> producers = new HashMap<>();
            for (int a = 0; a < distinct.size(); a++) {
                for (int b = a + 1; b < distinct.size(); b++) {
                    BitSet x = (BitSet) distinct.get(a).clone();
                    x.and(distinct.get(b));
                    if (x.isEmpty()) continue;
                    Set<BitSet> p = producers.computeIfAbsent(x, k -> new HashSet<>());
                    p.add(distinct.get(a));
                    p.add(distinct.get(b));
                }
            }
            EnumerationResult e = new FastMiner().enumerate(upa, MinerOptions.empty());
            Check.equal(producers.keySet(), new HashSet<>(e.generatedRoles()), "trial " + trial);
            int[] counts = e.generatedCounts();
            for (int g = 0; g < counts.length; g++) {
                BitSet role = e.generatedRoles().get(g);
                int want = 0;
                for (BitSet r : producers.get(role)) want += exact.get(r);
                Check.equal(want, counts[g], "contributor count of " + role);
            }
            checkCandidates(e, exact, producers.keySet());
        }
    }
}
