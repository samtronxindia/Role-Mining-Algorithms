package rolemining.miners.enumeration;

import java.util.BitSet;

/**
 * Fixed-width bit vectors as {@code long[]}, used inside the enumeration so that
 * the hundreds of millions of intersections CompleteMiner performs need no
 * allocation. Roles leave the package as {@link BitSet}s.
 */
final class Bits {
    private Bits() {}

    /** Number of 64-bit words needed for {@code bits} bits (at least one). */
    static int words(int bits) {
        return Math.max(1, (bits + 63) >>> 6);
    }

    static long[] of(BitSet set, int words) {
        long[] w = new long[words];
        long[] src = set.toLongArray();
        if (src.length > words) throw new IllegalArgumentException("bit set wider than " + (64 * words) + " bits");
        System.arraycopy(src, 0, w, 0, src.length);
        return w;
    }

    static BitSet toBitSet(long[] w) {
        return BitSet.valueOf(w);
    }

    /** out = a AND b. {@code out} may be the same array as a or b. */
    static void and(long[] a, long[] b, long[] out) {
        for (int i = 0; i < out.length; i++) out[i] = a[i] & b[i];
    }

    static boolean isEmpty(long[] a) {
        for (long x : a) if (x != 0) return false;
        return true;
    }

    /** True when every bit of {@code a} is also set in {@code b}. */
    static boolean isSubset(long[] a, long[] b) {
        for (int i = 0; i < a.length; i++) if ((a[i] & ~b[i]) != 0) return false;
        return true;
    }

    static int cardinality(long[] a) {
        int n = 0;
        for (long x : a) n += Long.bitCount(x);
        return n;
    }

    static int hash(long[] a) {
        long h = 0x9E3779B97F4A7C15L;
        for (long x : a) {
            h ^= x;
            h *= 0xBF58476D1CE4E5B9L;
            h ^= h >>> 31;
        }
        return (int) (h ^ (h >>> 32));
    }
}
