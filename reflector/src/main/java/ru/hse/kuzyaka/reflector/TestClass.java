package ru.hse.kuzyaka.reflector;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class TestClass<E extends java.lang.Object> {
    private int i;
    private static int j;

    int k;
    final public String str = "abc";
    final public Integer integer = null;
    public E x;
    private final E y = null;

    private <U extends E, V extends U> E fun(ArrayList<? super E> x, V y) {
        return null;
    }

    public static void main(String[] args) {
    }

    public abstract void lol();

    public static class NestedClass {
        private int abc;
        public static void uuu(TestClass x) {
            int a = 2 + 3;
        }
        final int t = 5;
        Integer returnInt() {
            return null;
        }
        protected class Iter implements Iterator {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        }
    }
    protected class Iter1 implements Iterator<E> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public E next() {
            return null;
        }
        private class Innter1 {
            private int o;
        }
    }
}
