package rolemining.miners.enumeration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HexFormat;
import java.util.List;
import rolemining.core.UpaMatrix;

/**
 * Rebuilds the text files the legacy programs wrote (formats copied from
 * fastMinerPrintOutput.printOutput, PrintCR_And_Ratings.printRating and
 * completeMiner.main), so the port can be compared with the original outputs.
 */
final class LegacyFormat {
    private LegacyFormat() {}

    static String output(UpaMatrix upa, EnumerationResult e, boolean complete) {
        int perms = upa.permCount();
        List<BitSet> rows = rows(upa);
        List<BitSet> ir = e.initialRoles();
        List<BitSet> gr = e.generatedRoles();
        int[] vc = vertical(rows, perms);
        StringBuilder sb = new StringBuilder();
        sb.append("Perm distribution: ").append(list(vc)).append('\n');
        sb.append("Total number of Perms: ").append(perms).append('\n');
        sb.append("App distribution: ").append(list(sizes(rows))).append('\n');
        sb.append("Total number of Apps: ").append(rows.size()).append('\n');
        if (complete) {
            sb.append("\n Used permissions/Total Permissions: ").append(used(vc)).append('/').append(perms).append('\n');
            sb.append("\n Used permissions/Total Permissions: (IR) ").append(used(vertical(ir, perms))).append('/').append(perms).append('\n');
            sb.append("\n Used permissions/Total Permissions: (GR) ").append(used(vertical(gr, perms))).append('/').append(perms).append('\n');
        } else {
            for (int p = 0; p < perms; p++) if (vc[p] != 0) sb.append(upa.permName(p)).append(',');
            sb.append("\n Used permissions/Total Permissions: ").append(used(vc)).append('/').append(perms).append('\n');
            sb.append("\n Used permissions IR: ").append(used(vertical(ir, perms))).append('\n');
            sb.append("\n Used permissions GR: ").append(used(vertical(gr, perms))).append('\n');
        }
        sb.append("\n Number of Perms associated with each IR: ").append(list(sizes(ir))).append("\n\n");
        sb.append("Orig count:").append(list(e.originalCounts())).append("\n\n");
        sb.append("Orig count size:").append(ir.size()).append("\n\n");
        sb.append("\n Number of Perms associated with each GR: ").append(list(sizes(gr))).append('\n');
        sb.append("Count:").append(list(e.generatedCounts())).append("\n\n");
        sb.append("GR SIZE:").append(gr.size()).append("\n\n");
        if (complete) sb.append("1st GR").append(zeroOne(gr.get(0), perms)).append("\n\n");
        sb.append("Count Size:").append(e.generatedCounts().length).append("\n\n");
        return sb.toString();
    }

    static String ratings(CandidateSet c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < c.size(); i++) sb.append(c.ratings(i)).append('\n');
        sb.append("Size of rating: ").append(c.size()).append('\n');
        return sb.toString();
    }

    /** Fast Miner "PermCR": permission names of each candidate. */
    static String permCr(UpaMatrix upa, List<BitSet> roles) {
        StringBuilder sb = new StringBuilder();
        for (BitSet b : roles) {
            names(upa, b, sb);
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Fast Miner "CandidateRoles": a blank line, then one 0/1 row per candidate. */
    static String candidateRoles(List<BitSet> roles, int perms) {
        StringBuilder sb = new StringBuilder("\n");
        for (BitSet b : roles) {
            for (int p = 0; p < perms; p++) sb.append(b.get(p) ? '1' : '0').append(',');
            sb.append('\n');
        }
        return sb.toString();
    }

    /** Complete Miner "IRPerms". */
    static String irPerms(UpaMatrix upa, List<BitSet> roles) {
        StringBuilder sb = new StringBuilder();
        for (BitSet b : roles) {
            sb.append('\n');
            names(upa, b, sb);
        }
        return sb.toString();
    }

    static String numPermCr(List<BitSet> roles) {
        return "\n Number of Perms associated with each CR: " + list(sizes(roles)) + "\n";
    }

    static String crVertical(List<BitSet> roles, int perms) {
        return "Vertical permission count CR" + list(vertical(roles, perms)) + "\n";
    }

    /** "sha256hex size", the format of legacy-500.properties. */
    static String digest(String text) {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(bytes);
            return HexFormat.of().formatHex(d) + " " + bytes.length;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void names(UpaMatrix upa, BitSet b, StringBuilder sb) {
        for (int p = b.nextSetBit(0); p >= 0; p = b.nextSetBit(p + 1)) sb.append(upa.permName(p)).append(',');
    }

    private static List<BitSet> rows(UpaMatrix upa) {
        List<BitSet> rows = new ArrayList<>();
        for (int u = 0; u < upa.userCount(); u++) rows.add(upa.row(u));
        return rows;
    }

    private static String list(int[] a) {
        List<Integer> l = new ArrayList<>(a.length);
        for (int x : a) l.add(x);
        return l.toString();
    }

    private static int[] sizes(List<BitSet> roles) {
        int[] a = new int[roles.size()];
        for (int i = 0; i < a.length; i++) a[i] = roles.get(i).cardinality();
        return a;
    }

    private static int[] vertical(List<BitSet> roles, int perms) {
        int[] a = new int[perms];
        for (BitSet b : roles) for (int p = b.nextSetBit(0); p >= 0; p = b.nextSetBit(p + 1)) a[p]++;
        return a;
    }

    private static int used(int[] v) {
        int n = 0;
        for (int x : v) if (x != 0) n++;
        return n;
    }

    private static String zeroOne(BitSet b, int perms) {
        List<Integer> l = new ArrayList<>(perms);
        for (int p = 0; p < perms; p++) l.add(b.get(p) ? 1 : 0);
        return l.toString();
    }
}
