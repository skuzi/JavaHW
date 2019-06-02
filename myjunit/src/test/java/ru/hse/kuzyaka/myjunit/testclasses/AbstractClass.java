package ru.hse.kuzyaka.myjunit.testclasses;

import ru.hse.kuzyaka.myjunit.annotations.Test;

public abstract class AbstractClass {
    @Test(expected = Exception.class)
    public void a() {
        throw new IllegalArgumentException();
    }
}
