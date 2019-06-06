package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

/** Class representing a landscape **/
public class Landscape implements GameObject {
    private List<Line> lines = new ArrayList<>();
    private List<Point2D> points = new ArrayList<>();

    /**
     * Creates the landscape
     *
     * @param points points on which the landscape is based, resulting points are sorted by x coordinate
     */
    public Landscape(List<Point2D> points) {
        this.points.addAll(points);
        this.points.sort((firstPoint, secondPoint) -> (int) (firstPoint.getX() - secondPoint.getX()));
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new Line(points.get(i), points.get(i + 1)));
        }
    }

    /**
     * Returns a line such that its begin is left to the given point and its end is right to the given point
     *
     * @param position point to compare to
     * @return such line, or {@code null} if there is no such line (i.e. point is out of boundaries)
     */
    public Line getLineByPoint(Point2D position) {
        for (var line : lines) {
            if (line.getBegin().getX() <= position.getX() && line.getEnd().getX() >= position.getX()) {
                return line;
            }
        }
        return null;
    }

    /**
     * Returns maximal x coordinate of this landscape
     *
     * @return maximal x coordinate
     */
    public double getMaxX() {
        return points.get(points.size() - 1).getX();
    }

    /**
     * Returns minimal x coordinate of this landscape
     *
     * @return minimum x coordinate
     */
    public double getMinX() {
        return points.get(0).getX();
    }

    /** {@inheritDoc} **/
    @Override
    public void draw(Renderer renderer) {
        lines.forEach(renderer::drawLine);
    }

    /** {@inheritDoc} **/
    @Override
    public boolean isAlive() {
        return true;
    }

    /** {@inheritDoc} **/
    @Override
    public void move() {
    }
}
