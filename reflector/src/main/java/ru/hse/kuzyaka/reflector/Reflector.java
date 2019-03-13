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

public class Reflector {
    private static final String SOME_CLASS = "SomeClass";
    private static final int SPACE_IN_TAB = 4;
    private static StringBuilder out;
    private static int indent;
    private static String tab;

    public static void printStructure(@NotNull Class<?> someClass, @NotNull Path path) throws IOException {
        out = new StringBuilder();
        printPackage(someClass);
        printClass(someClass, SOME_CLASS);
        writeClass(someClass, path);
    }

    public static boolean diffClasses(@NotNull Class<?> firstClass, @NotNull Class<?> secondClass, PrintStream writer) {
        return differentMethods(firstClass, secondClass, writer) ||
                differentFields(firstClass, secondClass, writer) ||
                differentMethods(secondClass, firstClass, writer) ||
                differentFields(secondClass, firstClass, writer);
    }

    private static boolean differentFields(Class<?> firstClass, Class<?> secondClass, PrintStream writer) {
        var fieldsFirst = Arrays.stream(firstClass.getDeclaredFields()).
                map(Reflector::fieldToString).collect(Collectors.toCollection(HashSet::new));

        return Arrays.stream(secondClass.getDeclaredFields()).
                map(Reflector::fieldToString).
                filter(s -> !fieldsFirst.contains(s)).
                peek(writer::println).count() > 0;
    }

    private static boolean differentMethods(Class<?> firstClass, Class<?> secondClass, PrintStream writer) {
        var methodsFirst = Arrays.stream(firstClass.getDeclaredMethods()).
                map(Reflector::methodToString).collect(Collectors.toCollection(HashSet::new));

        return Arrays.stream(secondClass.getDeclaredMethods()).
                map(Reflector::methodToString).
                filter(s -> !methodsFirst.contains(s)).
                peek(writer::println).count() > 0;
    }


    private static void printClass(Class<?> someClass, String className) {
        out.append("\n");
        printClassHeader(someClass, className);
        printClassFields(someClass);
        printClassConstructors(someClass, className);
        printClassMethods(someClass);

        for (var inner : someClass.getDeclaredClasses()) {
            printClass(inner, inner.getSimpleName());
        }

        out.append("}\n");
    }

    private static void printClassHeader(Class<?> someClass, String className) {
        out.append(Modifier.toString(someClass.getModifiers())).append(" ").
                append(someClass.isInterface() ? "" : "class ").
                append(className)
                .append(genericSignature(someClass.getTypeParameters()));
        addImplementedAndExtended(someClass);
        out.append(" {\n");
    }

    private static void addImplementedAndExtended(Class<?> someClass) {
        Type superclass = someClass.getGenericSuperclass();
        if (superclass != null && !superclass.getTypeName().equals("java.lang.Object")) {
            out.append(" extends ").append(superclass.getTypeName().replace('$', '.'));
        }
        var interfaces = someClass.getGenericInterfaces();
        if (interfaces.length > 0) {
            out.append(someClass.isInterface() ? " extends " : " implements ").
                    append(Arrays.stream(interfaces).
                            map((t) -> t.getTypeName().replace('$', '.')).
                            collect(Collectors.joining(", ", "", "")));
        }
    }

    private static void printClassMethods(Class<?> someClass) {
        for (var method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            int modifiers = method.getModifiers();
            out.append(methodToString(method));
            printExceptions(method);
            if (!Modifier.isAbstract(modifiers)) {
                out.append(" {\n");
                if (!method.getGenericReturnType().getTypeName().equals("void")) {
                    out.append("return ").
                            append(getDefaultValue(method.getReturnType())).
                            append(";\n");
                }
                out.append("}");
            } else {
                out.append(";");
            }
            out.append("\n\n");
        }
    }

    private static void printExceptions(Method method) {
        var exceptions = method.getGenericExceptionTypes();
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
        for (var field : someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            out.append(fieldToString(field));
            if (Modifier.isFinal(field.getModifiers())) {
                out.append(" = ").append(getDefaultValue(field.getType()));
            }
            out.append(";\n");
        }
        if (someClass.getDeclaredFields().length > 0) {
            out.append("\n");
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
        return (field.getModifiers() != 0 ? Modifier.toString(field.getModifiers()) + " " : "") +
                field.getGenericType().getTypeName().replace('$', '.') +
                " " + field.getName();
    }

    private static void printClassConstructors(Class<?> someClass, String className) {
        var constructors = someClass.getDeclaredConstructors();
        for (var constructor : constructors) {
            out.append(constructorToString(constructor, className,
                    someClass.getEnclosingClass() != null && !Modifier.isStatic(someClass.getModifiers())));
            out.append(" {\n\n}\n");
            out.append("\n");
        }
    }

    private static String constructorToString(Constructor<?> constructor, String className, boolean isInner) {
        return Modifier.toString(constructor.getModifiers()) +
                genericSignature(constructor.getTypeParameters()) +
                " " + className +
                parametersList(constructor.getParameters(), constructor.getGenericParameterTypes(), isInner);
    }

    private static void printPackage(Class<?> someClass) {
        out.append("package ").append(someClass.getPackageName()).append(";\n");
    }

    private static void increaseIndent() {
        indent++;
    }

    private static void decreaseIndent() {
        indent--;
    }

    private static void writeClass(Class<?> someClass, @NotNull Path path) throws IOException {
        Path fullPath = Paths.get(path.toString(), someClass.getPackageName());
        Files.createDirectories(fullPath);
        File file = new File(fullPath.toString(),SOME_CLASS + ".java");

        var writer = new FileWriter(file);

        String clazz = out.toString();
        var lines = clazz.split("\n");
        for (var line : lines) {
            if (line.endsWith("}")) {
                decreaseIndent();
            }
            writer.write(getIndent());
            writer.write(line.replaceAll("\\b" +
                    someClass.getPackageName() + "\\." +
                    someClass.getSimpleName() + "\\b", SOME_CLASS).
                    replaceAll("<T", "<E"));
            writer.write("\n");
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
