package ru.hse.kuzyaka.cannon;

/** Interface for game objects **/
public interface GameObject {
    /** Draws the object using the given renderer **/
    void draw(Renderer renderer);

    /**
     * Tells whether this object is alive
     *
     * @return {@code true} if the object is alive (i.e. it should be drawn)
     */
    boolean isAlive();

    /** Moves the object **/
    void move();
}
