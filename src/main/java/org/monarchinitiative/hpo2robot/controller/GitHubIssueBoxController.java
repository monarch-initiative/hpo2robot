package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class GitHubIssueBoxController implements Initializable  {

    @FXML
    private Button getIssues;

    @FXML
    private Label gitHubStatusLabel;

    @FXML
    private Button nextIssue;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        getIssues.setOnAction(e -> {
//            GitHubIssueRetriever retriever = new GitHubIssueRetriever();
//            retriever.getIssues();
            System.out.println("TODO implement get issues connect");
        });

        nextIssue.setOnAction(e -> {
            System.out.println("TODO implement");
        });

        /*
        addButton.setOnAction(e ->{
            String parentTermText = textField.getText();
            parentTermLabels.add(parentTermText);
            textField.clear();
            parentTermErrorLabel.setText(getErrorLabel());
        });
        addButton.setStyle("-fx-spacing: 10;");
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        parentTermLabel.setFont(largeFont);
         */
    }

    public StringProperty getGitHubLabelProperty() {
        return this.gitHubStatusLabel.textProperty();
    }


}
