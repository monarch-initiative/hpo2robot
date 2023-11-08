package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This controller adds a new ROBOT Item (representing a new HPO term)
 */
public class AddNewHpoTermController implements Initializable {

    @FXML
    private VBox newRobotItemBox;

    @FXML
    private Button newRobotRowButton;

    @FXML
    private Button clearRobotButton;

    @FXML
    private Button exportRobotButton;


    @FXML
    private Label robotStatusLabel;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        newRobotItemBox.setSpacing(10);
        newRobotItemBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 1;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        newRobotRowButton.setStyle("-fx-spacing: 10;");
        Font small = Font.font("Arial", FontWeight.NORMAL, FontPosture.REGULAR, 10);
        robotStatusLabel.setFont(small);
    }

    /**
     * This method takes a reference to a event handler that is defined in the Main controller and
     * that adds the contents of the GUI to a new ROBOT item.
     * @param handler function passed in from {@link MainWindowController}
     */
    public void setAction(EventHandler<ActionEvent> handler) {
        newRobotRowButton.setOnAction(handler);
    }


    public void setExportRobotAction(EventHandler<ActionEvent> handler) {
        exportRobotButton.setOnAction(handler);
    }

    public void setClearRobotAction(EventHandler<ActionEvent> handler) {
        clearRobotButton.setOnAction(handler);
    }


    public StringProperty robotLabelProperty() {
        return robotStatusLabel.textProperty();
    }
}
