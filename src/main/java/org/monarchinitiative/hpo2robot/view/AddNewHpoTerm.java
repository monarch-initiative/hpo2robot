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
import org.monarchinitiative.hpo2robot.controller.PopUps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This custom control creates a panel with buttons to add ROBOT issues.
 */
public class AddNewHpoTerm extends HBox {
    private final static Logger LOGGER = LoggerFactory.getLogger(AddNewHpoTerm.class);

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
            LOGGER.error("Could not load AddNewHpoTerm Widget: {}", e.getMessage());
            PopUps.showException("Error", "Could not load AddNewHpoTerm Widget",
                    e.getMessage(), e);
        }
    }


    public void setRobotLabel(String text) {
        robotStatusLabelProperty.set(text);
    }


    public void setCreateNewRobotItemAction(EventHandler<ActionEvent> handler) {
        controller.setAction(handler);
    }
    
    public void setExportRobotAction(EventHandler<ActionEvent> handler) {
        controller.setExportRobotAction(handler);
    }

    public void setClearRobotAction(EventHandler<ActionEvent> handler) {
        controller.setClearRobotAction(handler);
    }

    public void setRunRobotAction(EventHandler<ActionEvent> handler) {
        controller.setRunRobotAction(handler);
    }

    public void clearFields() {
        robotStatusLabelProperty.set("");
    }
}
