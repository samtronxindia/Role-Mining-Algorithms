package rolemining.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rolemining.testing.Check;

public class ResultWriterTest {
    private static BitSet set(int... bits) {
        BitSet b = new BitSet();
        for (int i : bits) b.set(i);
        return b;
    }

    private static Path temp(String text) throws IOException {
        Path p = Files.createTempFile("rw-test", ".txt");
        p.toFile().deleteOnExit();
        Files.writeString(p, text, StandardCharsets.UTF_8);
        return p;
    }

    private static UpaMatrix upa() throws IOException {
        return UpaMatrix.load(temp("1,1,0\n0,1,1\n"), temp("read\nwrite\nexec\n"), temp("alice\nbob\n"));
    }

    public void writesThreeFilesWithExpectedContents() throws Exception {
        Path dir = Files.createTempDirectory("rw-out").resolve("nested");
        UpaMatrix upa = upa();
        MiningResult result = new MiningResult(List.of(set(0, 1), set(1, 2)), List.of(set(0), set(1)))
                .stat("iterations", 3);
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("roles", 2);
        metrics.put("distance", 0);
        ResultWriter.write(dir, upa, result, metrics);

        Check.equal(List.of("role,size,permissions", "1,2,read;write", "2,2,write;exec"),
                Files.readAllLines(dir.resolve("roles.csv")));
        Check.equal(List.of("role,userCount,users", "1,1,alice", "2,1,bob"),
                Files.readAllLines(dir.resolve("assignments.csv")));
        Check.equal(List.of("roles=2", "distance=0", "iterations=3"),
                Files.readAllLines(dir.resolve("metrics.txt")));
    }

    public void derivedAssignmentsAreWrittenWhenResultHasNone() throws Exception {
        Path dir = Files.createTempDirectory("rw-out");
        MiningResult result = new MiningResult(List.of(set(1), set(0, 1, 2)));
        ResultWriter.write(dir, upa(), result, Map.of());
        Check.equal(List.of("role,userCount,users", "1,2,alice;bob", "2,0,"),
                Files.readAllLines(dir.resolve("assignments.csv")));
        Check.equal(List.of("role,size,permissions", "1,1,write", "2,3,read;write;exec"),
                Files.readAllLines(dir.resolve("roles.csv")));
        Check.equal(List.of(), Files.readAllLines(dir.resolve("metrics.txt")));
    }

    public void emptyRoleListWritesHeadersOnly() throws Exception {
        Path dir = Files.createTempDirectory("rw-out");
        ResultWriter.write(dir, upa(), new MiningResult(List.of()), Map.of());
        Check.equal(List.of("role,size,permissions"), Files.readAllLines(dir.resolve("roles.csv")));
        Check.equal(List.of("role,userCount,users"), Files.readAllLines(dir.resolve("assignments.csv")));
    }
}
