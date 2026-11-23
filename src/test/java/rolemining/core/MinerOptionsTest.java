package rolemining.core;

import java.util.Map;
import rolemining.testing.Check;

public class MinerOptionsTest {
    public void defaultsWhenAbsent() {
        MinerOptions o = MinerOptions.empty();
        Check.isTrue(!o.has("delta"), "has");
        Check.equal(350, o.getInt("delta", 350));
        Check.equal(0.5, o.getDouble("x", 0.5));
        Check.equal("fallback", o.get("name", "fallback"));
        Check.equal(Map.of(), o.asMap());
    }

    public void parsesValues() {
        MinerOptions o = MinerOptions.empty().set("delta", "42").set("ratio", "0.25").set("flag", true);
        Check.isTrue(o.has("delta"), "has");
        Check.equal(42, o.getInt("delta", 0));
        Check.equal(0.25, o.getDouble("ratio", 0));
        Check.equal("true", o.get("flag", ""));
        Check.equal(42.0, o.getDouble("delta", 0));
    }

    public void toleratesSurroundingSpacesInNumbers() {
        MinerOptions o = MinerOptions.empty().set("k", " 7 ");
        Check.equal(7, o.getInt("k", 0));
    }

    public void laterValueReplacesEarlier() {
        MinerOptions o = MinerOptions.empty().set("k", 1).set("k", 2);
        Check.equal(2, o.getInt("k", 0));
        Check.equal(Map.of("k", "2"), o.asMap());
    }

    public void nonNumericIsAnError() {
        MinerOptions o = MinerOptions.empty().set("delta", "abc").set("ratio", "x1");
        try {
            o.getInt("delta", 0);
            throw new AssertionError("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            Check.isTrue(e.getMessage().contains("delta") && e.getMessage().contains("abc"), e.getMessage());
        }
        Check.fails(IllegalArgumentException.class, () -> o.getDouble("ratio", 0));
        Check.fails(IllegalArgumentException.class, () -> MinerOptions.empty().set("n", "1.5").getInt("n", 0));
    }

    public void asMapIsACopy() {
        MinerOptions o = MinerOptions.empty().set("a", 1);
        Check.fails(UnsupportedOperationException.class, () -> o.asMap().put("b", "2"));
    }
}
