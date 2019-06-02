package ru.hse.kuzyaka.myjunit;

import ru.hse.kuzyaka.myjunit.annotations.Test;

import java.io.IOException;

public class TestClass {
    @Test(expected = IOException.class)
    public static void bSimple() throws IOException {
        throw new IOException();
    }

    @Test(expected = NullPointerException.class)
    public void aSimple() {
        throw new NullPointerException();
    }

    @Test(expected = Exception.class)
    public void cSubclass() {
        throw new IndexOutOfBoundsException();
    }
}
