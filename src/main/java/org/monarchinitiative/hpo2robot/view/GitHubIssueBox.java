package org.monarchinitiative.hpo2robot.view;

import javafx.application.HostServices;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.GitHubIssueBoxController;

import java.util.Optional;

public class GitHubIssueBox extends HBox  {

    private GitHubIssueBoxController controller;

    private final StringProperty gitHubLabelProperty;


    public GitHubIssueBox() {
        super();
        gitHubLabelProperty = new SimpleStringProperty("");
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/GitHubIssueBox.fxml"));
            controller = new GitHubIssueBoxController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            gitHubLabelProperty.bindBidirectional(controller.getGitHubLabelProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setGitHubLabelProperty(String text) {
        this.gitHubLabelProperty.set(text);
    }

    public void setHostServices(Optional<HostServices> hostServicesOpt) {
        this.controller.setHostServices(hostServicesOpt);
    }

    public void clearFields() {
        this.controller.clearFields();
    }

    public Optional<String> getGitHubIssueNumber() {
        return controller.getGitHubIssueNumber();
    }
}
