package rolemining.testing;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Runs every public no-argument void method of every class named *Test under
 * the given compiled-classes directory. Exit status 1 if any method fails.
 * Run from the repository root so tests can read data/ and src/test/resources/.
 */
public final class TestRunner {
    private TestRunner() {}

    public static void main(String[] args) throws IOException {
        Path root = Path.of(args.length > 0 ? args[0] : "out/test");
        List<String> classes;
        try (Stream<Path> files = Files.walk(root)) {
            classes = files.map(root::relativize).map(Path::toString)
                    .filter(f -> f.endsWith("Test.class") && !f.contains("$"))
                    .map(f -> f.substring(0, f.length() - ".class".length()).replace('/', '.').replace('\\', '.'))
                    .sorted().toList();
        }
        int passed = 0;
        int failed = 0;
        for (String name : classes) {
            Class<?> cls;
            Object instance;
            try {
                cls = Class.forName(name);
                instance = cls.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                System.out.println("FAIL " + name + ": cannot create: " + e);
                failed++;
                continue;
            }
            for (Method m : cls.getDeclaredMethods()) {
                if (!Modifier.isPublic(m.getModifiers()) || m.getParameterCount() != 0
                        || m.getReturnType() != void.class || Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                try {
                    m.invoke(instance);
                    passed++;
                } catch (InvocationTargetException e) {
                    failed++;
                    System.out.println("FAIL " + cls.getSimpleName() + "." + m.getName() + ": " + e.getCause());
                } catch (IllegalAccessException e) {
                    failed++;
                    System.out.println("FAIL " + cls.getSimpleName() + "." + m.getName() + ": " + e);
                }
            }
        }
        System.out.println(passed + " passed, " + failed + " failed, " + classes.size() + " test classes");
        if (failed > 0 || passed == 0) System.exit(1);
    }
}
