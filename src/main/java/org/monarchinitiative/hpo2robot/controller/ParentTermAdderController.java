package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Set;
import java.util.HashSet;
import java.util.ResourceBundle;

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


    private final Set<String> parentTermLabels;

    public ParentTermAdderController() {
        parentTermLabels = new HashSet<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addButton.setOnAction(e ->{
            String parentTermText = textField.getText();
            parentTermLabels.add(parentTermText);
        });
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
