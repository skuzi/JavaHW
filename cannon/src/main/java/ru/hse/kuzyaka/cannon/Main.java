package ru.hse.kuzyaka.cannon;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/** Class representing a game **/
public class Main extends Application {
    private static final int WIDTH = 1280;
    private static final int HEIGHT = 800;
    private static final int TARGET_RADIUS = 10;
    private static final int WIDTH_IN_METERS = 1000;
    private static final String CHOOSE_PROJECTILE_MESSAGE = "Projectiles: \n 1 -- small \n 2 -- medium \n 3 -- big";
    private static final double TEXT_BEGIN_X = 10;
    private static final double TEXT_BEGIN_Y = 40;


    private ProjectileType projectileType = ProjectileType.SMALL;
    private Landscape landscape;
    private Cannon cannon;
    private List<Projectile> projectiles = new ArrayList<>();
    private List<Bang> bangs = new ArrayList<>();
    private Renderer renderer;
    private Target target;

    private Group root = new Group();
    private HashSet<String> pressedKeys = new HashSet<>();

    /**
     * Launch a game with the given command line arguments
     * @param args command line arguments
     */
    public static void run(String[] args) {
        Application.launch(args);
    }

    /** {@inheritDoc} **/
    @Override
    public void start(Stage stage) {
        stage.setTitle("Scorched Earth");
        stage.setWidth(WIDTH);
        stage.setHeight(HEIGHT);
        stage.setResizable(false);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        Scene mainScene = new Scene(root);
        root.getChildren().addAll(canvas);

        mainScene.setOnKeyPressed(keyEvent -> {
            String code = keyEvent.getCode().toString();
            pressedKeys.add(code);
        });

        mainScene.setOnKeyReleased(keyEvent -> pressedKeys.remove(keyEvent.getCode().toString()));
        stage.setScene(mainScene);


        var graphicsContext = canvas.getGraphicsContext2D();
        Point2D cannonPosition;
        Point2D targetPosition;

        var mapStream = Main.class.getResourceAsStream("/map.txt");
        var in = new Scanner(mapStream);

        cannonPosition = new Point2D(in.nextInt(), in.nextInt());
        targetPosition = new Point2D(in.nextInt(), in.nextInt());

        int numberOfPoints = in.nextInt();
        List<Point2D> points = new ArrayList<>();
        for (int i = 0; i < numberOfPoints; i++) {
            points.add(new Point2D(in.nextInt(), in.nextInt()));
        }
        landscape = new Landscape(points);
        cannon = new Cannon(cannonPosition, landscape, projectileType);
        target = new Target(targetPosition, TARGET_RADIUS);
        renderer = new Renderer(graphicsContext, WIDTH, HEIGHT, WIDTH_IN_METERS);
        GameOverAlert alert = new GameOverAlert(stage);

        new AnimationTimer() {

            @Override
            public void handle(long nowNano) {
                renderer.clear();
                pressKeys();
                cannon.setProjectileType(projectileType);
                cannon.move();
                projectiles.forEach(Projectile::move);

                var iterator = projectiles.iterator();
                while (iterator.hasNext()) {
                    var current = iterator.next();
                    if (!current.isAlive()) {
                        bangs.add(current.makeBang());
                        iterator.remove();
                    }
                }

                bangs.removeIf(bang -> !bang.isAlive());
                renderer.fillBackground(Color.ANTIQUEWHITE);
                projectiles.forEach(projectile -> projectile.draw(renderer));
                bangs.forEach(bang -> bang.draw(renderer));
                landscape.draw(renderer);
                cannon.draw(renderer);
                target.draw(renderer);
                renderer.drawText(CHOOSE_PROJECTILE_MESSAGE, TEXT_BEGIN_X, TEXT_BEGIN_Y);

                for (var bang : bangs) {
                    if (bang.hasHit(targetPosition, TARGET_RADIUS)) {
                        alert.show();
                        break;
                    }
                }
            }
        }.start();
        stage.show();
    }

    private void pressKeys() {
        boolean left = pressedKeys.contains("A");
        boolean right = pressedKeys.contains("D");
        boolean up = pressedKeys.contains("W");
        boolean down = pressedKeys.contains("S");
        boolean digit1 = pressedKeys.contains("DIGIT1");
        boolean digit2 = pressedKeys.contains("DIGIT2");
        boolean digit3 = pressedKeys.contains("DIGIT3");
        boolean enter = pressedKeys.contains("ENTER");

        if (digit1) {
            projectileType = ProjectileType.SMALL;
        }
        if (digit2) {
            projectileType = ProjectileType.MEDIUM;
        }
        if (digit3) {
            projectileType = ProjectileType.BIG;
        }
        if (left && right || !left && !right) {
            cannon.setCannonDirection(Direction.NONE);
        } else if (left) {
            cannon.setCannonDirection(Direction.LEFT);
        } else {
            cannon.setCannonDirection(Direction.RIGHT);
        }

        if (up && down || !up && !down) {
            cannon.setBarrelDirection(Direction.NONE);
        } else if (up) {
            cannon.setBarrelDirection(Direction.RIGHT);
        } else {
            cannon.setBarrelDirection(Direction.LEFT);
        }

        if (enter) {
            projectiles.add(cannon.shoot());
        }
    }
}
