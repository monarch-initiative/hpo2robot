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

    final StringProperty buttonNameProperty;

    final StringProperty definitionTextProperty;

    final StringProperty commentTextProperty;

    final StringProperty definitionErrorProperty;

    final StringProperty commentErrorProperty;


    final BooleanProperty isValid;

    ValidatingTextEntryPaneController controller;


    public ValidatingTextEntryPane() {
        super();
        this.definitionTextProperty = new SimpleStringProperty("");
        this.commentTextProperty = new SimpleStringProperty("");
        this.definitionErrorProperty = new SimpleStringProperty("");
        this.commentErrorProperty = new SimpleStringProperty("");
        this.isValid = new SimpleBooleanProperty(false);
        this.buttonNameProperty = new SimpleStringProperty("Get text");

        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/ValidatingTextEntryPane.fxml"));
            this.controller = new ValidatingTextEntryPaneController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            definitionErrorProperty.bindBidirectional(controller.getDefinitionErrorLabel().textProperty());
            commentErrorProperty.bindBidirectional(controller.getCommentErrorLabel().textProperty());
            definitionTextProperty.bindBidirectional(controller.definitionStringProperty());
            commentTextProperty.bindBidirectional(controller.commentStringProperty());
            isValid.bindBidirectional(controller.isValidDefinitionPropertyProperty());
            this.buttonNameProperty.bindBidirectional(controller.getDefinitionValidateButton().textProperty());
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
        this.commentTextProperty.set("");
        this.definitionErrorProperty.set("");
        this.commentErrorProperty.set("");
    }

    public String getDefinition() {
        return controller.definitionStringProperty().get();
    }

    public String getComment() {
        return controller.commentStringProperty().get();
    }
}
