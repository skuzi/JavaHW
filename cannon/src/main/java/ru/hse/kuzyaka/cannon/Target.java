package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

public class Target implements GameObject {
    private Point2D position;
    private double radius;

    public Target(Point2D position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.drawCircle(position, radius);
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void move() {

    }
}
