package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

/** Class representing an explosion **/
public class Bang implements GameObject {
    private Point2D center;
    private double radius;
    private int numberOfFrames;
    private boolean isAlive = true;
    private int currentFrame;
    private double scale;

    /**
     * Creates an explosion with the given center, radius and a number of living stages
     *
     * @param center center
     * @param radius radius
     * @param numberOfFrames number of stages to live
     */
    public Bang(Point2D center, double radius, int numberOfFrames) {
        this.center = center;
        this.radius = radius;
        this.numberOfFrames = numberOfFrames;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void draw(Renderer renderer) {
        currentFrame++;
        renderer.drawCircle(center, radius);
        if (currentFrame == numberOfFrames) {
            isAlive = false;
        }
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void move() {

    }

    /**
     * Shows whether this bang hit the given target which means that their circles intercept
     *
     * @param target center of target circle
     * @param targetRadius radius of target circle
     * @return {@code true} if the bang has hit the target
     */
    public boolean hasHit(Point2D target, double targetRadius) {
        return center.distance(target) < radius + targetRadius;
    }
}
