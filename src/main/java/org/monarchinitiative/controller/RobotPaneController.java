package org.monarchinitiative.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.Launcher;

import java.io.IOException;

public class RobotPaneController extends AnchorPane  {

    public RobotPaneController() {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("view/RobotPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(RobotPaneController.this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        initElements();
    }

    private void initElements() {

    }


}
