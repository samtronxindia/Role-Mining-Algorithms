package rolemining.miners.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Candidate roles with the numbers the legacy {@code calcRating} step attached
 * to them. For candidate i:
 * <ul>
 * <li>{@link #exactUsers(int)}: users whose permissions are exactly the role
 *     (the legacy "origCount"); 0 if the candidate is a generated role only;</li>
 * <li>{@link #count(int)}: the generated role's count (FastMiner: users of the
 *     initial roles whose pairwise intersections produced it; CompleteMiner: users
 *     whose permissions are a proper superset of it); 0 if the candidate is an
 *     initial role only;</li>
 * <li>{@link #rating(int, int)}: {@code exactUsers * p + count} at priority p.
 *     This is the legacy rating for all three kinds of candidate.</li>
 * </ul>
 * Immutable.
 */
public final class CandidateSet {
    /** Where a candidate came from. */
    public enum Origin {
        /** An initial role (a distinct user row) that is not also a generated role. */
        INITIAL,
        /** A generated role that is not also an initial role. */
        GENERATED,
        /** A role that is both: the legacy code merged these into one candidate. */
        BOTH
    }

    private final List<BitSet> roles;
    private final Origin[] origin;
    private final int[] exact;
    private final int[] count;
    private final int[] initialIndex;
    private final int[] generatedIndex;
    private final int[] priorities;

    CandidateSet(List<BitSet> roles, Origin[] origin, int[] exact, int[] count,
            int[] initialIndex, int[] generatedIndex, int[] priorities) {
        this.roles = roles;
        this.origin = origin;
        this.exact = exact;
        this.count = count;
        this.initialIndex = initialIndex;
        this.generatedIndex = generatedIndex;
        this.priorities = priorities;
    }

    public int size() { return roles.size(); }

    /** The permissions of candidate i (a copy). */
    public BitSet role(int i) { return (BitSet) roles.get(i).clone(); }

    /** Copies of all candidate roles, in this set's order. */
    public List<BitSet> roles() {
        List<BitSet> out = new ArrayList<>(roles.size());
        for (BitSet b : roles) out.add((BitSet) b.clone());
        return out;
    }

    public Origin origin(int i) { return origin[i]; }

    public int exactUsers(int i) { return exact[i]; }

    public int count(int i) { return count[i]; }

    /** Index into {@link EnumerationResult#initialRoles()}, or -1. */
    public int initialIndex(int i) { return initialIndex[i]; }

    /** Index into {@link EnumerationResult#generatedRoles()}, or -1. */
    public int generatedIndex(int i) { return generatedIndex[i]; }

    /** The priorities at which {@link #ratings(int)} are computed. */
    public int[] priorities() { return priorities.clone(); }

    public int rating(int i, int priority) {
        return Math.addExact(Math.multiplyExact(exact[i], priority), count[i]);
    }

    /** One rating per priority in {@link #priorities()}: one line of the legacy "ratings" file. */
    public List<Integer> ratings(int i) {
        List<Integer> r = new ArrayList<>(priorities.length);
        for (int p : priorities) r.add(rating(i, p));
        return r;
    }

    /** Number of candidates whose role equals an earlier candidate's (non-zero only in legacy mode). */
    public int duplicateRoles() {
        return roles.size() - (int) roles.stream().distinct().count();
    }

    /** This set sorted by rating at {@code priority}, highest first; ties keep their order. */
    public CandidateSet orderedByRating(int priority) {
        Integer[] idx = IntStream.range(0, size()).boxed().toArray(Integer[]::new);
        Arrays.sort(idx, Comparator.comparingInt((Integer i) -> rating(i, priority)).reversed());
        return permute(idx);
    }

    private CandidateSet permute(Integer[] idx) {
        int n = idx.length;
        List<BitSet> r = new ArrayList<>(n);
        Origin[] o = new Origin[n];
        int[] e = new int[n];
        int[] c = new int[n];
        int[] ii = new int[n];
        int[] gi = new int[n];
        for (int k = 0; k < n; k++) {
            int i = idx[k];
            r.add(roles.get(i));
            o[k] = origin[i];
            e[k] = exact[i];
            c[k] = count[i];
            ii[k] = initialIndex[i];
            gi[k] = generatedIndex[i];
        }
        return new CandidateSet(List.copyOf(r), o, e, c, ii, gi, priorities);
    }
}
