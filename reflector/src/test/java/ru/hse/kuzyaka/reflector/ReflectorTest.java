package ru.hse.kuzyaka.reflector;

import org.junit.jupiter.api.Test;
import ru.hse.kuzyaka.reflector.testclasses.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    private static final Path PREFIX = Paths.get("src", "test", "java");
    private static final Path PACKAGE = Paths.get("ru", "hse", "kuzyaka", "reflector");

    void shutDown(String directoryName) throws IOException {
        Files.delete(Paths.get(directoryName, "SomeClass.java"));
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


    private void compareStructure(Class<?> clazz, String expectedFileName) throws IOException {
        assertDoesNotThrow(() -> Reflector.printStructure(clazz, PREFIX));
        String code = getFileContents(Paths.get(PREFIX.toString(),
                PACKAGE.toString(), "testclasses", "expected", expectedFileName));
        assertEquals(code,
                getFileContents(Paths.get(PREFIX.toString(), "SomeClass.java")));
        shutDown(PREFIX.toString());
    }

    private String getFileContents(Path path) throws IOException {
        return Files.lines(path).collect(Collectors.joining(System.lineSeparator()));
    }
}