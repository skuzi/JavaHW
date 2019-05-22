package ru.hse.kuzyaka.cannon;

public enum Direction {
    LEFT(-1), RIGHT(1), NONE(0);

    private double value;

    Direction(int i) {
        value = i;
    }

    public double getValue() {
        return value;
    }
}
