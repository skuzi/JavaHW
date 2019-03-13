package ru.hse.kuzyaka.reflector.testclasses;

import java.util.ArrayList;
import java.util.List;

public class ClassWithFields {
    private int a;
    private final int b = 1;
    private static int c;
    public String str;
    public final Object obj = null;
    public static List<Object> list = new ArrayList<>();
    static final Integer d = null;
}
