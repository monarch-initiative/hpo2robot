package org.monarchinitiative.controller;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ValidatingTextEntryPaneController implements Initializable {
    @FXML
    private Label errorLabel;

    @FXML
    private Button button;

    @FXML
    private Label textSummary;

    private BooleanProperty isValidProperty;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isValidProperty = new SimpleBooleanProperty(false);
        button.setOnAction(e -> {
            TextArea jfxTextarea = new TextArea();
            VBox jfxbox = new VBox(jfxTextarea);
            Scene jfxscene = new Scene(jfxbox, 320, 160);
            Stage jfxStage = new Stage();
            jfxStage.setScene(jfxscene);
            jfxStage.showAndWait();
        });
    }
}
