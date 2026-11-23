package rolemining.miners.enumeration;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;
import rolemining.miners.CompleteMiner;
import rolemining.miners.FastMiner;
import rolemining.testing.Check;

/**
 * Byte-for-byte parity with the legacy outputs on the 500-app dataset, via the
 * SHA-256 digests in src/test/resources/enumeration/legacy-500.properties.
 */
public class LegacyParityTest {
    private static UpaMatrix android500() throws Exception {
        return UpaMatrix.load(Path.of("data/android-500/upa.csv"),
                Path.of("data/android-500/permissions.csv"), Path.of("data/android-500/apps.txt"));
    }

    private static Properties expected() throws Exception {
        Properties p = new Properties();
        try (Reader r = Files.newBufferedReader(Path.of("src/test/resources/enumeration/legacy-500.properties"))) {
            p.load(r);
        }
        return p;
    }

    private static MinerOptions legacy() {
        return MinerOptions.empty().set("legacy", true);
    }

    public void fastMinerCandidateRolesFileIsReproducedExactly() throws Exception {
        UpaMatrix upa = android500();
        List<BitSet> cr = new FastMiner().candidateRoles(upa, MinerOptions.empty());
        Check.equal(14606, cr.size(), "candidate rows");
        Check.equal(expected().getProperty("fast.CandidateRoles"),
                LegacyFormat.digest(LegacyFormat.candidateRoles(cr, upa.permCount())), "CandidateRoles");
        // Passing someone else's options does not change the result.
        List<BitSet> again = new FastMiner().candidateRoles(upa, MinerOptions.empty().set("delta", 350));
        Check.equal(cr, again, "options of another miner are ignored");
    }

    public void fastMinerLegacyOutputsMatch() throws Exception {
        UpaMatrix upa = android500();
        Properties want = expected();
        EnumerationResult e = new FastMiner().enumerate(upa, legacy());
        Check.equal(want.getProperty("fast.output"), LegacyFormat.digest(LegacyFormat.output(upa, e, false)), "output");
        Check.equal(want.getProperty("fast.ratings"), LegacyFormat.digest(LegacyFormat.ratings(e.candidates())), "ratings");
        Check.equal(want.getProperty("fast.PermCR"),
                LegacyFormat.digest(LegacyFormat.permCr(upa, e.candidates().roles())), "PermCR");
        Check.equal(417, e.initialRoles().size(), "IR SIZE");
        Check.equal(14371, e.generatedRoles().size(), "GR SIZE");
        Check.equal(182, e.mergedRoles(), "commonElements");
        Check.equal(134, e.candidates().duplicateRoles(), "duplicate rows in CandidateRoles");
    }

    public void completeMinerLegacyOutputsMatch() throws Exception {
        UpaMatrix upa = android500();
        Properties want = expected();
        EnumerationResult e = new CompleteMiner().enumerate(upa, legacy());
        List<BitSet> cr = e.candidates().roles();
        Check.equal(want.getProperty("complete.output"), LegacyFormat.digest(LegacyFormat.output(upa, e, true)), "output");
        Check.equal(want.getProperty("complete.ratings"), LegacyFormat.digest(LegacyFormat.ratings(e.candidates())), "ratings");
        Check.equal(want.getProperty("complete.IRPerms"),
                LegacyFormat.digest(LegacyFormat.irPerms(upa, e.initialRoles())), "IRPerms");
        Check.equal(want.getProperty("complete.NumPermCR"), LegacyFormat.digest(LegacyFormat.numPermCr(cr)), "NumPermCR");
        Check.equal(want.getProperty("complete.CRVertical"),
                LegacyFormat.digest(LegacyFormat.crVertical(cr, upa.permCount())), "CRVertical");
        Check.equal(240905, e.generatedRoles().size(), "GR SIZE");
        Check.equal(241140, cr.size(), "Size of rating");
    }

    public void correctedResultsOnAndroid500() throws Exception {
        UpaMatrix upa = android500();
        MiningResult fast = new FastMiner().mine(upa, MinerOptions.empty());
        Check.equal(14370, fast.stats().get("generatedRoles"));
        Check.equal(316, fast.stats().get("mergedRoles"));
        Check.equal(14471, fast.roles().size());
        Check.equal(0, fast.stats().get("duplicateCandidates"));
        Check.equal(fast.roles(), new FastMiner().candidateRoles(upa, MinerOptions.empty().set("legacy", false)));
        // The corrected set is the legacy file's rows without the duplicates and the empty row.
        List<BitSet> legacyRows = new FastMiner().candidateRoles(upa, MinerOptions.empty());
        HashSet<BitSet> legacySet = new HashSet<>(legacyRows);
        legacySet.remove(new BitSet());
        Check.equal(legacySet, new HashSet<>(fast.roles()), "same distinct non-empty roles");

        MiningResult complete = new CompleteMiner().mine(upa, MinerOptions.empty());
        Check.equal(240904, complete.stats().get("generatedRoles"));
        Check.equal(241005, complete.roles().size());
        Check.equal(0, complete.stats().get("duplicateCandidates"));
        HashSet<BitSet> legacyComplete = new HashSet<>(new CompleteMiner().enumerate(upa, legacy()).candidates().roles());
        legacyComplete.remove(new BitSet());
        Check.equal(legacyComplete, new HashSet<>(complete.roles()), "same distinct non-empty roles");
    }
}
