package ru.hse.kuzyaka.reflector.testclasses;

public class Nested1 {
    private int i;

    private static class NestedA {
        protected final int z = 5;
        public int x;
        public String y;
        NestedA(int a, int b) {
        }

        private static class NestNest {
            protected final int z = 5;
            public int x;
            public String y;
            NestNest(int a, int b) {
            }
        }

        protected class NestInner {
            public int x;
            public String y;
            protected final int z = 5;
        }
    }
}
