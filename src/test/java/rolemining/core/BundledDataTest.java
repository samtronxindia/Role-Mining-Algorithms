package rolemining.core;

import java.nio.file.Path;
import rolemining.testing.Check;

public class BundledDataTest {
    public void loadsAndroid500() throws Exception {
        UpaMatrix upa = UpaMatrix.load(Path.of("data/android-500/upa.csv"),
                Path.of("data/android-500/permissions.csv"), Path.of("data/android-500/apps.txt"));
        Check.equal(500, upa.userCount());
        Check.equal(582, upa.permCount());
    }
}
