package org.monarchinitiative.controller;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ParentTermAdderController implements Initializable {

    @FXML
    private Label parentTermLabel;

    @FXML
    private TextField textField;

    @FXML
    private Button addButton;

    private ObservableList<String> parentTerms;

    public ParentTermAdderController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        parentTerms = FXCollections.observableList(new ArrayList<>());
        parentTerms.addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("Detected a change! ");
            }
        });
    }


    public void addParentTerm(ActionEvent actionEvent) {
        parentTerms.add(textField.getText());
    }

    public StringProperty parentTermProperty() {
        return textField.textProperty();
    }

    public ObservableList<String> getParentList() {
        return this.parentTerms;
    }




}
