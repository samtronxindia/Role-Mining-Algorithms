package rolemining.miners;

import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.RoleMiner;
import rolemining.core.UpaMatrix;
import rolemining.miners.enumeration.EnumerationResult;
import rolemining.miners.enumeration.Enumerator;
import rolemining.miners.enumeration.MinerSupport;
import rolemining.miners.enumeration.Settings;

/**
 * CompleteMiner (Vaidya, Atluri and Warner, CCS 2006): candidate roles are the
 * distinct user permission sets plus the intersections of every subset of two or
 * more of them, each rated by how many users it covers. Port of the legacy
 * {@code Complete Miner Algorithm/completeMiner.java}.
 *
 * <p>The number of intersections can grow exponentially with the number of
 * distinct users; {@code maxGeneratedRoles} (default 1000000, 0 = unlimited)
 * stops the run with an error instead of exhausting memory. On the 500-app data
 * there are 240904 non-empty intersections.
 *
 * <p>{@link #mine} returns the candidate roles in legacy order: initial roles that
 * are also intersections (in initial-role order), the other initial roles, then
 * the other intersections in enumeration order; or, with {@code order=rating},
 * sorted by rating. See {@link Settings} for parameters.
 */
public final class CompleteMiner implements RoleMiner {
    public CompleteMiner() {}

    @Override
    public String name() { return "completeminer"; }

    @Override
    public String description() {
        return "CompleteMiner (intersections of all subsets of user permission sets); params: legacy=false, "
                + "minPriority=10, priorityStep=10, maxPriority=100, order=legacy|rating, rankPriority=maxPriority, "
                + "maxGeneratedRoles=1000000";
    }

    @Override
    public MiningResult mine(UpaMatrix upa, MinerOptions options) {
        Settings.checkNames(options, true);
        Settings s = Settings.parse(options, false, true);
        return MinerSupport.toResult(upa, Enumerator.run(upa, s, Enumerator.Kind.COMPLETE), s);
    }

    /** The full enumeration (initial, generated and candidate roles with their counts). legacy defaults to false. */
    public EnumerationResult enumerate(UpaMatrix upa, MinerOptions options) {
        return Enumerator.run(upa, Settings.parse(options, false, true), Enumerator.Kind.COMPLETE);
    }
}
