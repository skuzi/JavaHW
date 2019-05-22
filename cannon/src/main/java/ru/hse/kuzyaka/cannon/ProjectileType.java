package ru.hse.kuzyaka.cannon;

public enum ProjectileType {
    BIG {
        @Override
        public double radius() {
            return 13;
        }

        @Override
        public double velocity() {
            return 2;
        }

        @Override
        public double explosionRadius() {
            return 20;
        }
    },
    MEDIUM {
        @Override
        public double radius() {
            return 8;
        }

        @Override
        public double velocity() {
            return 2.4;
        }

        @Override
        public double explosionRadius() {
            return 10;
        }
    },
    SMALL {
        @Override
        public double radius() {
            return 3;
        }

        @Override
        public double velocity() {
            return 2.7;
        }

        @Override
        public double explosionRadius() {
            return 5;
        }
    };

    public abstract double radius();

    public abstract double velocity();

    public abstract double explosionRadius();
}
