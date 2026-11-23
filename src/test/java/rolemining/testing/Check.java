package rolemining.testing;

import java.util.Objects;

/** Minimal assertions, so the tests need nothing beyond the JDK. */
public final class Check {
    private Check() {}

    public static void equal(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void equal(Object expected, Object actual, String what) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(what + ": expected <" + expected + "> but was <" + actual + ">");
        }
    }

    public static void isTrue(boolean condition, String what) {
        if (!condition) throw new AssertionError(what);
    }

    /** Passes when {@code body} throws an exception of the given type. */
    public static void fails(Class<? extends Throwable> type, ThrowingRunnable body) {
        try {
            body.run();
        } catch (Throwable t) {
            if (type.isInstance(t)) return;
            throw new AssertionError("expected " + type.getSimpleName() + " but got " + t, t);
        }
        throw new AssertionError("expected " + type.getSimpleName() + " but nothing was thrown");
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
