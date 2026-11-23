package rolemining.miners.rmp;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import rolemining.core.MinerOptions;
import rolemining.core.UpaMatrix;

/**
 * Where the candidate-based miners (Delta RMP, MinNoise RMP, Basic RMP with
 * tiles=candidates) get their candidate roles from.
 *
 * <p>The legacy code took its candidates from the output file of the legacy
 * FastMiner. That miner is ported separately; until it is wired in here,
 * {@link #defaultSource()} uses {@link #rowsAndPairwiseIntersections}, which
 * generates the same set of roles (see that method for how the order differs).
 */
public final class CandidateSources {
    private CandidateSources() {}

    /**
     * THE default candidate source used by the no-argument constructors of
     * {@code BasicRmp}, {@code DeltaRmp} and {@code MinNoiseRmp}. This is the one
     * place to change when the ported FastMiner becomes available.
     */
    public static BiFunction<UpaMatrix, MinerOptions, List<BitSet>> defaultSource() {
        return CandidateSources::rowsAndPairwiseIntersections;
    }

    /**
     * The candidate set of FastMiner (Vaidya, Atluri, Warner 2006): every distinct
     * user row, then every distinct non-empty intersection of two distinct rows
     * that is not already in the list.
     *
     * <p>Order: distinct rows in order of first occurrence, then intersections in
     * the order of the row pairs (i, j), i &lt; j. The legacy FastMiner
     * ({@code CalcCandidateRolesAndRatings.calcRating}) writes the same set plus
     * the empty role, but in a different order: rows that are also an intersection
     * of two rows first, then the other rows, then the remaining intersections, and
     * with some duplicates (an index skip after {@code iR.remove(i)}). The order
     * only matters for breaking ties between candidates of equal area.
     */
    public static List<BitSet> rowsAndPairwiseIntersections(UpaMatrix upa, MinerOptions options) {
        Set<BitSet> distinct = new LinkedHashSet<>();
        for (int u = 0; u < upa.userCount(); u++) distinct.add(upa.row(u));
        List<BitSet> rows = new ArrayList<>(distinct);
        List<BitSet> out = new ArrayList<>(rows);
        Set<BitSet> seen = new HashSet<>(rows);
        for (int i = 0; i < rows.size(); i++) {
            for (int j = i + 1; j < rows.size(); j++) {
                BitSet both = (BitSet) rows.get(i).clone();
                both.and(rows.get(j));
                if (!both.isEmpty() && seen.add(both)) out.add(both);
            }
        }
        out.removeIf(BitSet::isEmpty);
        return out;
    }

    /** A source that ignores its input and always returns copies of {@code candidates}, in order. */
    public static BiFunction<UpaMatrix, MinerOptions, List<BitSet>> fixed(List<BitSet> candidates) {
        List<BitSet> copy = new ArrayList<>();
        for (BitSet b : candidates) copy.add((BitSet) b.clone());
        return (upa, options) -> {
            List<BitSet> out = new ArrayList<>(copy.size());
            for (BitSet b : copy) out.add((BitSet) b.clone());
            return out;
        };
    }
}
