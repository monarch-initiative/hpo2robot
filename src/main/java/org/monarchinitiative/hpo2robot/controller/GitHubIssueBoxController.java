package org.monarchinitiative.hpo2robot.controller;

import javafx.application.HostServices;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.monarchinitiative.hpo2robot.github.GitHubIssue;
import org.monarchinitiative.hpo2robot.github.GitHubIssueRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class GitHubIssueBoxController implements Initializable  {
    Logger LOGGER = LoggerFactory.getLogger(GitHubIssueBoxController.class);

    @FXML
    VBox githubVBox;
    @FXML
    private Button getIssuesButton;

    @FXML
    private Label gitHubStatusLabel;

    @FXML
    private Button nextIssueButton;

    @FXML
    private Button openInGithubButton;

    private Map<GitHubIssue, Boolean> gitHubIssueMap;

    private Optional<GitHubIssue> currentIssueOpt;

    private Optional<HostServices> hostServicesOpt;



    private final Font LARGE_FONT = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
    private final Font SMALL_FONT = Font.font("Arial", 12);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gitHubIssueMap = new TreeMap<>();
        githubVBox.setSpacing(10);
        githubVBox.setStyle("-fx-padding: 10;" + "-fx-border-style: solid inside;"
                + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: blue;");
        currentIssueOpt = Optional.empty();
        getIssuesButton.setOnAction(this::getGithubIssuesAction);
        nextIssueButton.setOnAction(this::nextGithubIssueAction);
        openInGithubButton.setOnAction(this::openInGithubAction);
    }
    @FXML
    private void openInGithubAction(ActionEvent e) {
        e.consume();
        final String urlPart = "https://github.com/obophenotype/human-phenotype-ontology/issues/";
        if (currentIssueOpt.isPresent()) {
            GitHubIssue issue = currentIssueOpt.get();
            String url = urlPart + issue.getIssueNumber();
            try {
                if (hostServicesOpt.isPresent()) {
                    var services = hostServicesOpt.get();
                    services.showDocument(url);
                } else {
                    gitHubStatusLabel.setTextFill(Color.RED);
                    gitHubStatusLabel.setFont(LARGE_FONT);
                    gitHubStatusLabel.setText("Could not retrieve host services");
                }
            } catch (InternalError ee) {
                ee.printStackTrace();
            }
        }
    }

    public StringProperty getGitHubLabelProperty() {
        return this.gitHubStatusLabel.textProperty();
    }
    @FXML
    public void getGithubIssuesAction(ActionEvent actionEvent) {
        getGitHubIssues();
    }
    @FXML
    private void getGitHubIssues() {
        GitHubIssueRetriever retriever = new GitHubIssueRetriever();
        gitHubIssueMap.clear();
        retriever.getIssues().stream().forEach(i -> gitHubIssueMap.put(i, true));
        String message = String.format("Retrieved %d issues from GitHub", gitHubIssueMap.size());
        gitHubStatusLabel.setTextFill(Color.BLACK);
        gitHubStatusLabel.setFont(SMALL_FONT);
        gitHubStatusLabel.setText(message);
    }


    public void nextGithubIssueAction(ActionEvent actionEvent) {
        Optional<Map.Entry<GitHubIssue, Boolean> > opt =
                gitHubIssueMap.entrySet()
                        .stream()
                        .filter(Map.Entry::getValue)
                        .findFirst();
        if (opt.isEmpty()) {
            PopUps.alertDialog("Warning", "No open GitHub issues, retrieve more");
            gitHubStatusLabel.setTextFill(Color.RED);
            gitHubStatusLabel.setFont(LARGE_FONT);
        } else {
            Map.Entry<GitHubIssue, Boolean> nextPendingIssue = opt.get();
            boolean result = PopUps.nextGitHubIssue(nextPendingIssue.getKey());
            if (result) {
                String message = String.format("HPO Issue #%s", nextPendingIssue.getKey().getIssueNumber());
                gitHubStatusLabel.setTextFill(Color.BLACK);
                gitHubStatusLabel.setFont(SMALL_FONT);
                gitHubStatusLabel.setText(message);
                this.currentIssueOpt = Optional.of(nextPendingIssue.getKey());
            } else {
                gitHubStatusLabel.setTextFill(Color.RED);
                gitHubStatusLabel.setFont(LARGE_FONT);
                gitHubStatusLabel.setText("Could not retrieve next GitHub Issue");
                this.currentIssueOpt = Optional.empty();
            }
        }
    }
    public void setHostServices(Optional<HostServices> hostServicesOpt) {
        this.hostServicesOpt = hostServicesOpt;
    }

    public void clearFields() {
        gitHubStatusLabel.setTextFill(Color.BLACK);
        gitHubStatusLabel.setFont(SMALL_FONT);
        gitHubStatusLabel.setText("");
        this.currentIssueOpt = Optional.empty();
    }



}
