package rolemining.miners;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
 * MinNoise RMP (Vaidya, Atluri, Guo, SACMAT 2007), ported from the legacy
 * {@code MinNoise RMP Algorithm/MinNoiseRMP}: add a single-permission role for
 * every permission to the candidates, rank them by area, greedily pick at most
 * {@code roles} roles by uncovered area, then also give each role to every user
 * holding more than {@code associateThreshold} of its permissions. The result's
 * noise (delta) counts both missing and extra permissions.
 *
 * <p>See docs/rmp-miners.md for the parameters, statistics and differences from the
 * legacy code.
 */
public final class MinNoiseRmp implements RoleMiner {
    public static final int DEFAULT_ROLES = 10;
    public static final double DEFAULT_ASSOCIATE_THRESHOLD = 0.5;
    public static final String DEFAULT_INTERIM_ASSOCIATIONS = "120,130";
    public static final int DEFAULT_ROLE_CAP = 134;
    public static final boolean DEFAULT_REFRESH_AFTER_ASSOCIATION = true;

    private final BiFunction<UpaMatrix, MinerOptions, List<BitSet>> candidateSource;

    public MinNoiseRmp() { this(CandidateSources.defaultSource()); }

    public MinNoiseRmp(BiFunction<UpaMatrix, MinerOptions, List<BitSet>> candidateSource) {
        this.candidateSource = Objects.requireNonNull(candidateSource);
    }

    @Override
    public String name() { return "minnoise"; }

    @Override
    public String description() {
        return "MinNoise RMP: greedy tiling with at most `roles` roles, then extra role assignments."
                + " roles=" + DEFAULT_ROLES
                + "; associateThreshold=" + DEFAULT_ASSOCIATE_THRESHOLD
                + " (also give a role to users holding more than this fraction of it; 1 disables)"
                + "; interimAssociations=" + DEFAULT_INTERIM_ASSOCIATIONS
                + " (role counts at which that step also runs during selection; none disables)"
                + "; roleCap=" + DEFAULT_ROLE_CAP + " (legacy hard limit on roles; 0 disables)"
                + "; refreshAfterAssociation=" + DEFAULT_REFRESH_AFTER_ASSOCIATION
                + " (false: legacy pick on stale areas after an interim step)";
    }

    @Override
    public MiningResult mine(UpaMatrix upa, MinerOptions options) {
        int requested = Params.getNonNegativeInt(options, "roles", DEFAULT_ROLES);
        double threshold = options.getDouble("associateThreshold", DEFAULT_ASSOCIATE_THRESHOLD);
        if (!(threshold >= 0)) throw new IllegalArgumentException("parameter associateThreshold must be >= 0");
        Set<Integer> interim = Params.getIntSet(options, "interimAssociations", DEFAULT_INTERIM_ASSOCIATIONS);
        int cap = Params.getNonNegativeInt(options, "roleCap", DEFAULT_ROLE_CAP);
        boolean refresh = Params.getBoolean(options, "refreshAfterAssociation", DEFAULT_REFRESH_AFTER_ASSOCIATION);
        int limit = cap > 0 ? Math.min(requested, cap) : requested;

        List<BitSet> candidates = new ArrayList<>(candidateSource.apply(upa, options));
        int singletons = RoleSteps.addSingletons(candidates, upa.permCount());
        List<RankedRoles.Role> ranked = RankedRoles.rank(upa, candidates);

        Coverage coverage = new Coverage(upa);
        GreedyTiler tiler = new GreedyTiler(ranked);
        List<BitSet> roles = new ArrayList<>();
        List<BitSet> users = new ArrayList<>();
        int interimRuns = 0;
        int tileAssignments = 0;
        while (roles.size() < limit) {
            Coverage pickWith = coverage;
            if (interim.contains(roles.size())) {
                if (!refresh) pickWith = coverage.copy(); // legacy: areas computed before the step
                RoleSteps.associate(upa, roles, users, coverage, threshold);
                interimRuns++;
            }
            if (coverage.delta() < 1) break;
            int rank = tiler.next(pickWith);
            if (rank < 0) break; // nothing left uncovered that a candidate could cover
            RankedRoles.Role role = ranked.get(rank);
            coverage.grant(role.permissions(), role.users());
            roles.add(role.permissions());
            users.add((BitSet) role.users().clone());
            tileAssignments += role.users().cardinality();
        }
        int deltaBeforeAssociation = coverage.delta();
        RoleSteps.associate(upa, roles, users, coverage, threshold);
        int extra = -tileAssignments;
        for (BitSet u : users) extra += u.cardinality();

        double[] privilege = coverage.privilegePercent();
        BitSet rolePerms = new BitSet();
        for (BitSet r : roles) rolePerms.or(r);
        return new MiningResult(roles, users)
                .stat("delta", coverage.delta())
                .stat("deltaBeforeAssociation", deltaBeforeAssociation)
                .stat("overPrivilegePercent", privilege[1])
                .stat("underPrivilegePercent", privilege[0])
                .stat("permissionCoverage", coverage.permissionCoverage())
                .stat("rolePermissions", rolePerms.cardinality())
                .stat("permissionsHeld", Coverage.heldPermissions(upa))
                .stat("rolesRequested", requested)
                .stat("associatedAssignments", extra)
                .stat("interimAssociationRuns", interimRuns)
                .stat("candidates", ranked.size())
                .stat("singletonCandidatesAdded", singletons);
    }
}
