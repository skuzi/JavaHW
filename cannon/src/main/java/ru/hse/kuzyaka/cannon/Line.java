package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

public class Line {
    private Point2D begin;
    private Point2D end;
    private double gradient;
    private double intercept;

    public Line(Point2D begin, Point2D end) {
        this.begin = begin;
        this.end = end;
        gradient = (end.getY() - begin.getY()) / (end.getX() - begin.getX());
        intercept = begin.getY() - gradient * begin.getX();
    }

    public double getYbyX(double x) {
        return gradient * x + intercept;
    }

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
