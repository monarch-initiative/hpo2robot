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
import org.monarchinitiative.hpo2robot.controller.widgets.Platform;
import org.monarchinitiative.hpo2robot.github.GitHubIssue;
import org.monarchinitiative.hpo2robot.github.GitHubIssueRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

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
    private Button skipIssueButton;

    @FXML
    private Button openInGithubButton;

    private Map<GitHubIssue, Boolean> gitHubIssueMap;
    @FXML
    private Button clearGitHubIssue;

    private Optional<GitHubIssue> currentIssueOpt;

    private Optional<HostServices> hostServicesOpt;

    private Set<String> skippedIssueSet;



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
        skipIssueButton.setOnAction(this::skipIssue);
        clearGitHubIssue.setOnAction(e -> {
            this.currentIssueOpt = Optional.empty();
            this.gitHubStatusLabel.setTextFill(Color.BLACK);
            this.gitHubStatusLabel.setFont(SMALL_FONT);
            this.gitHubStatusLabel.setText("");
        });
        File skippedIssueFile = Platform.getSkippedIssueFile();
        this.skippedIssueSet = readSkippedIssues(skippedIssueFile);
    }

    private Set<String> readSkippedIssues(File skippedIssueFile) {
        Set<String> issues = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(skippedIssueFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("!")) continue;
                issues.add(line.strip());
            }
        }catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return issues;
    }

    private void writeSkippedIssues() {
        File skippedIssueFile = Platform.getSkippedIssueFile();
        int wrote = 0;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(skippedIssueFile))) {
            List<String> sorted = new ArrayList<>(this.skippedIssueSet);
            sorted.sort(String::compareTo);
            for (String item: sorted) {
                bw.write(item + "\n");
                wrote++;
            }
        }catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Wrote {} issues to skipped list as {}", wrote, skippedIssueFile.getAbsolutePath());
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
                LOGGER.error("Error opening system browser to show GitHub issue: {}", ee.getMessage());
            }
        }
    }


    @FXML
    public void skipIssue(ActionEvent e) {
        e.consume();
        Optional<String> opt = getGitHubIssueNumber();
        if (opt.isEmpty()) {
            LOGGER.error("Could not get GitHub Issue Number");
            return;
        }
        String issueNumber = opt.get();
        skippedIssueSet.add(issueNumber);
        LOGGER.info("Adding issue {} to skipped list", issueNumber);
        writeSkippedIssues();
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
        retriever.getIssues().forEach(i -> gitHubIssueMap.put(i, true));
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
                        .filter(Predicate.not(skippedIssueSet::contains))
                        .findFirst();
        if (opt.isEmpty()) {
            PopUps.alertDialog("Warning", "No open GitHub issues, retrieve more");
            gitHubStatusLabel.setTextFill(Color.RED);
            gitHubStatusLabel.setFont(LARGE_FONT);
        } else {
            Map.Entry<GitHubIssue, Boolean> nextPendingIssueEntry = opt.get();
            GitHubIssue nextPendingIssue = nextPendingIssueEntry.getKey();
            boolean result = PopUps.nextGitHubIssue(nextPendingIssue);
            if (result) {
                String message = String.format("HPO Issue #%s", nextPendingIssue.getIssueNumber());
                gitHubStatusLabel.setTextFill(Color.BLACK);
                gitHubStatusLabel.setFont(SMALL_FONT);
                gitHubStatusLabel.setText(message);
                this.currentIssueOpt = Optional.of(nextPendingIssue);
            } else {
                gitHubStatusLabel.setTextFill(Color.RED);
                gitHubStatusLabel.setFont(LARGE_FONT);
                gitHubStatusLabel.setText("Could not retrieve next GitHub Issue");
                this.currentIssueOpt = Optional.empty();
            }
            gitHubIssueMap.put(nextPendingIssue, false);
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


    public Optional<String> getGitHubIssueNumber() {
        return currentIssueOpt.map(GitHubIssue::getIssueNumber);
    }
}
