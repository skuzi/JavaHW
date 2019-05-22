package ru.hse.kuzyaka.cannon;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GameOverAlert {
    private Alert alert;
    private Stage stage;
    private boolean wasShown;

    public GameOverAlert(Stage stage) {
        this.stage = stage;
        alert = new Alert(Alert.AlertType.INFORMATION, "YATTA!");
        Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        exitButton.setText("Exit");
        exitButton.setOnTouchPressed(event -> Platform.exit());
        alert.setTitle("Game over");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.setOnHidden(event -> Platform.exit());
    }

    void show() {
        if (wasShown) {
            return;
        }
        alert.show();
        wasShown = true;
    }
}
