package rolemining.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import rolemining.testing.Check;

/**
 * All cases use the same 2-user, 3-permission matrix:
 * user 0 holds {0,1}, user 1 holds {1,2}; 4 cells are set.
 */
public class MetricsTest {
    private static UpaMatrix upa() {
        return UpaMatrix.of(new int[][] {{1, 1, 0}, {0, 1, 1}});
    }

    private static BitSet set(int... bits) {
        BitSet b = new BitSet();
        for (int i : bits) b.set(i);
        return b;
    }

    private static void check(Map<String, Object> m, int roles, int covered, int under, int over, int used) {
        Check.equal(2, m.get("users"), "users");
        Check.equal(3, m.get("permissions"), "permissions");
        Check.equal(4, m.get("upaCells"), "upaCells");
        Check.equal(roles, m.get("roles"), "roles");
        Check.equal(covered, m.get("coveredCells"), "coveredCells");
        Check.equal(under, m.get("underAssigned"), "underAssigned");
        Check.equal(over, m.get("overAssigned"), "overAssigned");
        Check.equal(under + over, m.get("distance"), "distance");
        Check.equal(used, m.get("permissionsUsed"), "permissionsUsed");
    }

    public void exactCoverHasDistanceZero() {
        MiningResult r = new MiningResult(List.of(set(0, 1), set(1, 2)), List.of(set(0), set(1)));
        check(Metrics.compute(upa(), r), 2, 4, 0, 0, 3);
    }

    public void underAssignmentCountsMissingCells() {
        // role {0,1} is given to user 0 only; user 1's two permissions are not granted.
        MiningResult r = new MiningResult(List.of(set(0, 1)), List.of(set(0)));
        check(Metrics.compute(upa(), r), 1, 2, 2, 0, 2);
    }

    public void overAssignmentCountsExtraCells() {
        // role {0,1,2} given to both users: user 0 gains permission 2, user 1 gains permission 0.
        MiningResult r = new MiningResult(List.of(set(0, 1, 2)), List.of(set(0, 1)));
        check(Metrics.compute(upa(), r), 1, 4, 0, 2, 3);
    }

    public void overAndUnderTogether() {
        // role {0,2} given to user 0: user 0 gets 2 (over) and misses 1 (under); user 1 misses {1,2}.
        MiningResult r = new MiningResult(List.of(set(0, 2)), List.of(set(0)));
        // user 0: rebuilt {0,2}; covered {0}; under {1}; over {2}. user 1: rebuilt {}; under {1,2}.
        check(Metrics.compute(upa(), r), 1, 1, 3, 1, 2);
    }

    public void derivedAssignmentGivesRoleToEveryUserWhoseRowContainsIt() {
        MiningResult r = new MiningResult(List.of(set(0, 1), set(1, 2), set(1)));
        List<BitSet> ua = Metrics.assignments(upa(), r);
        Check.equal(set(0), ua.get(0));
        Check.equal(set(1), ua.get(1));
        Check.equal(set(0, 1), ua.get(2));
        // roles {0,1} and {1,2} cover everything exactly; {1} adds nothing new.
        check(Metrics.compute(upa(), r), 3, 4, 0, 0, 3);
    }

    public void derivedAssignmentNeverOverAssigns() {
        // {1} alone: both users hold it, user 0 and user 1 each lose one other permission.
        MiningResult r = new MiningResult(List.of(set(1)));
        check(Metrics.compute(upa(), r), 1, 2, 2, 0, 1);
    }

    public void derivedAssignmentLeavesUnmatchedRoleWithoutUsers() {
        // {0,1,2} is not contained in either user's row, so nobody gets it.
        MiningResult r = new MiningResult(List.of(set(0, 1, 2)));
        Check.equal(0, Metrics.assignments(upa(), r).get(0).cardinality());
        check(Metrics.compute(upa(), r), 1, 0, 4, 0, 3);
    }

    public void explicitAssignmentsAreUsedAsGiven() {
        List<BitSet> given = List.of(set(1));
        MiningResult r = new MiningResult(List.of(set(0, 1)), given);
        Check.equal(set(1), Metrics.assignments(upa(), r).get(0));
    }

    public void emptyRoleListCoversNothing() {
        MiningResult r = new MiningResult(new ArrayList<>());
        check(Metrics.compute(upa(), r), 0, 0, 4, 0, 0);
        Check.equal(0, Metrics.assignments(upa(), r).size());
    }

    public void emptyRoleGrantsNothingAndIsHeldByNoOneWhenDerived() {
        MiningResult r = new MiningResult(List.of(new BitSet()));
        Check.equal(0, Metrics.assignments(upa(), r).get(0).cardinality());
        check(Metrics.compute(upa(), r), 1, 0, 4, 0, 0);
    }

    public void emptyRoleWithExplicitAssignmentChangesNothing() {
        MiningResult r = new MiningResult(List.of(new BitSet()), List.of(set(0, 1)));
        check(Metrics.compute(upa(), r), 1, 0, 4, 0, 0);
    }

    public void reconstructRebuildsUnionOfRoles() {
        MiningResult r = new MiningResult(List.of(set(0), set(2)), List.of(set(0, 1), set(1)));
        List<BitSet> rows = Metrics.reconstruct(upa(), r);
        Check.equal(set(0), rows.get(0));
        Check.equal(set(0, 2), rows.get(1));
    }

    public void permissionsUsedCountsDistinctPermissions() {
        MiningResult r = new MiningResult(List.of(set(0), set(0, 1)), List.of(set(), set()));
        Check.equal(2, Metrics.compute(upa(), r).get("permissionsUsed"));
    }
}
