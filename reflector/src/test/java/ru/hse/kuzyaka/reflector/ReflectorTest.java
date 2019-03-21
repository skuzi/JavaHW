package ru.hse.kuzyaka.reflector;

import org.joor.Reflect;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.hse.kuzyaka.reflector.testclasses.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    private static final Path PREFIX = Paths.get("src", "test", "java");
    private static final Path PACKAGE = Paths.get("ru", "hse", "kuzyaka", "reflector");

    @AfterAll
    static void shutDown() throws IOException {
        Files.deleteIfExists(Paths.get(PREFIX.toString(), "SomeClass.java"));
    }

    @Test
    void testPrintSimple() throws IOException {
        compareStructure(Dummy.class, "ExpectedDummy");
    }

    @Test
    void testPrintFields() throws IOException {
        compareStructure(ClassWithFields.class, "ExpectedClassWithFields");
    }

    @Test
    void testPrintLang() throws IOException {
        compareStructure(HashSet.class, "ExpectedHashSet");
    }

    @Test
    void testNested() throws IOException {
        compareStructure(Nested1.class, "ExpectedNested1");
    }

    @Test
    void testInnerGeneric() throws IOException {
        compareStructure(Generic1.class, "ExpectedGeneric1");
    }

    @Test
    void diffClassesSimple() {
        assertTrue(Reflector.diffClasses(HashSet.class, HashMap.class, System.out));
    }

    @Test
    void testDiffSame() {
        assertFalse(Reflector.diffClasses(Generic1.class, Generic1Copy.class, System.out));
    }

    @Test
    void testDifferent() throws IOException {
        var out = new ByteArrayOutputStream();
        assertTrue(Reflector.diffClasses(Simple1.class, Simple2.class, new PrintStream(out)));
        var sOut = out.toString();
        System.out.println(sOut);
        assertEquals(
                getFileContents(Paths.get(PREFIX.toString(),
                        PACKAGE.toString(), "testclasses", "expected", "ExpectedDiffSimple1Simple2")),
                sOut);
    }

    @Test
    void testCompileSimple1() throws IOException {
        assertFalse(Reflector.diffClasses(Simple1.class, compileAndGetClass(Simple1.class), System.out));
        cleanupClassWithinPackage(Simple1.class.getPackageName());
    }

    @Test
    void testCompileGeneric1() throws IOException {
        assertFalse(Reflector.diffClasses(Generic1.class, compileAndGetClass(Generic1.class), System.out));
        cleanupClassWithinPackage(Generic1.class.getPackageName());
    }

    @Test
    void testCompileInterface1() throws IOException {
        assertFalse(Reflector.diffClasses(Interface1.class, compileAndGetClass(Interface1.class), System.out));
        cleanupClassWithinPackage(Interface1.class.getPackageName());
    }

    @Test
    void testCompileNested1() throws IOException {
        assertFalse(Reflector.diffClasses(Nested1.class, compileAndGetClass(Nested1.class), System.out));
        cleanupClassWithinPackage(Nested1.class.getPackageName());
    }

    private void cleanupClassWithinPackage(String packageName) throws IOException {
        Path directory = Paths.get(PREFIX.toString(), packageName.split("."));
        Files.deleteIfExists(Paths.get(directory.toString(), "SomeClass.java"));
    }


    private void compareStructure(Class<?> clazz, String expectedFileName) throws IOException {
        assertDoesNotThrow(() -> Reflector.printStructure(clazz, PREFIX));
        String code = getFileContents(Paths.get(PREFIX.toString(),
                PACKAGE.toString(), "testclasses", "expected", expectedFileName));
        assertEquals(code,
                getFileContents(Paths.get(PREFIX.toString(), "SomeClass.java")));
    }

    private String getFileContents(Path path) throws IOException {
        return Files.lines(path).collect(Collectors.joining(System.lineSeparator()));
    }

    Class<?> compileAndGetClass(Class<?> clazz) throws IOException {
        Path path = Paths.get(PREFIX.toString(), clazz.getPackageName().split("\\."));
        Reflector.printStructure(clazz, path);
        String code = getFileContents(Paths.get(path.toString(), "SomeClass.java"));

        return Reflect.compile(clazz.getPackageName() + ".SomeClass",
                code).type();
    }
}