package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.Test;

import java.io.IOException;

public class ThrowingTestsClass {
    @Test(expected = IOException.class)
    public static void f() throws IOException {
        throw new IOException();
    }

    @Test(expected = IllegalArgumentException.class)
    public void g() {
        throw new IllegalArgumentException();
    }

    @Test(expected = Exception.class)
    public void h() {
        throw new NullPointerException();
    }
}
