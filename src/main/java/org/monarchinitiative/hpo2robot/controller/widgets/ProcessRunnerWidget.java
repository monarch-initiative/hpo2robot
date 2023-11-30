package org.monarchinitiative.hpo2robot.controller.widgets;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProcessRunnerWidget {

    private Timeline timeline;

    private final Service<String> service;

    private final String commandString;


    public ProcessRunnerWidget(Service<String> runnerService, String myCommand) {
        service = runnerService;
        commandString = myCommand;
    }

    public void showAndWait() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Running Process" );
        alert.setHeaderText("Running process ");
        alert.setContentText(commandString);
        Label timerLabel = new Label("waiting");

        TextArea textArea = new TextArea("...");
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(timerLabel, 0, 0);
        expContent.add(textArea, 0, 1);

        final Button btnOK = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        btnOK.disableProperty().set(true);
        service.setOnSucceeded(event -> {
            String text = service.getMessage();
            if (text.isEmpty()) {
                text = "finished successfully";
            }
            textArea.setText(text);
            btnOK.disableProperty().set(false);
        });
        service.setOnRunning(e -> {
            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1),
                            (EventHandler) event -> {
                                int timeSeconds = 0;
                                timeSeconds++;
                                timerLabel.setText(String.format("%s s", timeSeconds));
                            }));
            timeline.playFromStart();
        });
        service.start();
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

}
