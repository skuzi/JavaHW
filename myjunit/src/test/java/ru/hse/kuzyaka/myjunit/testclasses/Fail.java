package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.Test;

import java.io.IOException;

public class Fail {
    @Test(expected = IOException.class)
    public void fail() {
        throw new IndexOutOfBoundsException();
    }
}
