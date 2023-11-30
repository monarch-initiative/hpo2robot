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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidatingTextEntryPane extends AnchorPane {
    Logger LOGGER = LoggerFactory.getLogger(ValidatingTextEntryPane.class);
    final StringProperty buttonNameProperty;

    final StringProperty definitionTextProperty;

    final StringProperty commentTextProperty;

    final StringProperty definitionErrorProperty;

    final StringProperty commentErrorProperty;

    /**
     * If true, then the definition (contents of this pane is valid so we are ready o contribute to a new ROBOT item.
     * Note that the comment is optional and does not affect this property.
     */
    final BooleanProperty isReady;

    ValidatingTextEntryPaneController controller;


    public ValidatingTextEntryPane() {
        super();
        this.definitionTextProperty = new SimpleStringProperty("");
        this.commentTextProperty = new SimpleStringProperty("");
        this.definitionErrorProperty = new SimpleStringProperty("");
        this.commentErrorProperty = new SimpleStringProperty("");
        this.isReady = new SimpleBooleanProperty(false);
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
            isReady.bindBidirectional(controller.isValidDefinitionPropertyProperty());
            this.buttonNameProperty.bindBidirectional(controller.getDefinitionValidateButton().textProperty());
        } catch (Exception e) {
            LOGGER.error("Error loading ValidatingTextEntryPaneController: {}", e.getMessage());
        }
    }

    public BooleanProperty isReadyProperty() {
        return isReady;
    }

    public void initializeButtonText(String label) {
        buttonNameProperty.set(label);
    }




    public void clearFields() {
        this.controller.clearFields();
    }

    public String getDefinition() {
        return controller.definitionStringProperty().get();
    }

    public String getComment() {
        return controller.commentStringProperty().get();
    }
}
