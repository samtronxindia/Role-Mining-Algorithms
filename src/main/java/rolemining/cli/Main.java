package rolemining.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import rolemining.core.Metrics;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.ResultWriter;
import rolemining.core.RoleMiner;
import rolemining.core.UpaMatrix;

/** Command line entry point: rolemine --algo NAME --input FILE [options]. */
public final class Main {
    private Main() {}

    public static void main(String[] args) {
        try {
            System.exit(run(args));
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("error: " + e.getMessage());
            System.exit(2);
        }
    }

    static int run(String[] args) throws IOException {
        Map<String, RoleMiner> miners = Miners.all();
        String algo = null;
        Path input = null;
        Path perms = null;
        Path users = null;
        Path out = null;
        boolean withAssignments = true;
        MinerOptions options = MinerOptions.empty();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            switch (a) {
                case "-h", "--help" -> { usage(miners); return 0; }
                case "--list" -> { miners.values().forEach(m -> System.out.println(m.name())); return 0; }
                case "--algo" -> algo = value(args, ++i, a);
                case "--input" -> input = Path.of(value(args, ++i, a));
                case "--perms" -> perms = Path.of(value(args, ++i, a));
                case "--users" -> users = Path.of(value(args, ++i, a));
                case "--out" -> out = Path.of(value(args, ++i, a));
                case "--no-assignments" -> withAssignments = false;
                case "--param" -> {
                    String kv = value(args, ++i, a);
                    int eq = kv.indexOf('=');
                    if (eq < 1) throw new IllegalArgumentException("--param needs name=value, got '" + kv + "'");
                    options.set(kv.substring(0, eq), kv.substring(eq + 1));
                }
                default -> throw new IllegalArgumentException("unknown option '" + a + "' (try --help)");
            }
        }
        if (algo == null || input == null) {
            usage(miners);
            return 2;
        }
        RoleMiner miner = miners.get(algo);
        if (miner == null) {
            throw new IllegalArgumentException("unknown algorithm '" + algo + "'; available: " + miners.keySet());
        }
        UpaMatrix upa = UpaMatrix.load(input, perms, users);
        long start = System.nanoTime();
        MiningResult result = miner.mine(upa, options);
        long millis = (System.nanoTime() - start) / 1_000_000;
        Map<String, Object> metrics = Metrics.compute(upa, result);
        System.out.println("algorithm=" + miner.name());
        metrics.forEach((k, v) -> System.out.println(k + "=" + v));
        result.stats().forEach((k, v) -> System.out.println(k + "=" + v));
        System.out.println("millis=" + millis);
        if (out != null) {
            ResultWriter.write(out, upa, result, metrics, withAssignments);
            System.out.println("wrote " + out);
        }
        return 0;
    }

    private static String value(String[] args, int i, String option) {
        if (i >= args.length) throw new IllegalArgumentException(option + " needs a value");
        return args[i];
    }

    private static void usage(Map<String, RoleMiner> miners) {
        System.out.println("usage: rolemine --algo NAME --input UPA.csv [--perms FILE] [--users FILE]");
        System.out.println("                [--out DIR] [--no-assignments] [--param name=value ...]");
        System.out.println();
        System.out.println("  --input   0/1 matrix, one row per user, comma separated");
        System.out.println("  --perms   permission names, one per column: one per line, or one comma-separated line");
        System.out.println("  --users   user names, one per row, same format");
        System.out.println("  --out     directory for roles.csv, assignments.csv, metrics.txt");
        System.out.println("  --no-assignments  do not write assignments.csv (large when there are many roles)");
        System.out.println("  --list    print algorithm names");
        System.out.println();
        System.out.println("algorithms:");
        miners.values().forEach(m -> System.out.println("  " + m.name() + "  " + m.description()));
    }
}
