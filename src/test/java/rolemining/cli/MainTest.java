package rolemining.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import rolemining.testing.Check;

public class MainTest {
    /** Runs Main.run with stdout captured; returns the exit status and fills {@code out}. */
    private static int run(StringBuilder out, String... args) throws Exception {
        PrintStream saved = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
        try {
            return Main.run(args);
        } finally {
            System.setOut(saved);
            out.append(buf.toString(StandardCharsets.UTF_8));
        }
    }

    public void helpReturnsZeroAndPrintsUsage() throws Exception {
        StringBuilder out = new StringBuilder();
        Check.equal(0, run(out, "--help"));
        Check.isTrue(out.toString().contains("usage: rolemine"), "usage text");
        Check.isTrue(out.toString().contains("--algo"), "mentions --algo");
        out.setLength(0);
        Check.equal(0, run(out, "-h"));
        Check.isTrue(out.toString().contains("usage: rolemine"), "usage text for -h");
    }

    public void listReturnsZero() throws Exception {
        Check.equal(0, run(new StringBuilder(), "--list"));
    }

    public void missingAlgoOrInputReturnsTwo() throws Exception {
        StringBuilder out = new StringBuilder();
        Check.equal(2, run(out));
        Check.isTrue(out.toString().contains("usage: rolemine"), "usage printed");
        Check.equal(2, run(new StringBuilder(), "--input", "data/android-500/upa.csv"));
        Check.equal(2, run(new StringBuilder(), "--algo", "fastminer"));
    }

    public void unknownOptionIsRejected() {
        Check.fails(IllegalArgumentException.class, () -> run(new StringBuilder(), "--frobnicate"));
        Check.fails(IllegalArgumentException.class, () -> run(new StringBuilder(), "positional"));
    }

    public void unknownAlgorithmIsRejected() {
        Check.fails(IllegalArgumentException.class,
                () -> run(new StringBuilder(), "--algo", "no-such-miner", "--input", "data/android-500/upa.csv"));
    }

    public void paramWithoutEqualsIsRejected() {
        Check.fails(IllegalArgumentException.class, () -> run(new StringBuilder(), "--param", "delta"));
        Check.fails(IllegalArgumentException.class, () -> run(new StringBuilder(), "--param", "=5"));
    }

    public void optionWithoutValueIsRejected() {
        Check.fails(IllegalArgumentException.class, () -> run(new StringBuilder(), "--algo"));
        Check.fails(IllegalArgumentException.class, () -> run(new StringBuilder(), "--input"));
    }
}
