package rolemining.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/** Writes roles.csv, assignments.csv and metrics.txt into an output directory. */
public final class ResultWriter {
    private ResultWriter() {}

    public static void write(Path dir, UpaMatrix upa, MiningResult result, Map<String, Object> metrics)
            throws IOException {
        Files.createDirectories(dir);
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(dir.resolve("roles.csv"), StandardCharsets.UTF_8))) {
            w.println("role,size,permissions");
            List<BitSet> roles = result.roles();
            for (int r = 0; r < roles.size(); r++) {
                StringJoiner names = new StringJoiner(";");
                BitSet role = roles.get(r);
                for (int p = role.nextSetBit(0); p >= 0; p = role.nextSetBit(p + 1)) names.add(upa.permName(p));
                w.println((r + 1) + "," + role.cardinality() + "," + names);
            }
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(dir.resolve("assignments.csv"), StandardCharsets.UTF_8))) {
            w.println("role,userCount,users");
            List<BitSet> ua = Metrics.assignments(upa, result);
            for (int r = 0; r < ua.size(); r++) {
                StringJoiner names = new StringJoiner(";");
                BitSet users = ua.get(r);
                for (int u = users.nextSetBit(0); u >= 0; u = users.nextSetBit(u + 1)) names.add(upa.userName(u));
                w.println((r + 1) + "," + users.cardinality() + "," + names);
            }
        }
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(dir.resolve("metrics.txt"), StandardCharsets.UTF_8))) {
            metrics.forEach((k, v) -> w.println(k + "=" + v));
            result.stats().forEach((k, v) -> w.println(k + "=" + v));
        }
    }
}
