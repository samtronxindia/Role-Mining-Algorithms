package rolemining.cli;

import java.util.LinkedHashMap;
import java.util.Map;
import rolemining.core.RoleMiner;

/** The miners available from the command line. */
final class Miners {
    private Miners() {}

    private static final String[] CLASSES = {
        "rolemining.miners.CompleteMiner",
        "rolemining.miners.FastMiner",
        "rolemining.miners.BasicRmp",
        "rolemining.miners.DeltaRmp",
        "rolemining.miners.MinNoiseRmp",
    };

    static Map<String, RoleMiner> all() {
        Map<String, RoleMiner> miners = new LinkedHashMap<>();
        for (String cls : CLASSES) {
            try {
                RoleMiner m = (RoleMiner) Class.forName(cls).getDeclaredConstructor().newInstance();
                miners.put(m.name(), m);
            } catch (ClassNotFoundException e) {
                // not built into this copy; skip
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("cannot create " + cls, e);
            }
        }
        return miners;
    }
}
