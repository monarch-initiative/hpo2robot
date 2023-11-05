package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.*;

/**
 * Controller for a widget that allows the user to select parent terms by autocomplete or
 * by connecting to the OntologyTree widget.
 */
public class ParentTermAdderController implements Initializable {

    @FXML
    private Label parentTermLabel;

    @FXML
    private TextField textField;

    @FXML
    private Button addButton;

    @FXML
    private Label parentTermErrorLabel;


    private final Set<String> parentTermLabels;

    public ParentTermAdderController() {
        parentTermLabels = new HashSet<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnAction(e ->{
            String parentTermText = textField.getText();
            parentTermLabels.add(parentTermText);
            textField.clear();
            parentTermErrorLabel.setText(getErrorLabel());
        });
        addButton.setStyle("-fx-spacing: 10;");
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        parentTermLabel.setFont(largeFont);
    }


    private String getErrorLabel() {
        if (parentTermLabels.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            List<String> shortLabels = new ArrayList<>();
            for (String ptl : parentTermLabels) {
                shortLabels.add(ptl.length() < 30 ? ptl : String.format("%s...", ptl.substring(0,27)));
            }
            return String.join("; ", shortLabels);
        }
    }

    public StringProperty parentTermProperty() {
        return textField.textProperty();
    }

    /**
     *
     * @return a set of Labels representing the parent or parents of the current term.
     */
    public Set<String> getParentSet() {
        return parentTermLabels;
    }

    public TextField getTextField() {
        return textField;
    }


}
