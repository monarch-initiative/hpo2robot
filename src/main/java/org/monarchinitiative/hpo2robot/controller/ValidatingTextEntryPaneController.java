package org.monarchinitiative.hpo2robot.controller;


import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Provide user entry text area for the definition of comment together with some quality control.
 * Note that this widget expects the controller to set the initial name of the button to ewither
 * "Create definition" or "Create comment"; once the user has entered some text, the buttons names
 * will change to "Edit definition" or "Edit comment". This is currently a bit of a hack and could
 * be generalized.
 * @author Peter Robinson
 */
public class ValidatingTextEntryPaneController implements Initializable {
    @FXML
    private Label definitionErrorLabel;

    @FXML
    private Button validatingButtonDefinition;

    @FXML
    private Label textSummaryDefinition;

    @FXML
    private Label textSummaryComment;

    @FXML
    private Button validatingButtonComment;

    @FXML
    private Label commentErrorLabel;
    
    private StringProperty definitionStringProperty;

    private StringProperty commentStringProperty;


    private BooleanProperty isValidDefinitionProperty;

    private BooleanProperty isValidCommentProperty;

    public static final String CREATE_DEFINITION = "Create definition";
    public static final String EDIT_DEFINITION = "Edit definition";
    public static final String CREATE_COMMENT = "Create comment";
    public static final String EDIT_COMMENT = "Edit comment";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        isValidDefinitionProperty = new SimpleBooleanProperty(false);
        isValidCommentProperty = new SimpleBooleanProperty(false);
        definitionStringProperty = new SimpleStringProperty("");
        commentStringProperty = new SimpleStringProperty("");
        validatingButtonDefinition.setOnAction(e -> {
            String text = getUserStringFromTextArea("Enter text", "Enter the text of the definition (comment) here. " +
                    "Stray white space and newlines will be remove automatically.",
                    this.definitionStringProperty.get());
            definitionStringProperty.set(text);
            int N = Math.min(50, text.length());
            textSummaryDefinition.setText(text.substring(0,N) + (text.length() > N ? "..." : ""));
            validateDefinitionText(text);
            validatingButtonDefinition.setText(EDIT_DEFINITION);
        });
        validatingButtonDefinition.setText(CREATE_DEFINITION);
        // red text for error messages
        definitionErrorLabel.setTextFill(Color.color(1, 0, 0));
        validatingButtonComment.setText(CREATE_COMMENT);
        validatingButtonComment.setOnAction(e -> {
            String text = getUserStringFromTextArea("Enter text", "Enter the text of the definition (comment) here. " +
                    "Stray white space and newlines will be remove automatically.",
                    this.commentStringProperty.get());
            commentStringProperty.set(text);
            int N = Math.min(50, text.length());
            textSummaryComment.setText(text.substring(0,N) + (text.length() > N ? "..." : ""));
            validateCommentText(text);
            validatingButtonComment.setText(EDIT_COMMENT);
        });
        validatingButtonComment.setText(CREATE_COMMENT);
    }


    private void validateDefinitionText(String text) {
       validate(text,validatingButtonDefinition, textSummaryDefinition,definitionErrorLabel, isValidDefinitionProperty);
    }

    private void validateCommentText(String text) {
        validate(text,validatingButtonComment,textSummaryComment, commentErrorLabel, isValidCommentProperty);
    }

    private void validate(String text, Button button, Label label, Label errorLabel, BooleanProperty bprop) {
        byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
        String decodedLine = new String(bytes);
        boolean nonStandardChar = !text.equals(decodedLine);
        if (text.isEmpty()) {
            setInvalid("No text entered.",
                    button, label, errorLabel, bprop);
        } else if (text.contains("  ")) {
            setInvalid("Text must not contain multiple consecutive spaces.",
                    button, label, errorLabel, bprop);
        } else if (text.startsWith(" ")) {
            setInvalid("Text must not start with space.",
                    button, label,errorLabel, bprop);
        } else if (! text.endsWith(".")) {
            setInvalid("Text must end end with period.",
                    button, label, errorLabel,bprop);
        } else if (nonStandardChar) {
            setInvalid("Text contains a non-standard character encoding. Please remove it.",
                    button, label, errorLabel, bprop);
        } else {
            setValid(button,text, label, errorLabel, bprop);
        }
    }

    /**
     * Gets a string from the user that the user can enter into a TextArea.
     * Automatically removes new lines and extra whitespace and trims trailing/leading white
     * space. If there is a problem, return an empty string.
     * @param title Title of the window
     * @param header - explanatory text
     * @return user-entered string
     */
    private String getUserStringFromTextArea(String title, String header, String initialText) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(20, 150, 10, 10));

        final TextArea userTextField = new TextArea();
        userTextField.setPrefRowCount(10);
        userTextField.setPrefColumnCount(100);
        userTextField.setWrapText(true);
        userTextField.setPrefWidth(150);
        userTextField.textProperty().addListener( // ChangeListener
                (observable, oldValue, newValue) -> {
                    String txt = newValue.replaceAll("\\n", " ");
                    txt = txt.replaceAll("  ", " ");
                    userTextField.setText(txt);
                });
        userTextField.setText(initialText);
        vbox.getChildren().add(userTextField);
        dialog.getDialogPane().setContent(vbox);
        Platform.runLater(userTextField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return userTextField.getText();
            } else {
                return "";
            }
        });
        Optional<String> opt = dialog.showAndWait();
        return opt.orElse("");
    }




    private void setInvalid(String message, Button button,  Label label, Label errorLabel, BooleanProperty bprop) {
        bprop.set(false);
        label.setText("");
        button.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
        errorLabel.setStyle("-fx-text-box-border: red; -fx-focus-color: red ;");
        errorLabel.setText(message);
    }

    private void setValid(Button button, String text, Label label, Label errorLabel, BooleanProperty bprop) {
        bprop.set(true);
        button.setText("");
        button.setStyle("-fx-text-box-border: green; -fx-focus-color: green ;");
        label.setStyle("-fx-text-box-border: green; -fx-focus-color: green ;");
        String shortText = text.length() < 47 ? text : text.substring(0,47) + "...";
        label.setText(shortText);
        errorLabel.setText("");
    }


    public Label getDefinitionErrorLabel() {
        return definitionErrorLabel;
    }

    public Label getCommentErrorLabel() { return commentErrorLabel;}

    public Button getDefinitionValidateButton() {
        return validatingButtonDefinition;
    }


    public StringProperty definitionStringProperty() {
        return definitionStringProperty;
    }

    public StringProperty commentStringProperty() {
        return commentStringProperty;
    }

    public boolean getIsValidDefinitionProperty() {
        return isValidDefinitionProperty.get();
    }

    public BooleanProperty isValidDefinitionPropertyProperty() {
        return isValidDefinitionProperty;
    }



}
