package ru.hse.kuzyaka.reflector;

import org.junit.jupiter.api.Test;
import ru.hse.kuzyaka.reflector.testclasses.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Ref;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    private static Path prefix = Paths.get("src", "test", "java");

    private static String getFile(Path prefix, String packageName) throws IOException {
        return Files.lines(Paths.get(prefix.toString(), packageName, "SomeClass.java")).
                collect(Collectors.joining("\n"));
    }

    void shutDown(String directoryName, String packageName) throws IOException {
        Files.delete(Paths.get(directoryName, packageName, "SomeClass.java"));
    }

    @Test
    void testPrintSimple() throws IOException {
        compareStructure(Dummy.class, "package ru.hse.kuzyaka.reflector.testclasses;\n" +
                "\n" +
                "public class SomeClass {\n" +
                "    public SomeClass() {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "}");
    }

    @Test
    void testPrintFields() throws IOException {
        compareStructure(ClassWithFields.class, "package ru.hse.kuzyaka.reflector.testclasses;\n" +
                "\n" +
                "public class SomeClass {\n" +
                "    private int field1;\n" +
                "    private final int field2 = 0;\n" +
                "    private static int field3;\n" +
                "    static final java.lang.Integer field8 = null;\n" +
                "    public static java.util.List<java.lang.Object> field7;\n" +
                "    public final java.lang.Object obj = field5;\n" +
                "    public java.lang.String field4;\n" +
                "    \n" +
                "    public SomeClass() {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "}");
    }

    @Test
    void testPrintLang() throws IOException {
        compareStructure(HashSet.class, "package java.util;\n" +
                "\n" +
                "public class SomeClass <E extends java.lang.Object> extends java.util.AbstractSet<E> implements java.io.Serializable, java.lang.Cloneable, java.util.Set<E> {\n" +
                "    private static final java.lang.Object PRESENT = null;\n" +
                "    private transient java.util.HashMap<E, java.lang.Object> map;\n" +
                "    static final long serialVersionUID = 0;\n" +
                "    \n" +
                "     SomeClass(int arg0, float arg1, boolean arg2) {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    public SomeClass(int arg0) {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    public SomeClass(int arg0, float arg1) {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    public SomeClass(java.util.Collection<? extends E> arg0) {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    public SomeClass() {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    public boolean add(E arg0) {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public void clear() {\n" +
                "    }\n" +
                "    \n" +
                "    public java.lang.Object clone() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    public boolean contains(java.lang.Object arg0) {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public boolean isEmpty() {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public java.util.Iterator<E> iterator() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    private void readObject(java.io.ObjectInputStream arg0) throws java.io.IOException, java.lang.ClassNotFoundException {\n" +
                "    }\n" +
                "    \n" +
                "    public boolean remove(java.lang.Object arg0) {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public int size() {\n" +
                "        return 0;\n" +
                "    }\n" +
                "    \n" +
                "    public java.util.Spliterator<E> spliterator() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    private void writeObject(java.io.ObjectOutputStream arg0) throws java.io.IOException {\n" +
                "    }\n" +
                "    \n" +
                "}");
    }

    @Test
    void testNested() throws IOException {
        compareStructure(Nested1.class, "package ru.hse.kuzyaka.reflector.testclasses;\n" +
                "\n" +
                "public class SomeClass {\n" +
                "    private int f1;\n" +
                "    \n" +
                "    public SomeClass() {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    private static class NestedA {\n" +
                "        protected final int f1 = 0;\n" +
                "        public int f2;\n" +
                "        public java.lang.String f3;\n" +
                "        \n" +
                "         NestedA(int arg0, int arg1) {\n" +
                "            \n" +
                "        }\n" +
                "        \n" +
                "        \n" +
                "        protected class NestInner {\n" +
                "            public int f1;\n" +
                "            public java.lang.String f2;\n" +
                "            protected final int f3 = 0;\n" +
                "            \n" +
                "            protected NestInner() {\n" +
                "                \n" +
                "            }\n" +
                "            \n" +
                "        }\n" +
                "        \n" +
                "        private static class NestNest {\n" +
                "            protected final int f1 = 0;\n" +
                "            public int f2;\n" +
                "            public java.lang.String f3;\n" +
                "            \n" +
                "             NestNest(int arg0, int arg1) {\n" +
                "                \n" +
                "            }\n" +
                "            \n" +
                "        }\n" +
                "    }\n" +
                "}");
    }

    @Test
    void diffClassesSimple() {
        assertTrue(Reflector.diffClasses(HashSet.class, HashMap.class, System.out));
    }

    @Test
    void testInnerGeneric() throws IOException {
        compareStructure(Generic1.class, "package ru.hse.kuzyaka.reflector.testclasses;\n" +
                "\n" +
                "public class SomeClass <E extends java.util.List<T>, T extends java.lang.Object> {\n" +
                "     T field1;\n" +
                "     E field2;\n" +
                "    \n" +
                "     SomeClass(T arg0) {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "     <B extends java.lang.Object> SomeClass(B arg0, T arg1) {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    public T genericMethod(java.util.List<? super E> arg0, java.util.Map<T, ? extends T> arg1) {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    \n" +
                "    private class Inner extends java.util.AbstractList<E> {\n" +
                "        \n" +
                "        private Inner() {\n" +
                "            \n" +
                "        }\n" +
                "        \n" +
                "        public E get(int arg0) {\n" +
                "            return null;\n" +
                "        }\n" +
                "        \n" +
                "        public int size() {\n" +
                "            return 0;\n" +
                "        }\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "    static class genericNestedClass <B extends java.lang.Object> {\n" +
                "         B field1;\n" +
                "        \n" +
                "         genericNestedClass() {\n" +
                "            \n" +
                "        }\n" +
                "        \n" +
                "    }\n" +
                "}");
    }

    @Test
    void testDiffSame() {
        assertFalse(Reflector.diffClasses(Generic1.class, Generic1Copy.class, System.out));
    }

    @Test
    void testDifferent() {
        var out = new ByteArrayOutputStream();
        assertTrue(Reflector.diffClasses(Simple1.class, Simple2.class, new PrintStream(out)));
        var sOut = out.toString();
        System.out.println(sOut);
        assertEquals(" void kek()\n" +
                "public int y\n" +
                "private void kek()\n" +
                " int y\n", sOut);
    }


    private void compareStructure(Class<?> clazz, String code) throws IOException {
        assertDoesNotThrow(() -> Reflector.printStructure(clazz, prefix));
        assertEquals(code, getFile(prefix, clazz.getPackageName()));
        shutDown(prefix.toString(), clazz.getPackageName());
    }

}