package rolemining.miners.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * An insertion-ordered set of distinct roles (fixed-width bit vectors) with
 * constant-time lookup. Index {@code i} is the i-th distinct role added; this
 * order is what the legacy code's ArrayLists kept, so it is part of the output.
 */
final class RoleTable {
    private final int words;
    private long[][] roles = new long[16][];
    private int[] hashes = new int[16];
    private int size;
    private int[] slots = emptySlots(64);

    RoleTable(int words) {
        this.words = words;
    }

    int size() { return size; }

    int words() { return words; }

    /** The role at {@code index}. Callers must not modify the returned array. */
    long[] get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException(index);
        return roles[index];
    }

    /** Index of the role equal to {@code key}, or -1. */
    int indexOf(long[] key) {
        int h = Bits.hash(key);
        int mask = slots.length - 1;
        for (int s = h & mask; ; s = (s + 1) & mask) {
            int idx = slots[s];
            if (idx < 0) return -1;
            if (hashes[idx] == h && Arrays.equals(roles[idx], key)) return idx;
        }
    }

    /** Adds a copy of {@code key}, which must not already be present. Returns its index. */
    int add(long[] key) {
        if (key.length != words) throw new IllegalArgumentException("role has " + key.length + " words, expected " + words);
        if (size == roles.length) {
            roles = Arrays.copyOf(roles, size * 2);
            hashes = Arrays.copyOf(hashes, size * 2);
        }
        int h = Bits.hash(key);
        roles[size] = key.clone();
        hashes[size] = h;
        if (2 * (size + 1) > slots.length) rehash(slots.length * 2);
        insert(size, h);
        return size++;
    }

    List<BitSet> toBitSets() {
        List<BitSet> out = new ArrayList<>(size);
        for (int i = 0; i < size; i++) out.add(Bits.toBitSet(roles[i]));
        return out;
    }

    private void insert(int index, int h) {
        int mask = slots.length - 1;
        int s = h & mask;
        while (slots[s] >= 0) s = (s + 1) & mask;
        slots[s] = index;
    }

    private void rehash(int capacity) {
        slots = emptySlots(capacity);
        for (int i = 0; i < size; i++) insert(i, hashes[i]);
    }

    private static int[] emptySlots(int n) {
        int[] s = new int[n];
        Arrays.fill(s, -1);
        return s;
    }
}
