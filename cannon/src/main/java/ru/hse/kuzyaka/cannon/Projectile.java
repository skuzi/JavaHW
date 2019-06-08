package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

/** Class representing projectile **/
public class Projectile implements GameObject {
    private static final double GRAVITY = 0.01;
    private ProjectileType type;
    private Point2D velocity;
    private Point2D position;
    private Landscape landscape;

    /**
     * Creates projectile with given type, velocity, position and landscape
     *
     * @param type type of projectile
     * @param velocity velocity
     * @param position coordinate
     * @param landscape landscape
     */
    public Projectile(ProjectileType type, Point2D velocity, Point2D position, Landscape landscape) {
        this.type = type;
        this.velocity = velocity;
        this.position = position;
        this.landscape = landscape;
    }

    /**
     * Tells whether this projectile has fallen to the ground
     *
     * @return {@code true} if projectile has fallen
     */
    public boolean hasFallen() {
        var line = landscape.getLineByPoint(position);
        if (line == null) {
            return true;
        }
        return line.distance(position) < type.radius() || line.getYbyX(position.getX()) > position.getY();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void draw(Renderer renderer) {
        renderer.drawCircle(position, type.radius());
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isAlive() {
        return !hasFallen();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void move() {
        position = position.add(velocity);
        velocity = velocity.add(0, -GRAVITY);
    }

    /**
     * Makes a bang
     *
     * @return new Bang with start coordinate equal to this projectile current coordinate, and 20 stages of living
     */
    public Bang makeBang() {
        return new Bang(position, type.explosionRadius(), 20);
    }

}
