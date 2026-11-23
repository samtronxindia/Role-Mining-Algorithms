package rolemining.miners;

import java.util.BitSet;
import java.util.List;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.RoleMiner;
import rolemining.core.UpaMatrix;
import rolemining.miners.enumeration.EnumerationResult;
import rolemining.miners.enumeration.Enumerator;
import rolemining.miners.enumeration.MinerSupport;
import rolemining.miners.enumeration.Settings;

/**
 * FastMiner (Vaidya, Atluri and Warner, CCS 2006): candidate roles are the
 * distinct user permission sets plus the intersections of every pair of them,
 * each rated by how many users it covers. Port of the legacy
 * {@code Fast Miner Algorithm/prelimnaryCalculations} program.
 *
 * <p>{@link #mine} returns the candidate roles: first the initial roles that are
 * also pairwise intersections (in initial-role order), then the other initial
 * roles (in order of first appearance among the users), then the other
 * intersections (in the order the pair loop i &lt; j found them); or, with
 * {@code order=rating}, sorted by rating. See {@link Settings} for parameters.
 */
public final class FastMiner implements RoleMiner {
    public FastMiner() {}

    @Override
    public String name() { return "fastminer"; }

    @Override
    public String description() {
        return "FastMiner (pairwise intersections of user permission sets); params: legacy=false, "
                + "minPriority=10, priorityStep=10, maxPriority=100, order=legacy|rating, rankPriority=maxPriority";
    }

    @Override
    public MiningResult mine(UpaMatrix upa, MinerOptions options) {
        Settings.checkNames(options, false);
        Settings s = Settings.parse(options, false, false);
        return MinerSupport.toResult(upa, Enumerator.run(upa, s, Enumerator.Kind.FAST), s);
    }

    /** The full enumeration (initial, generated and candidate roles with their counts). legacy defaults to false. */
    public EnumerationResult enumerate(UpaMatrix upa, MinerOptions options) {
        return Enumerator.run(upa, Settings.parse(options, false, false), Enumerator.Kind.FAST);
    }

    /**
     * The candidate roles exactly as the legacy program wrote them to its
     * {@code CandidateRoles} file: same rows, same order. Unlike {@link #mine},
     * {@code legacy} defaults to <b>true</b> here, so the result keeps the legacy
     * defects: the all-zero role is included, and initial roles skipped by the
     * legacy merge appear twice (on the 500-app data, 134 of 14606 rows repeat an
     * earlier row). Pass {@code legacy=false} for the corrected set, which equals
     * {@code mine(...).roles()} with the default order. Other options are ignored
     * (the order is always the legacy order), so callers may pass their own
     * options object.
     */
    public List<BitSet> candidateRoles(UpaMatrix upa, MinerOptions options) {
        MinerOptions own = MinerOptions.empty();
        if (options.has(Settings.LEGACY)) own.set(Settings.LEGACY, options.get(Settings.LEGACY, "true"));
        Settings s = Settings.parse(own, true, false);
        return Enumerator.run(upa, s, Enumerator.Kind.FAST).candidates().roles();
    }
}
