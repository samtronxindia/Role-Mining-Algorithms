package rolemining.miners.rmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import rolemining.core.UpaMatrix;

/** Helpers shared by the tests in this package. */
final class Fixtures {
    private Fixtures() {}

    static final Path ANDROID_UPA = Path.of("data/android-500/upa.csv");
    static final Path ANDROID_PERMS = Path.of("data/android-500/permissions.csv");
    static final Path SMALL_4X7 = Path.of("src/test/resources/fixtures/small-4x7.csv");
    static final Path RESOURCES = Path.of("src/test/resources/rmp");

    static BitSet bits(int... indices) {
        BitSet b = new BitSet();
        for (int i : indices) b.set(i);
        return b;
    }

    static List<BitSet> list(BitSet... sets) { return List.of(sets); }

    static UpaMatrix android() throws IOException {
        return UpaMatrix.load(ANDROID_UPA, ANDROID_PERMS, null);
    }

    static UpaMatrix random(Random rnd, int users, int perms, double density) {
        int[][] cells = new int[users][perms];
        for (int u = 0; u < users; u++) {
            for (int p = 0; p < perms; p++) cells[u][p] = rnd.nextDouble() < density ? 1 : 0;
        }
        return UpaMatrix.of(cells);
    }

    /** One role per line, permission names separated by commas (the legacy PA files). */
    static List<BitSet> loadRoleNames(Path file, UpaMatrix upa) throws IOException {
        Map<String, Integer> index = new HashMap<>();
        for (int p = 0; p < upa.permCount(); p++) index.put(upa.permName(p), p);
        List<BitSet> roles = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) continue;
            BitSet b = new BitSet();
            for (String t : line.split(",")) {
                String s = t.strip();
                if (s.isEmpty()) continue;
                Integer p = index.get(s);
                if (p == null) throw new IllegalArgumentException(file + ": unknown permission " + s);
                b.set(p);
            }
            roles.add(b);
        }
        return roles;
    }
}
