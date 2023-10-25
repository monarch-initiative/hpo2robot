package org.monarchinitiative.view;

import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.Launcher;
import org.monarchinitiative.controller.ValidatingTextEntryPaneController;

public class ValidatingTextEntryPane extends AnchorPane {

    private ValidatingTextEntryPaneController controller;

    StringProperty buttonNameProperty;

    StringProperty textProperty;

    StringProperty errorProperty;

    BooleanProperty isValid;




    public ValidatingTextEntryPane() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/ValidatingTextEntryPane.fxml"));
            controller = new ValidatingTextEntryPaneController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            this.errorProperty = new SimpleStringProperty("");
            errorProperty.bindBidirectional(controller.getErrorLabel().textProperty());
            this.textProperty = new SimpleStringProperty("");
            textProperty.bindBidirectional(controller.userTextProperty());
            isValid = new SimpleBooleanProperty(false);
            isValid.bindBidirectional(controller.isValidPropertyProperty());
            this.buttonNameProperty = new SimpleStringProperty("Get text");
            this.buttonNameProperty.bindBidirectional(controller.getValidatingButton().textProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getUserText() {
        return textProperty.get();
    }


    public BooleanProperty isValidProperty() {
        return isValid;
    }

    public void initializeButtonText(String label) {
        buttonNameProperty.set(label);
    }
}