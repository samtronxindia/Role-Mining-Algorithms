package rolemining.core;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.List;
import rolemining.testing.Check;

public class UpaMatrixTest {
    private static Path temp(byte[] bytes) throws IOException {
        Path p = Files.createTempFile("upa-test", ".txt");
        p.toFile().deleteOnExit();
        Files.write(p, bytes);
        return p;
    }

    private static Path temp(String text) throws IOException {
        return temp(text.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] withBom(byte[] bom, String text, Charset cs) {
        byte[] body = text.getBytes(cs);
        byte[] all = new byte[bom.length + body.length];
        System.arraycopy(bom, 0, all, 0, bom.length);
        System.arraycopy(body, 0, all, bom.length, body.length);
        return all;
    }

    private static void checkTwoByThree(UpaMatrix m) {
        Check.equal(2, m.userCount());
        Check.equal(3, m.permCount());
        Check.equal(4, m.cardinality());
        Check.isTrue(m.get(0, 0) && m.get(0, 1) && !m.get(0, 2), "row 0");
        Check.isTrue(!m.get(1, 0) && m.get(1, 1) && m.get(1, 2), "row 1");
    }

    public void ofBuildsMatrixWithGeneratedNames() {
        UpaMatrix m = UpaMatrix.of(new int[][] {{1, 0, 1}, {0, 0, 1}});
        Check.equal(2, m.userCount());
        Check.equal(3, m.permCount());
        Check.equal(3, m.cardinality());
        Check.isTrue(m.get(0, 0) && !m.get(0, 1) && m.get(0, 2), "row 0 cells");
        Check.equal("u1", m.userName(1));
        Check.equal("p2", m.permName(2));
        Check.equal(List.of("u0", "u1"), m.userNames());
        Check.equal(List.of("p0", "p1", "p2"), m.permNames());
    }

    public void ofEmptyMatrix() {
        UpaMatrix m = UpaMatrix.of(new int[0][]);
        Check.equal(0, m.userCount());
        Check.equal(0, m.permCount());
        Check.equal(0, m.cardinality());
    }

    public void ofRejectsRaggedAndNonBinary() {
        Check.fails(IllegalArgumentException.class, () -> UpaMatrix.of(new int[][] {{1, 0}, {1}}));
        Check.fails(IllegalArgumentException.class, () -> UpaMatrix.of(new int[][] {{1, 2}}));
    }

    public void rowReturnsACopy() {
        UpaMatrix m = UpaMatrix.of(new int[][] {{1, 0, 1}});
        BitSet r = m.row(0);
        r.clear();
        r.set(1);
        Check.isTrue(m.get(0, 0) && !m.get(0, 1) && m.get(0, 2), "matrix unchanged");
        Check.equal(2, m.row(0).cardinality());
    }

    public void cardinalityCountsOnes() {
        Check.equal(0, UpaMatrix.of(new int[][] {{0, 0}, {0, 0}}).cardinality());
        Check.equal(4, UpaMatrix.of(new int[][] {{1, 1}, {1, 1}}).cardinality());
    }

    public void loadPlainFile() throws Exception {
        checkTwoByThree(UpaMatrix.load(temp("1,1,0\n0,1,1\n")));
    }

    public void loadGeneratesNamesWhenNoneGiven() throws Exception {
        UpaMatrix m = UpaMatrix.load(temp("1,1,0\n0,1,1\n"));
        Check.equal("u0", m.userName(0));
        Check.equal("p2", m.permName(2));
    }

    public void loadToleratesBlankLinesSpacesAndTrailingCommas() throws Exception {
        checkTwoByThree(UpaMatrix.load(temp("\n 1 , 1 ,0 ,\n\n   \n0,1, 1,\n\n")));
    }

    public void loadToleratesCrlfAndMissingFinalNewline() throws Exception {
        checkTwoByThree(UpaMatrix.load(temp("1,1,0\r\n0,1,1")));
        checkTwoByThree(UpaMatrix.load(temp("1,1,0\r\n0,1,1\r\n")));
    }

    public void loadToleratesUtf8Bom() throws Exception {
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        checkTwoByThree(UpaMatrix.load(temp(withBom(bom, "1,1,0\n0,1,1\n", StandardCharsets.UTF_8))));
    }

    public void loadReadsUtf16LittleEndianWithBom() throws Exception {
        byte[] bom = {(byte) 0xFF, (byte) 0xFE};
        checkTwoByThree(UpaMatrix.load(temp(withBom(bom, "1,1,0\r\n0,1,1\r\n", StandardCharsets.UTF_16LE))));
    }

    public void loadReadsUtf16BigEndianWithBom() throws Exception {
        byte[] bom = {(byte) 0xFE, (byte) 0xFF};
        checkTwoByThree(UpaMatrix.load(temp(withBom(bom, "1,1,0\n0,1,1\n", StandardCharsets.UTF_16BE))));
    }

    public void loadEmptyFileGivesEmptyMatrix() throws Exception {
        UpaMatrix m = UpaMatrix.load(temp("\n\n"));
        Check.equal(0, m.userCount());
        Check.equal(0, m.permCount());
    }

    public void loadRejectsRaggedRowWithLineNumber() throws Exception {
        Path p = temp("1,0,1\n\n1,0\n");
        try {
            UpaMatrix.load(p);
            throw new AssertionError("expected IOException");
        } catch (IOException e) {
            Check.isTrue(e.getMessage().contains(":3:"), "message names line 3: " + e.getMessage());
            Check.isTrue(e.getMessage().contains("2 values"), "message gives width: " + e.getMessage());
        }
    }

    public void loadRejectsNonBinaryValueWithLineNumber() throws Exception {
        Path p = temp("1,0,1\n0,2,1\n");
        try {
            UpaMatrix.load(p);
            throw new AssertionError("expected IOException");
        } catch (IOException e) {
            Check.isTrue(e.getMessage().contains(":2:"), "message names line 2: " + e.getMessage());
            Check.isTrue(e.getMessage().contains("'2'"), "message shows value: " + e.getMessage());
        }
        Check.fails(IOException.class, () -> UpaMatrix.load(temp("1,x\n")));
    }

    public void namesOnePerLine() throws Exception {
        Path perms = temp("read\nwrite\nexec\n");
        Path users = temp("alice\r\nbob\r\n");
        UpaMatrix m = UpaMatrix.load(temp("1,1,0\n0,1,1\n"), perms, users);
        Check.equal(List.of("read", "write", "exec"), m.permNames());
        Check.equal(List.of("alice", "bob"), m.userNames());
    }

    public void namesOnSingleCommaSeparatedLine() throws Exception {
        Path perms = temp("read, write ,exec\n");
        Path users = temp("alice,bob");
        UpaMatrix m = UpaMatrix.load(temp("1,1,0\n0,1,1\n"), perms, users);
        Check.equal(List.of("read", "write", "exec"), m.permNames());
        Check.equal(List.of("alice", "bob"), m.userNames());
    }

    public void namesWithCommasSurviveWhenOnePerLine() throws Exception {
        Path users = temp("app(arm,x86).apk\nplain.apk\n");
        UpaMatrix m = UpaMatrix.load(temp("1,1,0\n0,1,1\n"), null, users);
        Check.equal("app(arm,x86).apk", m.userName(0));
        Check.equal("plain.apk", m.userName(1));
    }

    public void namesReadFromUtf16File() throws Exception {
        byte[] bom = {(byte) 0xFF, (byte) 0xFE};
        Path perms = temp(withBom(bom, "a\r\nb\r\nc\r\n", StandardCharsets.UTF_16LE));
        UpaMatrix m = UpaMatrix.load(temp("1,1,0\n0,1,1\n"), perms, null);
        Check.equal(List.of("a", "b", "c"), m.permNames());
    }

    public void nameCountMismatchIsAnError() throws Exception {
        Path matrix = temp("1,1,0\n0,1,1\n");
        try {
            UpaMatrix.load(matrix, temp("a\nb\n"), null);
            throw new AssertionError("expected IOException");
        } catch (IOException e) {
            Check.isTrue(e.getMessage().contains("2 permission names"), e.getMessage());
            Check.isTrue(e.getMessage().contains("3"), e.getMessage());
        }
        Check.fails(IOException.class, () -> UpaMatrix.load(matrix, null, temp("only-one\n")));
        Check.fails(IOException.class, () -> UpaMatrix.load(matrix, null, temp("a,b,c\n")));
    }
}
