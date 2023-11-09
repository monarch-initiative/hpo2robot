package org.monarchinitiative.hpo2robot.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.AddNewHpoTermController;

public class AddNewHpoTerm extends HBox {

    final StringProperty robotStatusLabelProperty;

    AddNewHpoTermController controller;
    public AddNewHpoTerm() {
        super();
        robotStatusLabelProperty = new SimpleStringProperty("");
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/AddNewHpoTerm.fxml"));
            controller = new AddNewHpoTermController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            robotStatusLabelProperty.bindBidirectional(controller.robotLabelProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setRobotLabel(String text) {
        robotStatusLabelProperty.set(text);
    }


    public void setAction(EventHandler<ActionEvent> handler) {
        controller.setAction(handler);
    }
    
    public void setExportRobotAction(EventHandler<ActionEvent> handler) {
        controller.setExportRobotAction(handler);
    }

    public void setClearRobotAction(EventHandler<ActionEvent> handler) {
        controller.setClearRobotAction(handler);
    }


    public void clearFields() {
        robotStatusLabelProperty.set("");
    }
}
