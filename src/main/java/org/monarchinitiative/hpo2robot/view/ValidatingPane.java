package org.monarchinitiative.hpo2robot.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.PmidXrefAdderController;
import org.monarchinitiative.hpo2robot.controller.ValidatingLabelPaneController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatingPane extends AnchorPane {
    Logger LOGGER = LoggerFactory.getLogger(ValidatingPane.class);

    StringProperty fieldNameProperty;

    StringProperty textFieldProperty;

    StringProperty errorProperty;

    private ValidatingLabelPaneController controller;

    public ValidatingPane(){
        super();
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/ValidatingPane.fxml"));
            controller = new ValidatingLabelPaneController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            fieldNameProperty = new SimpleStringProperty();
            fieldNameProperty.bindBidirectional(controller.getFieldLabel().textProperty());
            this.errorProperty = new SimpleStringProperty("");
            errorProperty.bindBidirectional(controller.getErrorLabel().textProperty());
            this.textFieldProperty = new SimpleStringProperty("");
            textFieldProperty.bindBidirectional(controller.getTextField().textProperty());
        } catch (Exception e) {
            LOGGER.error("Error loading ValidatingLabelPaneController: {}", e.getMessage());
        }
    }


    public void setFieldLabel(String fieldLabel) {
        fieldNameProperty.set(fieldLabel);
    }

    public StringProperty getLabel() {
        return this.textFieldProperty;
    }

    public void clearFields() {
        this.textFieldProperty.set("");
    }

    public BooleanProperty getIsValidProperty() {
        return this.controller.getIsValidProperty();
    }
}
