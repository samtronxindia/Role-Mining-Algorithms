package rolemining.miners;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.RoleMiner;
import rolemining.core.UpaMatrix;
import rolemining.miners.rmp.Coverage;
import rolemining.miners.rmp.PrefixTileMiner;

/**
 * Basic RMP (Vaidya, Atluri, Guo, SACMAT 2007), ported from the legacy
 * {@code Basic RMP Algorithm/BasicRMP}: cover the UPA exactly with tiles, each
 * time taking the largest uncovered tile the legacy tile search finds (contiguous
 * permission ranges starting at the first uncovered permission, see
 * {@link PrefixTileMiner}), until every UPA cell is covered. Each tile is a role
 * (PA) together with the users that get it (UA). No parameters; no candidates.
 */
public final class BasicRmp implements RoleMiner {
    public BasicRmp() {}

    @Override
    public String name() { return "basicrmp"; }

    @Override
    public String description() {
        return "Basic RMP: exact greedy tiling of the UPA, largest uncovered tile first, over the legacy"
                + " tile search (contiguous permission ranges from the first uncovered permission). No parameters.";
    }

    @Override
    public MiningResult mine(UpaMatrix upa, MinerOptions options) {
        Coverage coverage = new Coverage(upa);
        PrefixTileMiner tiles = new PrefixTileMiner(upa);
        List<BitSet> roles = new ArrayList<>();
        List<BitSet> users = new ArrayList<>();
        PrefixTileMiner.Tile tile;
        while ((tile = tiles.next(coverage)) != null) {
            coverage.grant(tile.permissions(), tile.users());
            roles.add(tile.permissions());
            users.add(tile.users());
        }
        return new MiningResult(roles, users); // distance (always 0) is in the core metrics
    }
}
