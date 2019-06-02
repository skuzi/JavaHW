package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.Test;

public class NotThrowingTestsClass {
    private int val;

    @Test
    public static void f() {
    }

    @Test
    public void g() {
        val = 3;
    }
}
