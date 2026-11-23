package rolemining.miners.rmp;

import java.util.BitSet;
import java.util.List;
import rolemining.core.UpaMatrix;

/**
 * The tile search of the legacy Basic RMP ({@code AlgotithmLUTM.LUTM}), as it
 * actually behaves.
 *
 * <p>Let f be the first permission (by column index) that some user holds but has
 * not been granted yet. The legacy code only considers tiles whose permissions are
 * a contiguous column range f..e: the tile is every user holding all of f..e, and
 * its uncovered area counts the cells of that tile not granted yet. It returns the
 * range with the largest uncovered area, the shortest one on ties.
 *
 * <p>The legacy loop also built "conditional databases", but never fed one into
 * the next step (only {@code nextConditionalUPA} is written, from the first
 * {@code conditionalUPA}), so they only decide when to stop extending the range.
 * It stops when no user holds f, e and any later permission, which happens no
 * earlier than the range's tile becoming empty, so stopping as soon as the tile
 * is empty selects the same tile. This is much narrower than the Largest Tile
 * Mining search of Geerts et al. that the paper relies on, which considers any
 * permission set; it still always finds a tile with positive uncovered area, so
 * Basic RMP still reconstructs the UPA exactly.
 */
public final class PrefixTileMiner {
    private final UpaMatrix upa;
    private final List<BitSet> columns;

    /** A tile: permissions f..end and the users holding all of them. */
    public record Tile(BitSet permissions, BitSet users, int uncoveredArea) {}

    public PrefixTileMiner(UpaMatrix upa) {
        this.upa = upa;
        this.columns = RankedRoles.columns(upa);
    }

    /** First permission with a UPA cell not yet granted, or -1 when everything is covered. */
    public int firstUncoveredPermission(Coverage coverage) {
        for (int p = 0; p < upa.permCount(); p++) {
            BitSet holders = columns.get(p);
            for (int u = holders.nextSetBit(0); u >= 0; u = holders.nextSetBit(u + 1)) {
                if (!coverage.isGranted(u, p)) return p;
            }
        }
        return -1;
    }

    /** The largest uncovered tile starting at the first uncovered permission, or null if none is left. */
    public Tile next(Coverage coverage) {
        int first = firstUncoveredPermission(coverage);
        if (first < 0) return null;
        int[] uncoveredInRange = new int[upa.userCount()];
        BitSet users = (BitSet) columns.get(first).clone();
        int bestArea = 0;
        int bestEnd = -1;
        BitSet bestUsers = null;
        for (int end = first; end < upa.permCount(); end++) {
            if (end > first) users.and(columns.get(end));
            if (users.isEmpty()) break;
            int area = 0;
            for (int u = users.nextSetBit(0); u >= 0; u = users.nextSetBit(u + 1)) {
                if (!coverage.isGranted(u, end)) uncoveredInRange[u]++;
                area += uncoveredInRange[u];
            }
            if (area > bestArea) {
                bestArea = area;
                bestEnd = end;
                bestUsers = (BitSet) users.clone();
            }
        }
        BitSet perms = new BitSet(upa.permCount());
        perms.set(first, bestEnd + 1);
        return new Tile(perms, bestUsers, bestArea);
    }
}
