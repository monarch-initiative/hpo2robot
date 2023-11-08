package org.monarchinitiative.hpo2robot.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.ValidatingTextEntryPaneController;

public class ValidatingTextEntryPane extends AnchorPane {

    StringProperty buttonNameProperty;

    StringProperty definitionTextProperty;

    StringProperty commentTextProperty;

    StringProperty definitionErrorProperty;

    StringProperty commentErrorProperty;


    BooleanProperty isValid;

    ValidatingTextEntryPaneController controller;


    public ValidatingTextEntryPane() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/ValidatingTextEntryPane.fxml"));
            this.controller = new ValidatingTextEntryPaneController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            this.definitionErrorProperty = new SimpleStringProperty("");
            commentErrorProperty = new SimpleStringProperty("");
            definitionErrorProperty.bindBidirectional(controller.getDefinitionErrorLabel().textProperty());
            commentTextProperty.bindBidirectional(controller.getCommentErrorLabel().textProperty());
            this.definitionTextProperty = new SimpleStringProperty("");
            this.commentTextProperty = new SimpleStringProperty("");
            definitionTextProperty.bindBidirectional(controller.definitionStringProperty());
            commentTextProperty.bindBidirectional(controller.commentStringProperty());
            isValid = new SimpleBooleanProperty(false);
            isValid.bindBidirectional(controller.isValidPropertyProperty());
            this.buttonNameProperty = new SimpleStringProperty("Get text");
            this.buttonNameProperty.bindBidirectional(controller.getValidatingButton().textProperty());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BooleanProperty isValidProperty() {
        return isValid;
    }

    public void initializeButtonText(String label) {
        buttonNameProperty.set(label);
    }




    public void clearFields() {
        this.definitionTextProperty.set("");
    }

    public String getDefinition() {
        return controller.definitionStringProperty().get();
    }

    public String getComment() {
        return controller.commentStringProperty().get();
    }
}
