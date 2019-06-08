package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Class for rendering game objects
 **/
public class Renderer {
    private GraphicsContext graphicsContext;
    private double screenWidth;
    private double screenHeight;
    private double screenHeightInMeters;
    private double scale;

    /**
     * Creates renderer with given graphicsContext, width and height in pixels, and width in meters
     *
     * @param graphicsContext graphicsContext
     * @param width width in pixels
     * @param height height in pixels
     * @param widthInMeters width in meters
     */
    public Renderer(GraphicsContext graphicsContext, int width, int height, int widthInMeters) {
        screenWidth = width;
        screenHeight = height;
        scale = 1.0 * screenWidth / (double) widthInMeters;
        screenHeightInMeters = screenHeight / scale;
        this.graphicsContext = graphicsContext;
        this.graphicsContext.setFont(Font.font(24));
    }

    private double translate(double value) {
        return value * scale;
    }

    private double translateX(double x) {
        return x * scale;
    }

    private double translateY(double y) {
        return (screenHeightInMeters - y) * scale;
    }

    /**
     * Draws a line
     *
     * @param line line to draw
     */
    public void drawLine(Line line) {
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(translateX(line.getBegin().getX()), translateY(line.getBegin().getY()),
                translateX(line.getEnd().getX()), translateY(line.getEnd().getY()));
    }

    /**
     * Draws a circle
     *
     * @param center center of the circle
     * @param radius radius of the circle
     */
    public void drawCircle(Point2D center, double radius) {
        double r = translate(radius);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(translateX(center.getX()) - r,
                translateY(center.getY()) - r, 2 * r, 2 * r);
    }

    /**
     * Draws a rectangle using give array of points as its vertices
     *
     * @param points vertices of rectangle
     * @throws IllegalArgumentException if length of {@code points} is not equal to 4
     */
    public void drawRectangle(Point2D[] points) throws IllegalArgumentException {
        if (points.length != 4) {
            throw new IllegalArgumentException();
        }
        double[] pointsX = Arrays.stream(points).mapToDouble(point -> translateX(point.getX())).toArray();
        double[] pointsY = Arrays.stream(points).mapToDouble(point -> translateY(point.getY())).toArray();
        graphicsContext.fillPolygon(pointsX, pointsY, 4);
        graphicsContext.strokePolygon(pointsX, pointsY, 4);
    }

    /**
     * Draws a given text, starting from the given point
     *
     * @param text text to draw
     * @param beginX x coordinate of the start
     * @param beginY y coordinate of the start
     */
    public void drawText(String text, double beginX, double beginY) {
        graphicsContext.fillText(text, beginX, beginY);
    }

    /** Clears the canvas **/
    public void clear() {
        graphicsContext.clearRect(0, 0, screenWidth, screenHeight);
    }

    /**
     * Fills the background with the given paint
     *
     * @param paint paint to use
     */
    public void fillBackground(Paint paint) {
        graphicsContext.setFill(paint);
        graphicsContext.fillRect(0, 0, screenWidth, screenHeight);
    }
}
