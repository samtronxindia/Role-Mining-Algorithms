package rolemining.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import rolemining.testing.Check;

public class MiningResultTest {
    private static BitSet set(int... bits) {
        BitSet b = new BitSet();
        for (int i : bits) b.set(i);
        return b;
    }

    public void constructorCopiesRolesAndAssignments() {
        List<BitSet> roles = new ArrayList<>(List.of(set(0, 1)));
        List<BitSet> ua = new ArrayList<>(List.of(set(2)));
        MiningResult r = new MiningResult(roles, ua);
        roles.get(0).set(5);
        roles.add(set(9));
        ua.get(0).set(7);
        Check.equal(1, r.roles().size());
        Check.equal(set(0, 1), r.roles().get(0));
        Check.equal(set(2), r.assignments().get(0));
    }

    public void returnedListsCannotBeModified() {
        MiningResult r = new MiningResult(List.of(set(0)), List.of(set(1)));
        Check.fails(UnsupportedOperationException.class, () -> r.roles().add(set(2)));
        Check.fails(UnsupportedOperationException.class, () -> r.assignments().clear());
    }

    public void assignmentsAreNullWhenNotGiven() {
        MiningResult r = new MiningResult(List.of(set(0)));
        Check.isTrue(r.assignments() == null, "assignments null");
        Check.equal(1, r.roles().size());
    }

    public void mismatchedAssignmentCountIsRejected() {
        Check.fails(IllegalArgumentException.class,
                () -> new MiningResult(List.of(set(0), set(1)), List.of(set(0))));
        Check.fails(IllegalArgumentException.class,
                () -> new MiningResult(List.of(set(0)), List.of()));
    }

    public void statsKeepInsertionOrder() {
        MiningResult r = new MiningResult(List.of()).stat("zeta", 1).stat("alpha", "x");
        Check.equal(List.of("zeta", "alpha"), new ArrayList<>(r.stats().keySet()));
        Check.equal(1, r.stats().get("zeta"));
    }
}
