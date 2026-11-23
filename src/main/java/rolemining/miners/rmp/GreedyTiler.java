package rolemining.miners.rmp;

import java.util.List;
import java.util.PriorityQueue;

/**
 * The greedy step shared by the legacy Delta RMP and MinNoise RMP
 * ({@code calcFinalCR} with {@code updateUncoveredArea}): repeatedly take the
 * ranked role with the largest uncovered area, ties going to the role ranked
 * first, then grant its tile.
 *
 * <p>The legacy code recomputed the uncovered area of every candidate after each
 * selection. Uncovered areas can only shrink as more cells are granted, so this
 * class keeps the last computed area of each candidate as an upper bound in a
 * priority queue and recomputes only the candidate at the top ("lazy greedy").
 * When the recomputed area equals the bound, no other candidate can beat it, and
 * a candidate with an equal bound ranked earlier would have been at the top
 * instead, so the selection, ties included, is the same as the legacy full scan.
 * That requires the coverage passed to {@link #next} never to lose granted cells.
 */
public final class GreedyTiler {
    private final List<RankedRoles.Role> ranked;
    private final boolean[] taken;
    // entries: {bound, rank}; largest bound first, then smallest rank
    private final PriorityQueue<int[]> queue = new PriorityQueue<>((a, b) ->
            a[0] != b[0] ? Integer.compare(b[0], a[0]) : Integer.compare(a[1], b[1]));

    public GreedyTiler(List<RankedRoles.Role> ranked) {
        this.ranked = ranked;
        this.taken = new boolean[ranked.size()];
        for (int i = 0; i < ranked.size(); i++) queue.add(new int[] {ranked.get(i).area(), i});
    }

    /**
     * Rank of the not yet selected role with the largest uncovered area under
     * {@code coverage}, now marked selected; or -1 if no role has uncovered area
     * left. The caller grants the role's tile.
     *
     * <p>Fix: in that last case the legacy loop (whose search started at
     * {@code maxArea = 0, maxAreaIndex = 0}) added the top-ranked role again.
     */
    public int next(Coverage coverage) {
        while (!queue.isEmpty()) {
            int[] top = queue.poll();
            int rank = top[1];
            if (taken[rank]) continue;
            RankedRoles.Role role = ranked.get(rank);
            int area = coverage.uncoveredArea(role.permissions(), role.users());
            if (area == 0) continue; // stays 0 from now on
            if (area == top[0]) {
                taken[rank] = true;
                return rank;
            }
            queue.add(new int[] {area, rank});
        }
        return -1;
    }
}
