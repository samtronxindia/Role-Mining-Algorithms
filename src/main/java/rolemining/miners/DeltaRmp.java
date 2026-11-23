package rolemining.miners;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiFunction;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.RoleMiner;
import rolemining.core.UpaMatrix;
import rolemining.miners.rmp.CandidateSources;
import rolemining.miners.rmp.Coverage;
import rolemining.miners.rmp.GreedyTiler;
import rolemining.miners.rmp.Params;
import rolemining.miners.rmp.RankedRoles;
import rolemining.miners.rmp.RoleSteps;

/**
 * delta-approx RMP (Vaidya, Atluri, Guo, SACMAT 2007), ported from the legacy
 * {@code Delta RMP Algorithm/DeltaRMP}: rank candidate roles by area, then
 * greedily add the role whose tile covers the most still-uncovered UPA cells
 * until at most {@code delta} UPA cells are left uncovered.
 *
 * <p>Users get a role exactly when they hold all its permissions, so roles never
 * grant extra permissions and delta is the number of uncovered UPA cells. See
 * NOTES-rmp.md for the parameters, statistics and differences from the legacy code.
 */
public final class DeltaRmp implements RoleMiner {
    public static final int DEFAULT_DELTA = 350;
    public static final int DEFAULT_TRACE_BELOW = 4000;
    public static final boolean DEFAULT_NON_OVERLAPPING = true;

    private final BiFunction<UpaMatrix, MinerOptions, List<BitSet>> candidateSource;

    public DeltaRmp() { this(CandidateSources.defaultSource()); }

    public DeltaRmp(BiFunction<UpaMatrix, MinerOptions, List<BitSet>> candidateSource) {
        this.candidateSource = Objects.requireNonNull(candidateSource);
    }

    @Override
    public String name() { return "deltarmp"; }

    @Override
    public String description() {
        return "delta-approx RMP: greedy tiling over candidate roles until at most delta UPA cells are uncovered."
                + " delta=" + DEFAULT_DELTA
                + "; nonOverlapping=" + DEFAULT_NON_OVERLAPPING
                + " (afterwards remove from each role the permissions of earlier roles, as the legacy code did)"
                + "; traceBelow=" + DEFAULT_TRACE_BELOW
                + " (report roles:delta after each step once delta is below this)";
    }

    @Override
    public MiningResult mine(UpaMatrix upa, MinerOptions options) {
        int target = Params.getNonNegativeInt(options, "delta", DEFAULT_DELTA);
        int traceBelow = options.getInt("traceBelow", DEFAULT_TRACE_BELOW);
        boolean nonOverlapping = Params.getBoolean(options, "nonOverlapping", DEFAULT_NON_OVERLAPPING);

        List<RankedRoles.Role> ranked = RankedRoles.rank(upa, candidateSource.apply(upa, options));
        Coverage coverage = new Coverage(upa);
        GreedyTiler tiler = new GreedyTiler(ranked);
        List<BitSet> selected = new ArrayList<>();
        StringJoiner trace = new StringJoiner(" ");
        int delta = upa.cardinality();
        while (delta > target) {
            if (delta < traceBelow) trace.add(selected.size() + ":" + delta);
            int rank = tiler.next(coverage);
            if (rank < 0) break; // candidates cannot cover any more cells
            RankedRoles.Role role = ranked.get(rank);
            delta -= coverage.uncoveredArea(role.permissions(), role.users());
            coverage.grant(role.permissions(), role.users());
            selected.add(role.permissions());
        }
        if (delta < traceBelow) trace.add(selected.size() + ":" + delta);

        List<BitSet> roles = nonOverlapping ? RoleSteps.removeEarlierPermissions(selected) : selected;
        double[] privilege = coverage.privilegePercent();
        return new MiningResult(roles)
                .stat("delta", delta)
                .stat("deltaTarget", target)
                .stat("deltaReached", delta <= target)
                .stat("rolesSelected", selected.size())
                .stat("overPrivilegePercent", privilege[1])
                .stat("underPrivilegePercent", privilege[0])
                .stat("permissionCoverage", coverage.permissionCoverage())
                .stat("permissionsHeld", Coverage.heldPermissions(upa))
                .stat("candidates", ranked.size())
                .stat("nonOverlapping", nonOverlapping)
                .stat("deltaTrace", trace.length() == 0 ? "-" : trace.toString());
    }
}
