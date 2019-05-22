package ru.hse.kuzyaka.cannon;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Renderer {
    private GraphicsContext graphicsContext;
    private double screenWidth;
    private double screenHeight;
    private double screenWidthInMeters;
    private double screenHeightInMeters;
    private double scale;

    public Renderer(GraphicsContext graphicsContext, int width, int height, int widthInMeters) {
        screenWidth = width;
        screenHeight = height;
        screenWidthInMeters = widthInMeters;
        scale = 1.0 * screenWidth / screenWidthInMeters;
        screenHeightInMeters = screenHeight / scale;
        this.graphicsContext = graphicsContext;
    }

    private double transform(double value) {
        return value * scale;
    }

    private double transformX(double x) {
        return x * scale;
    }

    private double transformY(double y) {
        return (screenHeightInMeters - y) * scale;
    }

    public void drawLine(Line line) {
        graphicsContext.setStroke(Color.BLACK);
        graphicsContext.strokeLine(transformX(line.getBegin().getX()), transformY(line.getBegin().getY()),
                transformX(line.getEnd().getX()), transformY(line.getEnd().getY()));
    }

    public void drawCircle(Point2D center, double radius) {
        double r = transform(radius);
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillOval(transformX(center.getX()) - r,
                transformY(center.getY()) - r, 2 * r, 2 * r);
    }

    public void clear() {
        graphicsContext.clearRect(0, 0, screenWidth, screenHeight);
    }

    public void fillBackground(Paint paint) {
        graphicsContext.setFill(paint);
        graphicsContext.fillRect(0, 0, screenWidth, screenHeight);
    }
}
