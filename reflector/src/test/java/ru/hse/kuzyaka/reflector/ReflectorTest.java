package ru.hse.kuzyaka.reflector;

import org.junit.jupiter.api.Test;
import ru.hse.kuzyaka.reflector.testclasses.ClassWithFields;
import ru.hse.kuzyaka.reflector.testclasses.Dummy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertDoesNotThrow(() -> Reflector.printStructure(Dummy.class, prefix));
        assertEquals("package ru.hse.kuzyaka.reflector.testclasses;\n" +
                "\n" +
                "public class SomeClass {\n" +
                "    public SomeClass() {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "}", getFile(prefix, Dummy.class.getPackageName()));
        shutDown(prefix.toString(), Dummy.class.getPackageName());
    }

    @Test
    void testPrintFields() throws IOException {
        assertDoesNotThrow(() -> Reflector.printStructure(ClassWithFields.class, prefix));
        assertEquals("package ru.hse.kuzyaka.reflector.testclasses;\n" +
                "\n" +
                "public class SomeClass {\n" +
                "    private int a;\n" +
                "    private final int b = 0;\n" +
                "    private static int c;\n" +
                "    public java.lang.String str;\n" +
                "    public final java.lang.Object obj = null;\n" +
                "    public static java.util.List<java.lang.Object> list;\n" +
                "    static final java.lang.Integer d = null;\n" +
                "    \n" +
                "    public SomeClass() {\n" +
                "        \n" +
                "    }\n" +
                "    \n" +
                "}", getFile(prefix, ClassWithFields.class.getPackageName()));
        shutDown(prefix.toString(), ClassWithFields.class.getPackageName());
    }

    @Test
    void testPrintLang() throws IOException {
        assertDoesNotThrow(() -> Reflector.printStructure(HashSet.class, prefix));
        assertEquals("package java.util;\n" +
                "\n" +
                "public class SomeClass <E extends java.lang.Object> extends java.util.AbstractSet<E> implements java.util.Set<E>, java.lang.Cloneable, java.io.Serializable {\n" +
                "    static final long serialVersionUID = 0;\n" +
                "    private transient java.util.HashMap<E, java.lang.Object> map;\n" +
                "    private static final java.lang.Object PRESENT = null;\n" +
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
                "    public boolean remove(java.lang.Object arg0) {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public java.lang.Object clone() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    public void clear() {\n" +
                "    }\n" +
                "    \n" +
                "    public boolean isEmpty() {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public boolean contains(java.lang.Object arg0) {\n" +
                "        return false;\n" +
                "    }\n" +
                "    \n" +
                "    public int size() {\n" +
                "        return 0;\n" +
                "    }\n" +
                "    \n" +
                "    public java.util.Iterator<E> iterator() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    public java.util.Spliterator<E> spliterator() {\n" +
                "        return null;\n" +
                "    }\n" +
                "    \n" +
                "    private void readObject(java.io.ObjectInputStream arg0) throws java.io.IOException, java.lang.ClassNotFoundException {\n" +
                "    }\n" +
                "    \n" +
                "    private void writeObject(java.io.ObjectOutputStream arg0) throws java.io.IOException {\n" +
                "    }\n" +
                "    \n" +
                "}", getFile(prefix, HashSet.class.getPackageName()));
        shutDown(prefix.toString(), HashSet.class.getPackageName());
    }


}