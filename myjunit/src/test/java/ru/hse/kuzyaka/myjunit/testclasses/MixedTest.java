package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.Test;

import java.io.IOException;

public class MixedTest {
    @Test(expected = IOException.class)
    public static void fail1() {
        throw new NullPointerException();
    }

    @Test(expected = NullPointerException.class)
    public static void fail2() {
        throw new IndexOutOfBoundsException();
    }

    @Test(expected = IllegalArgumentException.class)
    public static void pass() {
        throw new IllegalArgumentException();
    }
}
