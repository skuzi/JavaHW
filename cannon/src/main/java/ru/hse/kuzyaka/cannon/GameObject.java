package ru.hse.kuzyaka.cannon;

public interface GameObject {
    void draw(Renderer renderer);
    boolean isAlive();
    void move();
}
