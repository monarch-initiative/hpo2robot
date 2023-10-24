package org.monarchinitiative.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.Launcher;
import org.monarchinitiative.controller.ValidatingLabelPaneController;

public class ValidatingPane extends AnchorPane {


    private ValidatingLabelPaneController controller;

    StringProperty fieldNameProperty;

    StringProperty textFieldProperty;

    StringProperty errorProperty;

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
            e.printStackTrace();
        }
    }


    public void setFieldLabel(String fieldLabel) {
        fieldNameProperty.set(fieldLabel);
    }
}
