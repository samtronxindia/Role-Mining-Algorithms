package rolemining.core;

import java.nio.file.Path;
import rolemining.testing.Check;

/** Loads every bundled data file and fixture; the sizes are what the files actually contain. */
public class BundledDataTest {
    public void loadsAndroid500() throws Exception {
        UpaMatrix upa = UpaMatrix.load(Path.of("data/android-500/upa.csv"),
                Path.of("data/android-500/permissions.csv"), Path.of("data/android-500/apps.txt"));
        Check.equal(500, upa.userCount());
        Check.equal(582, upa.permCount());
        Check.equal(7598, upa.cardinality());
        Check.equal(500, upa.userNames().size());
        Check.equal(582, upa.permNames().size());
    }

    public void appNamesContainingCommasAreKeptWhole() throws Exception {
        UpaMatrix upa = UpaMatrix.load(Path.of("data/android-500/upa.csv"),
                Path.of("data/android-500/permissions.csv"), Path.of("data/android-500/apps.txt"));
        long withComma = upa.userNames().stream().filter(n -> n.contains(",")).count();
        Check.equal(18L, withComma);
        Check.isTrue(upa.userNames().contains(
                "co.vero.app_0.9.5-90510_minAPI21(arm64-v8a,armeabi,armeabi-v7a,mips,mips64,x86,x86_64)(nodpi)_apkmirror.com.apk.txt"),
                "app name with commas present");
    }

    public void loadsAndroid5000Utf16() throws Exception {
        UpaMatrix upa = UpaMatrix.load(Path.of("data/android-5000/upa-utf16.csv"));
        Check.equal(5000, upa.userCount());
        Check.equal(592, upa.permCount());
        Check.equal(57982, upa.cardinality());
    }

    public void loadsLegacyCombineUtf16() throws Exception {
        UpaMatrix upa = UpaMatrix.load(Path.of("data/legacy/combine-utf16.csv"));
        Check.equal(497, upa.userCount());
        Check.equal(542, upa.permCount());
        Check.equal(5108, upa.cardinality());
    }

    public void loadsSmallFixtures() throws Exception {
        UpaMatrix a = UpaMatrix.load(Path.of("src/test/resources/fixtures/small-4x7.csv"));
        Check.equal(4, a.userCount());
        Check.equal(7, a.permCount());
        Check.equal(15, a.cardinality());
        UpaMatrix b = UpaMatrix.load(Path.of("src/test/resources/fixtures/small-15x4.csv"));
        Check.equal(15, b.userCount());
        Check.equal(4, b.permCount());
        Check.equal(32, b.cardinality());
    }
}
