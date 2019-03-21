package ru.hse.kuzyaka.reflector.testclasses;

import java.io.Serializable;

public interface Interface1 extends Serializable, Runnable {
    int x = 5;

    void run();
    void println();

    interface InnerInterface {
        String s = null;
    }
}
