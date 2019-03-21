package ru.hse.kuzyaka.reflector.testclasses;

import java.util.ArrayList;
import java.util.List;

public class ClassWithFields {
    private int field1;
    private final int field2 = 1;
    private static int field3;
    public String field4;
    public final Object field5 = null;
    public static List<Object> field7 = new ArrayList<>();
    static final Integer field8 = null;
}
