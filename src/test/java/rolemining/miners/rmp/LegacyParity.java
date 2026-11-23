package rolemining.miners.rmp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rolemining.core.MinerOptions;
import rolemining.core.MiningResult;
import rolemining.core.UpaMatrix;
import rolemining.miners.DeltaRmp;
import rolemining.miners.MinNoiseRmp;

/**
 * Parity experiment against the outputs of the legacy code. Not a test (the
 * runner only picks up *Test classes): it needs the untouched legacy repository,
 * whose 17 MB CandidateRoles file must not be copied into this one.
 *
 * <pre>
 * java -cp out/main:out/test rolemining.miners.rmp.LegacyParity /path/to/legacy/Role-Mining-Algorithms
 * </pre>
 * Run from the repository root.
 */
public final class LegacyParity {
    private LegacyParity() {}

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("usage: LegacyParity LEGACY_REPO_DIR");
            System.exit(2);
        }
        Path legacy = Path.of(args[0]);
        UpaMatrix upa = UpaMatrix.load(Path.of("data/android-500/upa.csv"),
                Path.of("data/android-500/permissions.csv"), Path.of("data/android-500/apps.txt"));
        Map<String, Integer> permIndex = new HashMap<>();
        for (int p = 0; p < upa.permCount(); p++) permIndex.put(upa.permName(p), p);

        long t0 = System.nanoTime();
        List<BitSet> legacyCandidates = loadMatrix(legacy.resolve("Output/Fast Miner output/CandidateRoles"));
        System.out.printf("legacy CandidateRoles: %d rows (read in %d ms)%n", legacyCandidates.size(),
                (System.nanoTime() - t0) / 1_000_000);
        List<BitSet> generated = CandidateSources.rowsAndPairwiseIntersections(upa, MinerOptions.empty());
        System.out.printf("generated candidates: %d; same set as legacy (ignoring the empty role): %b%n",
                generated.size(), sameSet(legacyCandidates, generated));
        compareRanking(upa, legacyCandidates, generated);

        Map<String, BiFunction<UpaMatrix, MinerOptions, List<BitSet>>> sources = new TreeMap<>();
        sources.put("legacy", CandidateSources.fixed(legacyCandidates));
        sources.put("generated", CandidateSources.fixed(generated));

        // Delta RMP
        List<BitSet> deltaCsv = loadDeltaCsv(legacy.resolve("Output/Delta RMP output/Delta RMP roles.csv"), permIndex);
        System.out.println();
        System.out.println("Delta RMP roles.csv: " + deltaCsv.size() + " roles");
        for (var src : sources.entrySet()) {
            for (int target : new int[] {350}) {
                MinerOptions o = MinerOptions.empty().set("delta", target).set("nonOverlapping", false)
                        .set("traceBelow", Integer.MAX_VALUE);
                long s = System.nanoTime();
                MiningResult r = new DeltaRmp(src.getValue()).mine(upa, o);
                long ms = (System.nanoTime() - s) / 1_000_000;
                System.out.printf("  %-9s delta=%d: %d roles, delta reached %s, %d ms; csv prefix match %d/%d,"
                        + " csv roles found anywhere %d/%d%n", src.getKey(), target, r.roles().size(),
                        r.stats().get("delta"), ms, prefixMatch(deltaCsv, r.roles()), deltaCsv.size(),
                        anywhere(deltaCsv, r.roles()), deltaCsv.size());
                String trace = (String) r.stats().get("deltaTrace");
                for (String step : trace.split(" ")) {
                    if (step.startsWith(deltaCsv.size() + ":")) {
                        System.out.println("    delta after " + deltaCsv.size() + " roles: " + step);
                    }
                }
            }
        }

        // MinNoise RMP. The settings below are the ones found to reproduce the files;
        // the "chained" ones associate at earlier checkpoints, then pick on stale areas.
        Map<String, MinerOptions> settings = new java.util.LinkedHashMap<>();
        settings.put("port-default", MinerOptions.empty());
        settings.put("legacy-code", MinerOptions.empty().set("refreshAfterAssociation", false));
        settings.put("no-interim", MinerOptions.empty().set("interimAssociations", "none"));
        settings.put("chain-5", MinerOptions.empty().set("interimAssociations", "5,10,15,20,25,30,35,40")
                .set("refreshAfterAssociation", false));
        settings.put("chain-10", MinerOptions.empty().set("interimAssociations", "10,20,30,40,50,60,70,80,90")
                .set("refreshAfterAssociation", false));
        settings.put("chain-77-94", MinerOptions.empty().set("interimAssociations", "77,94")
                .set("refreshAfterAssociation", false));
        System.out.println();
        System.out.println("MinNoise RMP: settings that reproduce each file exactly (roles in order and delta)");
        Pattern name = Pattern.compile("PA_R_(\\d+)_Delta_(\\d+)\\.txt");
        List<Path> files = new ArrayList<>();
        try (DirectoryStream<Path> dir = Files.newDirectoryStream(legacy.resolve("Output/Min Nosie RMP output"))) {
            for (Path f : dir) if (name.matcher(f.getFileName().toString()).matches()) files.add(f);
        }
        files.sort((x, y) -> {
            Matcher a = name.matcher(x.getFileName().toString());
            Matcher b = name.matcher(y.getFileName().toString());
            a.matches();
            b.matches();
            int c = Integer.compare(Integer.parseInt(a.group(1)), Integer.parseInt(b.group(1)));
            return c != 0 ? c : Integer.compare(Integer.parseInt(a.group(2)), Integer.parseInt(b.group(2)));
        });
        int reproduced = 0;
        for (Path f : files) {
            Matcher m = name.matcher(f.getFileName().toString());
            m.matches();
            int delta = Integer.parseInt(m.group(2));
            List<BitSet> want = loadNames(f, permIndex);
            List<String> hits = new ArrayList<>();
            String closest = "";
            int closestPrefix = -1;
            for (var src : sources.entrySet()) {
                for (var setting : settings.entrySet()) {
                    MinerOptions o = MinerOptions.empty();
                    setting.getValue().asMap().forEach(o::set);
                    o.set("roles", want.size());
                    MiningResult r = new MinNoiseRmp(src.getValue()).mine(upa, o);
                    int pm = prefixMatch(want, r.roles());
                    boolean same = sameList(want, r.roles()) && r.stats().get("delta").equals(delta);
                    if (same) hits.add(setting.getKey() + "/" + src.getKey());
                    if (pm > closestPrefix) {
                        closestPrefix = pm;
                        closest = setting.getKey() + " first " + pm + " roles";
                    }
                }
            }
            if (!hits.isEmpty()) reproduced++;
            System.out.printf("  %-26s %s%n", f.getFileName(), hits.isEmpty() ? "NOT reproduced (closest: " + closest + ")"
                    : String.join(" ", hits));
        }
        System.out.println("  reproduced " + reproduced + " of " + files.size() + " files");
    }

    private static void compareRanking(UpaMatrix upa, List<BitSet> a, List<BitSet> b) {
        List<RankedRoles.Role> ra = RankedRoles.rank(upa, a);
        List<RankedRoles.Role> rb = RankedRoles.rank(upa, b);
        int same = 0;
        while (same < Math.min(ra.size(), rb.size()) && ra.get(same).permissions().equals(rb.get(same).permissions())) {
            same++;
        }
        System.out.printf("ranked: legacy %d, generated %d, identical leading positions %d%n", ra.size(), rb.size(),
                same);
    }

    private static boolean sameSet(List<BitSet> a, List<BitSet> b) {
        HashSet<BitSet> sa = new HashSet<>(a);
        sa.remove(new BitSet());
        HashSet<BitSet> sb = new HashSet<>(b);
        sb.remove(new BitSet());
        return sa.equals(sb);
    }

    static int prefixMatch(List<BitSet> want, List<BitSet> got) {
        int i = 0;
        while (i < want.size() && i < got.size() && want.get(i).equals(got.get(i))) i++;
        return i;
    }

    static boolean sameList(List<BitSet> want, List<BitSet> got) {
        return want.equals(got);
    }

    static int anywhere(List<BitSet> want, List<BitSet> got) {
        HashSet<BitSet> g = new HashSet<>(got);
        int n = 0;
        for (BitSet w : want) if (g.contains(w)) n++;
        return n;
    }

    /** The legacy 0/1 format: comma-separated, trailing comma, blank lines ignored. */
    static List<BitSet> loadMatrix(Path file) throws IOException {
        List<BitSet> rows = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            String t = line.strip();
            if (t.isEmpty()) continue;
            BitSet b = new BitSet();
            int col = 0;
            for (String tok : t.split(",")) {
                String s = tok.strip();
                if (s.isEmpty()) continue;
                if (s.equals("1")) b.set(col);
                col++;
            }
            rows.add(b);
        }
        return rows;
    }

    /** One role per line, permission names separated by commas (PA_R_*.txt). */
    static List<BitSet> loadNames(Path file, Map<String, Integer> permIndex) throws IOException {
        List<BitSet> roles = new ArrayList<>();
        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            roles.add(parseNames(line, permIndex, file));
        }
        return roles;
    }

    /** "Roles,Permissions" header, then number,"name, name, ..." rows. */
    static List<BitSet> loadDeltaCsv(Path file, Map<String, Integer> permIndex) throws IOException {
        String text = Files.readString(file, StandardCharsets.UTF_8);
        if (text.startsWith("﻿")) text = text.substring(1);
        List<BitSet> roles = new ArrayList<>();
        Matcher m = Pattern.compile("\"([^\"]*)\"").matcher(text);
        while (m.find()) roles.add(parseNames(m.group(1), permIndex, file));
        return roles;
    }

    private static BitSet parseNames(String line, Map<String, Integer> permIndex, Path file) {
        BitSet b = new BitSet();
        for (String tok : line.split(",")) {
            String s = tok.strip();
            if (s.isEmpty()) continue;
            Integer p = permIndex.get(s);
            if (p == null) throw new IllegalArgumentException(file + ": unknown permission " + s);
            b.set(p);
        }
        return b;
    }
}
