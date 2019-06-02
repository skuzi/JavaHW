package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.Test;

import java.io.IOException;

public class IgnoredTests {
    @Test(expected = IOException.class, ignore = "1")
    public void ignored1() {

    }

    @Test(expected = NullPointerException.class, ignore = "2")
    public void ignored3() {

    }
}
