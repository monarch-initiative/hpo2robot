package org.monarchinitiative.controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Custom control for validating the entry of a new HPO label. The control forces the label to be ASCII Only and to
 * not contain stray white space.
 */
public class ValidatingLabelPaneController implements Initializable {
    /** Text label that is shown in red if there is an error in the new label entered by user. */
    @FXML
    private Label errorLabel;
    /** The label of the widget. */
    @FXML
    private Label fieldLabel;
    /** Field for user to enter new label. */
    @FXML
    private TextField textField;


    private BooleanProperty isValidProperty;

    public ValidatingLabelPaneController() {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isValidProperty = new SimpleBooleanProperty(false);
        textField.textProperty().addListener((obs, oldval, newval) -> {
            // The following three lines check for non-ASCII characters
            byte[] bytes = newval.getBytes(StandardCharsets.US_ASCII);
            String decodedLine = new String(bytes);
            boolean nonStandardChar = !newval.equals(decodedLine);
            String diff = "";
            if (nonStandardChar) {
                diff = checkNonASCII(newval, decodedLine);
            }
            if (newval.isEmpty()) {
                setInvalid("Enter new term label");
            } else if (newval.contains("  ")) {
                setInvalid("Label must not contain multiple consecutive spaces");
            } else if (newval.startsWith(" ")) {
                setInvalid("Label must not start with space");
            } else if (newval.endsWith(" ")) {
                setInvalid("Label must not end with space");
            }else if (nonStandardChar) {
                setInvalid("Text contains a non-ASCII character encoding: " + diff + " (Not allowed in labels).");
            } else {
                setValid();
            }
        });
        errorLabel.setTextFill(Color.color(1, 0, 0));
        Font largeFont = Font.font("Arial", FontWeight.BOLD, FontPosture.REGULAR, 18);
        Font smallFont = Font.font("Arial", 12);
        fieldLabel.setFont(largeFont);
        errorLabel.setFont(smallFont);
        setInvalid("Enter new term label");
    }

    private String checkNonASCII(String originalLine, String decodedLine) {
        StringBuffer sb = new StringBuffer(originalLine.length());
        for (int i = 0; i < originalLine.length(); i++) {
            if (originalLine.charAt(i) > 0x7F) {
                return String.format("Offering character: %c", originalLine.charAt(i));
            }
        }
        return "Not found";
    }


    private void setInvalid(String message) {
        isValidProperty.set(false);
        errorLabel.setText(message);
        textField.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
    }

    private void setValid() {
        isValidProperty.set(true);
        errorLabel.setText("");
        textField.setStyle("-fx-text-box-border: green; -fx-focus-color: green ;");
    }

    public Label getErrorLabel() {
        return errorLabel;
    }

    public Label getFieldLabel() {
        return fieldLabel;
    }

    public TextField getTextField() {
        return textField;
    }
}
