package rolemining.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * User-permission assignment (UPA) matrix: one row per user (here, an app),
 * one column per permission. Immutable.
 */
public final class UpaMatrix {
    private final List<BitSet> rows;
    private final int permCount;
    private final List<String> userNames;
    private final List<String> permNames;

    private UpaMatrix(List<BitSet> rows, int permCount, List<String> userNames, List<String> permNames) {
        this.rows = rows;
        this.permCount = permCount;
        this.userNames = userNames;
        this.permNames = permNames;
    }

    public int userCount() { return rows.size(); }

    public int permCount() { return permCount; }

    /** Permissions held by user {@code u}. Returns a copy; safe to modify. */
    public BitSet row(int u) { return (BitSet) rows.get(u).clone(); }

    public boolean get(int u, int p) { return rows.get(u).get(p); }

    /** Number of 1 cells in the matrix. */
    public int cardinality() {
        int n = 0;
        for (BitSet r : rows) n += r.cardinality();
        return n;
    }

    public String userName(int u) { return userNames.get(u); }

    public String permName(int p) { return permNames.get(p); }

    public List<String> userNames() { return userNames; }

    public List<String> permNames() { return permNames; }

    /** Builds a matrix from a 0/1 array, with generated names (u0.., p0..). */
    public static UpaMatrix of(int[][] cells) {
        int perms = cells.length == 0 ? 0 : cells[0].length;
        List<BitSet> rows = new ArrayList<>();
        for (int[] line : cells) {
            if (line.length != perms) throw new IllegalArgumentException("ragged matrix");
            BitSet b = new BitSet(perms);
            for (int p = 0; p < perms; p++) {
                if (line[p] == 1) b.set(p);
                else if (line[p] != 0) throw new IllegalArgumentException("cells must be 0 or 1");
            }
            rows.add(b);
        }
        return new UpaMatrix(Collections.unmodifiableList(rows), perms,
                defaultNames("u", rows.size()), defaultNames("p", perms));
    }

    /** Loads a matrix with generated user and permission names. */
    public static UpaMatrix load(Path matrixCsv) throws IOException {
        return load(matrixCsv, null, null);
    }

    /**
     * Loads a comma-separated 0/1 matrix. Blank lines, spaces around values,
     * trailing commas and UTF-8/UTF-16 byte order marks are tolerated.
     *
     * @param permNamesFile permission names, one per line or all on one comma-separated line, or null
     * @param userNamesFile user names, in the same format, or null
     */
    public static UpaMatrix load(Path matrixCsv, Path permNamesFile, Path userNamesFile) throws IOException {
        List<BitSet> rows = new ArrayList<>();
        int perms = -1;
        int lineNo = 0;
        for (String line : readText(matrixCsv).split("\\R")) {
            lineNo++;
            String trimmed = line.strip();
            if (trimmed.isEmpty()) continue;
            BitSet b = new BitSet();
            int width = 0;
            for (String token : trimmed.split(",")) {
                String t = token.strip();
                if (t.isEmpty()) continue;
                if (t.equals("1")) b.set(width);
                else if (!t.equals("0")) {
                    throw new IOException(matrixCsv + ":" + lineNo + ": expected 0 or 1 but found '" + t + "'");
                }
                width++;
            }
            if (perms == -1) perms = width;
            else if (width != perms) {
                throw new IOException(matrixCsv + ":" + lineNo + ": row has " + width
                        + " values, expected " + perms);
            }
            rows.add(b);
        }
        if (perms == -1) perms = 0;
        List<String> permNames = permNamesFile == null ? defaultNames("p", perms)
                : readNames(permNamesFile, perms, "permission");
        List<String> userNames = userNamesFile == null ? defaultNames("u", rows.size())
                : readNames(userNamesFile, rows.size(), "user");
        return new UpaMatrix(Collections.unmodifiableList(rows), perms, userNames, permNames);
    }

    private static List<String> readNames(Path file, int expected, String what) throws IOException {
        List<String> names = new ArrayList<>();
        // One name per line; a file with a single line is split on commas instead.
        for (String line : readText(file).split("\\R")) {
            String t = line.strip();
            if (!t.isEmpty()) names.add(t);
        }
        if (names.size() == 1 && expected != 1) {
            String only = names.remove(0);
            for (String token : only.split(",")) {
                String t = token.strip();
                if (!t.isEmpty()) names.add(t);
            }
        }
        if (names.size() != expected) {
            throw new IOException(file + ": found " + names.size() + " " + what
                    + " names, but the matrix has " + expected);
        }
        return Collections.unmodifiableList(names);
    }

    private static List<String> defaultNames(String prefix, int n) {
        List<String> names = new ArrayList<>(n);
        for (int i = 0; i < n; i++) names.add(prefix + i);
        return Collections.unmodifiableList(names);
    }

    /** Reads a text file, detecting UTF-8 or UTF-16 from its byte order mark. */
    static String readText(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        Charset cs = StandardCharsets.UTF_8;
        int skip = 0;
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
            cs = StandardCharsets.UTF_16LE;
            skip = 2;
        } else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
            cs = StandardCharsets.UTF_16BE;
            skip = 2;
        } else if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB
                && (bytes[2] & 0xFF) == 0xBF) {
            skip = 3;
        }
        return new String(bytes, skip, bytes.length - skip, cs);
    }
}
