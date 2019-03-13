package ru.hse.kuzyaka.reflector.testclasses;

import java.util.AbstractList;
import java.util.List;
import java.util.Map;

public class Generic1Copy<T, E extends List<T>> {
    T field1;
    E field2;

    Generic1Copy(T arg0) {
    }

    <B> Generic1Copy(B arg0, T arg1) {
    }

    public T genericMethod(List<? super E> list, Map<T, ? extends T> map) {
        return null;
    }

    static class genericNestedClass<B> {
        B field1;
    }

    private class Inner extends AbstractList<E> {
        public E get(int index) {
            return null;
        }

        public int size() {
            return 0;
        }
    }
}
