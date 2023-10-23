package org.monarchinitiative.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

import org.controlsfx.control.textfield.TextFields;

import org.monarchinitiative.Launcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RobotPaneController extends AnchorPane  {

    @FXML
    private TextField commentTextField;

    @FXML
    private TextField definitionTextField;

    @FXML
    private TextField parentTermTextField;

    @FXML
    private WebView robotWebView;

    @FXML
    private TextField termLabelTextField;

    @FXML
    void addPmidAction(ActionEvent event) {

    }

    @FXML
    void addSynonymAction(ActionEvent event) {

    }

    public RobotPaneController() {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("view/RobotPane.fxml"));
       // fxmlLoader.setRoot(this);
       // fxmlLoader.setController(RobotPaneController.this);

//        try {
//            fxmlLoader.load();
//        } catch (IOException exception) {
//            throw new RuntimeException(exception);
//        }

        initElements();
    }

    private void initElements() {
        // A list of possible suggestions
        List<String> possibleSuggestions = Arrays.asList(
                "C", "C#", "C++", "F#", "GoLang",
                "Dart", "Java", "JavaScript", "Kotlin", "PHP",
                "Python", "R", "Swift", "Visual Basic .NET"
        );
       // TextFields.bindAutoCompletion(this.parentTermTextField, possibleSuggestions);

    }


}
