package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

import static java.lang.Math.*;

public class Cannon implements GameObject {
    public static final double VELOCITY = 1.5;
    public static final double ROTATE_SPEED = 0.05;
    public static final double RADIUS = 10;
    public static final int BARREL_HEIGHT = 30;
    public static final int BARREL_WIDTH = 8;

    private Point2D position;
    private ProjectileType projectileType;
    private Direction cannonDirection = Direction.NONE;
    private Direction barrelDirection = Direction.NONE;
    private Landscape landscape;
    private double angle = 0;

    public Cannon(Point2D position, Landscape landscape, ProjectileType projectileType) {
        this.position = position;
        this.landscape = landscape;
        this.projectileType = projectileType;
    }

    public void updateAngle(double angleDelta) {
        angle += angleDelta;
    }

    private double getInBorders(double x) {
        if (x < landscape.getMinX()) {
            return landscape.getMinX();
        }

        if (x > landscape.getMaxX()) {
            return landscape.getMaxX();
        }

        return x;
    }

    public Projectile shoot() {
        return new Projectile(projectileType,
                new Point2D(projectileType.velocity() * cos(angle),
                        projectileType.velocity() * sin(angle)),
                position.add(BARREL_HEIGHT * cos(angle), RADIUS - 6 + BARREL_HEIGHT * sin(angle)), landscape);
    }

    public void setProjectileType(ProjectileType projectileType) {
        this.projectileType = projectileType;
    }

    @Override
    public void draw(Renderer renderer) {
        Point2D barrelEnd = new Point2D(Math.cos(angle), Math.sin(angle)).multiply(BARREL_HEIGHT);
        renderer.drawCircle(position, RADIUS);
        Line firstLineOfBarrel = new Line(position, position.add(barrelEnd));
        Point2D normalVector = new Point2D(abs(firstLineOfBarrel.getGradient()), 1).normalize().multiply(BARREL_WIDTH);
        Line secondLineOfBarrel = new Line(position.add(normalVector), position.add(barrelEnd).add(normalVector));
        renderer.drawLine(firstLineOfBarrel);
        renderer.drawLine(secondLineOfBarrel);
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void move() {
        position = position.add(VELOCITY * cannonDirection.getValue(), 0);
        position = new Point2D(getInBorders(position.getX()), position.getY());
        Line line = landscape.getLineByPoint(position);
        position = new Point2D(position.getX(), line.getYbyX(position.getX()));
        updateAngle(ROTATE_SPEED * barrelDirection.getValue());
    }

    public void setCannonDirection(Direction direction) {
        this.cannonDirection = direction;
    }

    public void setBarrelDirection(Direction direction) {
        this.barrelDirection = direction;
    }
}
