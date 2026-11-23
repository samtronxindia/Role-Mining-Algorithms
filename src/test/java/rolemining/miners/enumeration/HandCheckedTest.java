package rolemining.miners.enumeration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;
import rolemining.miners.CompleteMiner;
import rolemining.miners.FastMiner;
import rolemining.testing.Check;

/**
 * Small matrices whose candidate roles, counts and ratings are worked out by
 * hand from the definitions (see the comments), including the two fixtures.
 */
public class HandCheckedTest {
    static BitSet set(int... bits) {
        BitSet b = new BitSet();
        for (int x : bits) b.set(x);
        return b;
    }

    static List<BitSet> sets(BitSet... b) {
        return Arrays.asList(b);
    }

    static MinerOptions legacy() {
        return MinerOptions.empty().set("legacy", true);
    }

    static void expect(CandidateSet c, List<BitSet> roles, String origins, int[] exact, int[] count) {
        Check.equal(roles, c.roles(), "roles");
        StringBuilder o = new StringBuilder();
        for (int i = 0; i < c.size(); i++) o.append(c.origin(i).name().charAt(0));
        Check.equal(origins, o.toString(), "origins (I=initial, G=generated, B=both)");
        int[] e = new int[c.size()];
        int[] n = new int[c.size()];
        for (int i = 0; i < c.size(); i++) {
            e[i] = c.exactUsers(i);
            n[i] = c.count(i);
        }
        Check.equal(Arrays.toString(exact), Arrays.toString(e), "exact users");
        Check.equal(Arrays.toString(count), Arrays.toString(n), "counts");
    }

    // small-4x7.csv: A={0,1,4,5,6} B={3,4,5,6} C={0,1,3,4} D={0,1}, one user each.
    static UpaMatrix small4x7() throws Exception {
        return UpaMatrix.load(Path.of("src/test/resources/fixtures/small-4x7.csv"));
    }

    // small-15x4.csv: two empty rows; P={0,1,3} x5, Q={1,2} x3, R={1,2,3} x3, S={3} x2,
    // in that order of first appearance.
    static UpaMatrix small15x4() throws Exception {
        return UpaMatrix.load(Path.of("src/test/resources/fixtures/small-15x4.csv"));
    }

    public void fastMinerOnSmall4x7() throws Exception {
        // Pairs: AB={4,5,6} AC={0,1,4} AD={0,1} BC={3,4} BD={} CD={0,1}.
        // {0,1} is produced by AD and CD: contributors A,D,C, count 3. D equals {0,1}: merged.
        EnumerationResult e = new FastMiner().enumerate(small4x7(), MinerOptions.empty());
        Check.equal(sets(set(0, 1, 4, 5, 6), set(3, 4, 5, 6), set(0, 1, 3, 4), set(0, 1)), e.initialRoles());
        Check.equal(sets(set(4, 5, 6), set(0, 1, 4), set(0, 1), set(3, 4)), e.generatedRoles());
        Check.equal("[2, 2, 3, 2]", Arrays.toString(e.generatedCounts()));
        Check.equal(sets(set(0, 1), set(0, 2), set(0, 2, 3), set(1, 2)), e.contributors());
        expect(e.candidates(),
                sets(set(0, 1), set(0, 1, 4, 5, 6), set(3, 4, 5, 6), set(0, 1, 3, 4), set(4, 5, 6), set(0, 1, 4), set(3, 4)),
                "BIIIGGG", new int[] {1, 1, 1, 1, 0, 0, 0}, new int[] {3, 0, 0, 0, 2, 2, 2});
        // rating = exact * p + count
        Check.equal(List.of(13, 23, 33, 43, 53, 63, 73, 83, 93, 103), e.candidates().ratings(0));
        Check.equal(List.of(2, 2, 2, 2, 2, 2, 2, 2, 2, 2), e.candidates().ratings(4));
    }

    public void fastMinerOnSmall4x7Legacy() throws Exception {
        // Legacy: the all-zero role is seeded first and collects the empty pair BD (B, D: count 2).
        EnumerationResult e = new FastMiner().enumerate(small4x7(), legacy());
        Check.equal(sets(set(), set(4, 5, 6), set(0, 1, 4), set(0, 1), set(3, 4)), e.generatedRoles());
        Check.equal("[2, 2, 2, 3, 2]", Arrays.toString(e.generatedCounts()));
        expect(e.candidates(),
                sets(set(0, 1), set(0, 1, 4, 5, 6), set(3, 4, 5, 6), set(0, 1, 3, 4), set(), set(4, 5, 6), set(0, 1, 4), set(3, 4)),
                "BIIIGGGG", new int[] {1, 1, 1, 1, 0, 0, 0, 0}, new int[] {3, 0, 0, 0, 2, 2, 2, 2});
    }

    public void completeMinerOnSmall4x7() throws Exception {
        // All intersections of 2+ roles: AB={4,5,6} AC={0,1,4} AD=CD=ACD={0,1} BC={3,4} ABC={4};
        // the rest are empty. Order: i=A pairs; i=B pair BC, then sweep finds B∩{0,1,4}={4}.
        // Counts are users with a proper superset: {4,5,6}:A,B  {0,1,4}:A,C  {0,1}:A,C (D is equal)
        // {3,4}:B,C  {4}:A,B,C.
        EnumerationResult e = new CompleteMiner().enumerate(small4x7(), MinerOptions.empty());
        Check.equal(sets(set(4, 5, 6), set(0, 1, 4), set(0, 1), set(3, 4), set(4)), e.generatedRoles());
        Check.equal("[2, 2, 2, 2, 3]", Arrays.toString(e.generatedCounts()));
        Check.equal(null, e.contributors());
        expect(e.candidates(),
                sets(set(0, 1), set(0, 1, 4, 5, 6), set(3, 4, 5, 6), set(0, 1, 3, 4), set(4, 5, 6), set(0, 1, 4), set(3, 4), set(4)),
                "BIIIGGGG", new int[] {1, 1, 1, 1, 0, 0, 0, 0}, new int[] {2, 0, 0, 0, 2, 2, 2, 3});
        Check.equal(List.of(12, 22, 32, 42, 52, 62, 72, 82, 92, 102), e.candidates().ratings(0));
    }

    public void completeMinerLegacyPrioritiesAreShifted() throws Exception {
        // The legacy loop added 10 before using the priority: 20..110 for maxPriority=100.
        CandidateSet c = new CompleteMiner().enumerate(small4x7(), legacy()).candidates();
        Check.equal("[20, 30, 40, 50, 60, 70, 80, 90, 100, 110]", Arrays.toString(c.priorities()));
        Check.equal(set(0, 1), c.role(0));
        Check.equal(List.of(22, 32, 42, 52, 62, 72, 82, 92, 102, 112), c.ratings(0));
        // The seeded empty role: every user has a proper superset of it.
        int empty = c.roles().indexOf(set());
        Check.equal(4, c.count(empty), "count of the empty role");
    }

    public void fastMinerOnSmall15x4() throws Exception {
        // Pairs: PQ={1} (P,Q: 8) PR={1,3} (8) PS={3} (P,S: 7) QR={1,2} (Q,R: 6) QS={} RS={3} (+R: 10).
        // Q={1,2} and S={3} are also generated: merged.
        MiningResult r = new FastMiner().mine(small15x4(), MinerOptions.empty());
        EnumerationResult e = new FastMiner().enumerate(small15x4(), MinerOptions.empty());
        Check.equal("[5, 3, 3, 2]", Arrays.toString(e.originalCounts()));
        Check.equal(sets(set(1), set(1, 3), set(3), set(1, 2)), e.generatedRoles());
        Check.equal("[8, 8, 10, 6]", Arrays.toString(e.generatedCounts()));
        expect(e.candidates(), sets(set(1, 2), set(3), set(0, 1, 3), set(1, 2, 3), set(1), set(1, 3)),
                "BBIIGG", new int[] {3, 2, 5, 3, 0, 0}, new int[] {6, 10, 0, 0, 8, 8});
        Check.equal(e.candidates().roles(), r.roles());
        Check.equal(2, r.stats().get("usersWithoutPermissions"));
        Check.equal(4, r.stats().get("initialRoles"));
        Check.equal(2, r.stats().get("mergedRoles"));
        Check.equal(6, r.stats().get("candidateRoles"));
    }

    public void completeMinerOnSmall15x4() throws Exception {
        // Intersections: {1}, {1,3}, {3}, {1,2} (PQ, PR, PS=RS=PRS, QR; PQR={1}; rest empty).
        // Proper supersets: {1}: P,Q,R = 11; {1,3}: P,R = 8; {3}: P,R = 8; {1,2}: R = 3.
        EnumerationResult e = new CompleteMiner().enumerate(small15x4(), MinerOptions.empty());
        Check.equal(sets(set(1), set(1, 3), set(3), set(1, 2)), e.generatedRoles());
        Check.equal("[11, 8, 8, 3]", Arrays.toString(e.generatedCounts()));
        expect(e.candidates(), sets(set(1, 2), set(3), set(0, 1, 3), set(1, 2, 3), set(1), set(1, 3)),
                "BBIIGG", new int[] {3, 2, 5, 3, 0, 0}, new int[] {3, 8, 0, 0, 11, 8});
    }

    public void legacyMergeSkipsTheRoleAfterEachMerge() {
        // X={0} Y={0,1} Z={0,1,2}: X and Y are both pairwise intersections (XY=XZ=X, YZ=Y).
        UpaMatrix upa = UpaMatrix.of(new int[][] {{1, 0, 0}, {1, 1, 0}, {1, 1, 1}});
        CandidateSet fixed = new FastMiner().enumerate(upa, MinerOptions.empty()).candidates();
        expect(fixed, sets(set(0), set(0, 1), set(0, 1, 2)), "BBI", new int[] {1, 1, 1}, new int[] {3, 2, 0});
        // Legacy: after merging X, Y slides into X's slot and is skipped, so Y is listed
        // once as an initial role and once as a generated role; the seeded empty role stays.
        CandidateSet old = new FastMiner().enumerate(upa, legacy()).candidates();
        expect(old, sets(set(0), set(0, 1), set(0, 1, 2), set(), set(0, 1)), "BIIGG",
                new int[] {1, 1, 1, 0, 0}, new int[] {3, 0, 0, 0, 2});
        Check.equal(1, old.duplicateRoles());
        Check.equal(old.roles(), new FastMiner().candidateRoles(upa, MinerOptions.empty()));
        Check.equal(fixed.roles(), new FastMiner().candidateRoles(upa, MinerOptions.empty().set("legacy", "false")));

        CandidateSet complete = new CompleteMiner().enumerate(upa, MinerOptions.empty()).candidates();
        expect(complete, sets(set(0), set(0, 1), set(0, 1, 2)), "BBI", new int[] {1, 1, 1}, new int[] {2, 1, 0});
    }

    public void orderByRating() throws Exception {
        MinerOptions o = MinerOptions.empty().set("order", "rating").set("rankPriority", 10);
        MiningResult r = new FastMiner().mine(small15x4(), o);
        // ratings at p=10: Q 36, S 30, P 50, R 30, {1} 8, {1,3} 8; ties keep legacy order.
        Check.equal(sets(set(0, 1, 3), set(1, 2), set(3), set(1, 2, 3), set(1), set(1, 3)), r.roles());
        Check.equal("rating@10", r.stats().get("order"));
    }

    public void assignmentsAreExact() throws Exception {
        UpaMatrix upa = small4x7();
        MiningResult r = new CompleteMiner().mine(upa, MinerOptions.empty());
        List<BitSet> expected = new ArrayList<>();
        for (BitSet role : r.roles()) {
            BitSet users = new BitSet();
            for (int u = 0; u < upa.userCount(); u++) {
                BitSet missing = (BitSet) role.clone();
                missing.andNot(upa.row(u));
                if (missing.isEmpty() && !role.isEmpty()) users.set(u);
            }
            expected.add(users);
        }
        Check.equal(expected, r.assignments());
    }
}
