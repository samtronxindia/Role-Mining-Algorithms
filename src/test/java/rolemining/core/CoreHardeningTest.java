package rolemining.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import rolemining.testing.Check;

public class CoreHardeningTest {
    private static BitSet bits(int... set) {
        BitSet b = new BitSet();
        for (int i : set) b.set(i);
        return b;
    }

    public void emptyValueInsideRowIsRejected() throws Exception {
        Path f = Files.createTempFile("upa", ".csv");
        Files.writeString(f, "1,0,1\n1,,0\n");
        try {
            UpaMatrix.load(f);
            throw new AssertionError("expected IOException");
        } catch (IOException e) {
            Check.isTrue(e.getMessage().contains(":2:"), "message should carry line 2: " + e.getMessage());
        } finally {
            Files.delete(f);
        }
    }

    public void trailingCommasStillAllowed() throws Exception {
        Path f = Files.createTempFile("upa", ".csv");
        Files.writeString(f, "1,0,1,\n0,0,1,,\n");
        try {
            UpaMatrix upa = UpaMatrix.load(f);
            Check.equal(2, upa.userCount());
            Check.equal(3, upa.permCount());
        } finally {
            Files.delete(f);
        }
    }

    public void resultCannotBeChangedThroughGetters() {
        MiningResult r = new MiningResult(List.of(bits(0, 1)), List.of(bits(0)));
        r.roles().get(0).set(5);
        r.assignments().get(0).set(5);
        Check.equal(bits(0, 1), r.roles().get(0));
        Check.equal(bits(0), r.assignments().get(0));
    }

    public void metricsRejectOutOfRangeIndices() {
        UpaMatrix upa = UpaMatrix.of(new int[][] {{1, 1}, {0, 1}});
        Check.fails(IllegalArgumentException.class,
                () -> Metrics.compute(upa, new MiningResult(List.of(bits(0, 2)))));
        Check.fails(IllegalArgumentException.class,
                () -> Metrics.compute(upa, new MiningResult(List.of(bits(0)), List.of(bits(2)))));
    }

    public void listFieldsAreQuotedWhenNeeded() {
        Check.equal("a;b", ResultWriter.quote("a;b"));
        Check.equal("\"a, Inc;b\"", ResultWriter.quote("a, Inc;b"));
        Check.equal("\"say \"\"hi\"\"\"", ResultWriter.quote("say \"hi\""));
    }
}
