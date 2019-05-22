package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

/** Class representing a target **/
public class Target implements GameObject {
    private Point2D position;
    private double radius;

    /**
     * Creates a circle-shaped target
     *
     * @param position center of target circle
     * @param radius radius of target circle
     */
    public Target(Point2D position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void draw(Renderer renderer) {
        renderer.drawCircle(position, radius);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isAlive() {
        return true;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void move() {

    }
}
