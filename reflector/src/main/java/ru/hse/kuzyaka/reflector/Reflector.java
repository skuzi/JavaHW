package ru.hse.kuzyaka.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Class for printing a structure of other classes and finding differences between them. */
public class Reflector {
    private static final String SOME_CLASS = "SomeClass";
    private static final int SPACE_IN_TAB = 4;
    private static final String SEPARATOR = System.lineSeparator();
    private static StringBuilder out;
    private static int indent;
    private static String tab;

    /**
     * Prints a structure of a given class to the specified path
     *
     * @param someClass class which is printed
     * @param path path used to written class to
     * @throws IOException if an I/O error occurs
     */
    public static void printStructure(@NotNull Class<?> someClass, @NotNull Path path) throws IOException {
        out = new StringBuilder();
        printPackage(someClass);
        printClass(someClass, SOME_CLASS);
        writeClass(someClass, path);
    }

    /**
     * Prints a differences between classes.
     * More precisely, prints all declared fields and methods unique in the first class, then the same for the second class
     *
     * @param firstClass class which unique fields and methods are printed first
     * @param secondClass class which unique field and methods are printed second
     * @param writer PrintStream used as output
     * @return {@code true} if classes are different, {@code false} otherwise
     */
    public static boolean diffClasses(@NotNull Class<?> firstClass, @NotNull Class<?> secondClass, PrintStream writer) {
        return differentMethods(secondClass, firstClass, writer) |
                differentFields(secondClass, firstClass, writer) |
                differentMethods(firstClass, secondClass, writer) |
                differentFields(firstClass, secondClass, writer);
    }

    private static boolean differentFields(Class<?> firstClass, Class<?> secondClass, PrintStream writer) {
        var fieldsFirst = Arrays.stream(firstClass.getDeclaredFields()).
                map(Reflector::fieldToString).collect(Collectors.toCollection(HashSet::new));

        var secondFields = secondClass.getDeclaredFields();
        Arrays.sort(secondFields, Comparator.comparing(Field::getName));
        return Arrays.stream(secondFields).
                map(Reflector::fieldToString).
                filter(s -> !fieldsFirst.contains(s)).
                peek(writer::println).count() > 0;
    }

    private static boolean differentMethods(Class<?> firstClass, Class<?> secondClass, PrintStream writer) {
        var firstMethods = Arrays.stream(firstClass.getDeclaredMethods()).
                map(Reflector::methodToString).collect(Collectors.toCollection(HashSet::new));

        var secondMethods = secondClass.getDeclaredMethods();
        Arrays.sort(secondMethods, Comparator.comparing(Method::getName));
        return Arrays.stream(secondMethods).
                map(Reflector::methodToString).
                filter(s -> !firstMethods.contains(s)).
                peek(writer::println).count() > 0;
    }


    private static void printClass(Class<?> someClass, String className) {
        out.append(SEPARATOR);
        printClassHeader(someClass, className);
        printClassFields(someClass);
        printClassConstructors(someClass, className);
        printClassMethods(someClass);

        Class<?>[] declaredClasses = someClass.getDeclaredClasses();
        Arrays.sort(declaredClasses, Comparator.comparing(Class::getName));
        for (var inner : declaredClasses) {
            printClass(inner, inner.getSimpleName());
        }

        out.append("}" + SEPARATOR);
    }

    private static void printClassHeader(Class<?> someClass, String className) {
        out.append(Modifier.toString(someClass.getModifiers())).append(" ").
                append(someClass.isInterface() ? "" : "class ").
                append(className)
                .append(genericSignature(someClass.getTypeParameters()));
        addImplementedAndExtended(someClass);
        out.append(" {" + SEPARATOR);
    }

    private static void addImplementedAndExtended(Class<?> someClass) {
        Type superclass = someClass.getGenericSuperclass();
        if (superclass != null && !superclass.getTypeName().equals("java.lang.Object")) {
            out.append(" extends ").append(superclass.getTypeName().replace('$', '.'));
        }
        var interfaces = someClass.getGenericInterfaces();
        Arrays.sort(interfaces, Comparator.comparing(Type::getTypeName));
        if (interfaces.length > 0) {
            out.append(someClass.isInterface() ? " extends " : " implements ").
                    append(Arrays.stream(interfaces).
                            map((t) -> t.getTypeName().replace('$', '.')).
                            collect(Collectors.joining(", ", "", "")));
        }
    }

    private static void printClassMethods(Class<?> someClass) {
        var declaredMethods = someClass.getDeclaredMethods();
        Arrays.sort(declaredMethods, Comparator.comparing(Method::getName));
        for (var method : declaredMethods) {
            if (method.isSynthetic()) {
                continue;
            }
            int modifiers = method.getModifiers();
            out.append(methodToString(method));
            printExceptions(method);
            if (!Modifier.isAbstract(modifiers)) {
                out.append(" {" + SEPARATOR);
                if (!method.getGenericReturnType().getTypeName().equals("void")) {
                    out.append("return ").
                            append(getDefaultValue(method.getReturnType())).
                            append(";" + SEPARATOR);
                }
                out.append("}");
            } else {
                out.append(";");
            }
            out.append(SEPARATOR + SEPARATOR);
        }
    }

    private static void printExceptions(Method method) {
        var exceptions = method.getGenericExceptionTypes();
        Arrays.sort(exceptions, Comparator.comparing(Type::getTypeName));
        if (exceptions.length > 0) {
            out.append(" throws ");
            out.append(Arrays.stream(exceptions).
                    map(t -> t.getTypeName().replace('$', '.')).
                    collect(Collectors.joining(", ", "", "")));
        }
    }

    private static String methodToString(Method method) {
        return Modifier.toString(method.getModifiers()) +
                genericSignature(method.getTypeParameters()) +
                " " + method.getGenericReturnType().getTypeName().replace('$', '.') +
                " " + method.getName() +
                parametersList(method.getParameters(), method.getGenericParameterTypes(), false);
    }

    private static String parametersList(Parameter[] parameters, Type[] parameterTypes, boolean isInner) {
        Stream.Builder<String> builder = Stream.builder();
        int start = isInner ? 1 : 0;
        for (int i = start; i < parameters.length; i++) {
            builder.add(parameterTypes[i].getTypeName().replace('$', '.') +
                    " " + parameters[i].getName());
        }
        return builder.build().collect(Collectors.joining(", ", "(", ")"));
    }

    private static String genericSignature(TypeVariable<?>[] typeVariables) {
        Arrays.sort(typeVariables, Comparator.comparing(Type::getTypeName));
        if (typeVariables.length == 0) {
            return "";
        }
        return Arrays.stream(typeVariables).
                map((variable) -> variable.getName() +
                        " extends " +
                        Arrays.stream(variable.getBounds()).
                                map(t -> t.getTypeName().replace('$', '.')).
                                collect(Collectors.joining(", "))).
                collect(Collectors.joining(", ", " <", ">"));
    }

    private static void printClassFields(Class<?> someClass) {
        var declaredFields = someClass.getDeclaredFields();
        Arrays.sort(declaredFields, Comparator.comparing(Field::getName));
        for (var field : declaredFields) {
            if (field.isSynthetic()) {
                continue;
            }
            out.append(fieldToString(field));
            if (Modifier.isFinal(field.getModifiers())) {
                out.append(" = ").append(getDefaultValue(field.getType()));
            }
            out.append(";" + SEPARATOR);
        }
        if (declaredFields.length > 0) {
            out.append(SEPARATOR);
        }
    }

    private static String getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) {
                return "false";
            } else {
                return "0";
            }
        } else {
            return "null";
        }
    }

    private static String fieldToString(Field field) {
        return Modifier.toString(field.getModifiers()) + " " +
                field.getGenericType().getTypeName().replace('$', '.') +
                " " + field.getName();
    }

    private static void printClassConstructors(Class<?> someClass, String className) {
        var constructors = someClass.getDeclaredConstructors();
        Arrays.sort(constructors, Comparator.comparing(Constructor::getName));
        for (var constructor : constructors) {
            out.append(constructorToString(constructor, className,
                    someClass.getEnclosingClass() != null && !Modifier.isStatic(someClass.getModifiers())));
            out.append(" {" + SEPARATOR + SEPARATOR + "}" + SEPARATOR);
            out.append(SEPARATOR);
        }
    }

    private static String constructorToString(Constructor<?> constructor, String className, boolean isInner) {
        return Modifier.toString(constructor.getModifiers()) +
                genericSignature(constructor.getTypeParameters()) +
                " " + className +
                parametersList(constructor.getParameters(), constructor.getGenericParameterTypes(), isInner);
    }

    private static void printPackage(Class<?> someClass) {
        out.append("package ").append(someClass.getPackageName()).append(";" + SEPARATOR);
    }

    private static void increaseIndent() {
        indent++;
    }

    private static void decreaseIndent() {
        indent--;
    }

    private static void writeClass(Class<?> someClass, @NotNull Path path) throws IOException {
        Path fullPath = Paths.get(path.toString());
        File file = new File(fullPath.toString(),SOME_CLASS + ".java");

        var writer = new FileWriter(file);

        String clazz = out.toString();
        var lines = clazz.split(SEPARATOR);
        for (var line : lines) {
            if (line.equals("")) {
                writer.write(SEPARATOR);
                continue;
            }

            if (line.endsWith("}")) {
                decreaseIndent();
            }
            writer.write(getIndent());
            writer.write(line.replaceAll("\\b" +
                    someClass.getPackageName() + "\\." +
                    someClass.getSimpleName() + "\\b", SOME_CLASS));
            writer.write(SEPARATOR);
            if (line.endsWith("{")) {
                increaseIndent();
            }
        }
        indent = 0;
        writer.flush();
        writer.close();
    }

    private static void setTab() {
        StringBuilder tabBuilder = new StringBuilder();
        for (int i = 0; i < SPACE_IN_TAB; i++) {
            tabBuilder.append(' ');
        }
        tab = tabBuilder.toString();
    }

    private static String getIndent() {
        var indentBuilder = new StringBuilder();
        if (tab == null) {
            setTab();
        }
        for (int i = 0; i < indent; i++) {
            indentBuilder.append(tab);
        }
        return indentBuilder.toString();
    }
}
