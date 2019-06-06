package ru.hse.kuzyaka.cannon;

/**
 * Enum representing a projectile type
 **/
public enum ProjectileType {
    BIG(13, 2, 20),
    MEDIUM(8, 2.4, 10),
    SMALL(3, 2.7, 5);

    private final double radius;
    private final double velocity;
    private final double explosionRadius;

    ProjectileType(double radius, double velocity, double explosionRadius) {
        this.radius = radius;
        this.velocity = velocity;
        this.explosionRadius = explosionRadius;
    }

    public double radius() {
        return radius;
    }

    public double velocity() {
        return velocity;
    }

    public double explosionRadius() {
        return explosionRadius;
    }
}
