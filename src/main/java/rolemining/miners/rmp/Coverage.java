package rolemining.miners.rmp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import rolemining.core.UpaMatrix;

/**
 * The legacy "upaTilingMatrix": which (user, permission) cells the roles chosen
 * so far grant. Cells outside the UPA can be granted too (MinNoise's association
 * step does that); tiles never grant them. Mutable; one per mining run.
 */
public final class Coverage {
    private final List<BitSet> upaRows;
    private final List<BitSet> granted;
    private final int permCount;
    private final BitSet scratch = new BitSet();

    public Coverage(UpaMatrix upa) {
        this.permCount = upa.permCount();
        this.upaRows = new ArrayList<>(upa.userCount());
        this.granted = new ArrayList<>(upa.userCount());
        for (int u = 0; u < upa.userCount(); u++) {
            upaRows.add(upa.row(u));
            granted.add(new BitSet(permCount));
        }
    }

    private Coverage(Coverage other) {
        this.permCount = other.permCount;
        this.upaRows = other.upaRows; // never modified
        this.granted = new ArrayList<>(other.granted.size());
        for (BitSet b : other.granted) granted.add((BitSet) b.clone());
    }

    /** An independent copy of the current state. */
    public Coverage copy() { return new Coverage(this); }

    public int userCount() { return upaRows.size(); }

    public boolean isGranted(int user, int perm) { return granted.get(user).get(perm); }

    /**
     * Uncovered area of a tile: the cells (u, p), u in {@code users}, p in
     * {@code perms}, that are in the UPA and not granted yet.
     */
    public int uncoveredArea(BitSet perms, BitSet users) {
        int area = 0;
        for (int u = users.nextSetBit(0); u >= 0; u = users.nextSetBit(u + 1)) {
            scratch.clear();
            scratch.or(perms);
            scratch.and(upaRows.get(u));
            scratch.andNot(granted.get(u));
            area += scratch.cardinality();
        }
        return area;
    }

    /** Grants {@code perms} to every user in {@code users}. */
    public void grant(BitSet perms, BitSet users) {
        for (int u = users.nextSetBit(0); u >= 0; u = users.nextSetBit(u + 1)) granted.get(u).or(perms);
    }

    /** Grants {@code perms} to one user. */
    public void grant(BitSet perms, int user) { granted.get(user).or(perms); }

    /** UPA cells not granted (legacy: under-privilege). */
    public int underAssigned() {
        int n = 0;
        for (int u = 0; u < upaRows.size(); u++) {
            scratch.clear();
            scratch.or(upaRows.get(u));
            scratch.andNot(granted.get(u));
            n += scratch.cardinality();
        }
        return n;
    }

    /** Granted cells that are not in the UPA (legacy: over-privilege). */
    public int overAssigned() {
        int n = 0;
        for (int u = 0; u < upaRows.size(); u++) {
            scratch.clear();
            scratch.or(granted.get(u));
            scratch.andNot(upaRows.get(u));
            n += scratch.cardinality();
        }
        return n;
    }

    /** The legacy {@code delta} ({@code Updaters.updateDelta}): under- plus over-assigned cells. */
    public int delta() { return underAssigned() + overAssigned(); }

    /**
     * Number of permissions granted to at least one user
     * (legacy {@code getStaticPermissionCoverage}).
     */
    public int permissionCoverage() {
        BitSet any = new BitSet(permCount);
        for (BitSet g : granted) any.or(g);
        return any.cardinality();
    }

    /**
     * The legacy {@code getPrivilege}: per user, under = missing / (missing + held)
     * and over = extra / (extra + held), where held counts granted UPA cells;
     * each averaged over all users, as a percentage. Returns {under, over}.
     *
     * <p>Fix: the legacy code divided 0 by 0 (giving NaN for the whole average)
     * for a user with no permissions (under) or with nothing granted (over); such a
     * user now contributes 0. With no users both are 0.
     */
    public double[] privilegePercent() {
        if (upaRows.isEmpty()) return new double[] {0.0, 0.0};
        double under = 0;
        double over = 0;
        for (int u = 0; u < upaRows.size(); u++) {
            BitSet row = upaRows.get(u);
            BitSet got = granted.get(u);
            BitSet tmp = (BitSet) row.clone();
            tmp.and(got);
            int held = tmp.cardinality();
            tmp = (BitSet) row.clone();
            tmp.andNot(got);
            int missing = tmp.cardinality();
            tmp = (BitSet) got.clone();
            tmp.andNot(row);
            int extra = tmp.cardinality();
            if (missing + held > 0) under += (double) missing / (missing + held);
            if (extra + held > 0) over += (double) extra / (extra + held);
        }
        return new double[] {under / upaRows.size() * 100, over / upaRows.size() * 100};
    }

    /** Permissions held by at least one user of the UPA: the legacy "out of 161 permissions". */
    public static int heldPermissions(UpaMatrix upa) {
        BitSet any = new BitSet(upa.permCount());
        for (int u = 0; u < upa.userCount(); u++) any.or(upa.row(u));
        return any.cardinality();
    }
}
