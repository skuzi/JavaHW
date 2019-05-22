package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

public class Projectile implements GameObject {
    private static final double GRAVITY = 0.01;
    private ProjectileType type;
    private Point2D velocity;
    private Point2D position;
    private Landscape landscape;


    public Projectile(ProjectileType type, Point2D velocity, Point2D position, Landscape landscape) {
        this.type = type;
        this.velocity = velocity;
        this.position = position;
        this.landscape = landscape;
    }

    public boolean hasFallen() {
        var line = landscape.getLineByPoint(position);
        if (line == null) {
            return true;
        }
        return line.distance(position) < type.radius() || line.getYbyX(position.getX()) < position.getY();
    }

    @Override
    public void draw(Renderer renderer) {
        renderer.drawCircle(position, type.radius());
    }

    @Override
    public boolean isAlive() {
        return hasFallen();
    }

    @Override
    public void move() {
        position = position.add(velocity);
        velocity = velocity.add(0, -GRAVITY);
    }

    public Bang makeBang() {
        return new Bang(position, type.explosionRadius(), 20);
    }

}
