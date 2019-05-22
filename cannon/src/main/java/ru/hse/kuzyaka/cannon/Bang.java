package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

public class Bang implements GameObject {
    private Point2D center;
    private double radius;
    private int numberOfFrames;
    private boolean isAlive = true;
    private int currentFrame;
    private double scale;

    public Bang(Point2D center, double radius, int numberOfFrames) {
        this.center = center;
        this.radius = radius;
        this.numberOfFrames = numberOfFrames;
    }

    @Override
    public void draw(Renderer renderer) {
        currentFrame++;
        renderer.drawCircle(center, radius);
        if (currentFrame == numberOfFrames) {
            isAlive = false;
        }
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void move() {

    }

    public boolean hasHit(Point2D target, double targetRadius) {
        return center.distance(target) < radius + targetRadius;
    }
}
