package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.After;

public class BadAfter {
    @After
    public static void after(int a, int b) {

    }
}
