package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/** Class representing a line **/
public class Line {
    private Point2D begin;
    private Point2D end;
    private double gradient;
    private double intercept;

    /**
     * Creates a line
     *
     * @param begin begin of the line
     * @param end end of the line
     */
    public Line(Point2D begin, Point2D end) {
        this.begin = begin;
        this.end = end;
        gradient = (end.getY() - begin.getY()) / (end.getX() - begin.getX());
        intercept = begin.getY() - gradient * begin.getX();
    }

    /**
     * Returns the y coordinate of the given x
     *
     * @param x x coordinate
     * @return y coordinate
     */
    public double getYbyX(double x) {
        return gradient * x + intercept;
    }

    /**
     * Calculates distance to the given point
     *
     * @param p point
     * @return distance to the point
     */
    public double distance(Point2D p) {
        return (abs(gradient * p.getX() + intercept - p.getY())) / sqrt(gradient * gradient + 1);
    }

    public Point2D getBegin() {
        return begin;
    }

    public Point2D getEnd() {
        return end;
    }

    public double getGradient() {
        return gradient;
    }

}
