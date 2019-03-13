package ru.hse.kuzyaka.reflector.testclasses;

public class Nested1 {
    private int f1;

    private static class NestedA {
        protected final int f1 = 5;
        public int f2;
        public String f3;
        NestedA(int a, int b) {
        }

        private static class NestNest {
            protected final int f1 = 5;
            public int f2;
            public String f3;
            NestNest(int a, int b) {
            }
        }

        protected class NestInner {
            public int f1;
            public String f2;
            protected final int f3 = 5;
        }
    }
}
