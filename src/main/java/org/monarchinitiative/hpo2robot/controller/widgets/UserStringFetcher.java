package org.monarchinitiative.hpo2robot.controller.widgets;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provide simple dialogs to fetch data from the user
 */
public class UserStringFetcher {


    private static final String ORCID_REGEX =
            "^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$";

    /** Regular expression to check whether an input string is a valid ORCID id. */
    private static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_REGEX);


    private static final String PUBMED_REGEX = "^PMID:\\d+";

    private static final Pattern PUBMED_PATTERN = Pattern.compile(PUBMED_REGEX);


    public static Optional<String> fetchORCID() {
        return fetch("Enter ORCID Identifier", "ORCID", ORCID_PATTERN);
    }

    public static Optional<String> fetchPubmed() {
        return fetch("Enter PMID", "PMID", PUBMED_PATTERN);
    }


    public static Optional<String> fetch(String windowTitle, String labelText, Pattern regex) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        BooleanProperty isValidProperty = new SimpleBooleanProperty();
        dialog.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            Matcher matcher = regex.matcher(newValue);
            isValidProperty.set(matcher.matches());
            dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(! isValidProperty.get());
        });
        return dialog.showAndWait();
    }


}
