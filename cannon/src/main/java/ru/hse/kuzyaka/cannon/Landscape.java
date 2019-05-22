package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class Landscape implements GameObject {
    private List<Line> lines = new ArrayList<>();
    private List<Point2D> points = new ArrayList<>();

    public Landscape(List<Point2D> points) {
        this.points.addAll(points);
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new Line(points.get(i), points.get(i + 1)));
        }
    }

    public Line getLineByPoint(Point2D position) {
        for (var line : lines) {
            if (line.getBegin().getX() <= position.getX() && line.getEnd().getX() >= position.getX()) {
                return line;
            }
        }
        return null;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void addLine(Line line) {
        lines.add(line);
    }

    public List<Point2D> getPoints() {
        return points;
    }

    public double getMaxX() {
        return points.get(points.size() - 1).getX();
    }

    public double getMinX() {
        return points.get(0).getX();
    }

    @Override
    public void draw(Renderer renderer) {
        lines.forEach(renderer::drawLine);
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void move() {
    }
}
