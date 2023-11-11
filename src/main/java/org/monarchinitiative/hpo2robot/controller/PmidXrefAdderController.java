package org.monarchinitiative.hpo2robot.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import org.monarchinitiative.hpo2robot.controller.widgets.UserStringFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PmidXrefAdderController implements Initializable  {
    final Logger LOGGER = LoggerFactory.getLogger(PmidXrefAdderController.class);

    @FXML
    private Button pmidButton;

    @FXML
    private Label pmidLabel;

    @FXML
    private Button xrefButton;
    @FXML
    private Label xrefLabel;
    @FXML
    private Button synonymAdderButton;
    @FXML
    private Label synonymAdderLabel;

    @FXML
    private Button orcidButton;

    private List<String> pmidList;

    private String customOrcid = null;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pmidList = new ArrayList<>();
        pmidButton.setOnAction(e -> {
            Optional<String> opt = PopUps.getPMID();
            if (opt.isPresent()) {
                pmidList.add(opt.get());
                LOGGER.trace("Adding {}", opt.get());
                pmidLabel.setText(String.format("(%d)", pmidList.size()));
            } else {
                LOGGER.warn("Could not retrieve PMID");
            }
        });

        xrefButton.setOnAction(e -> {
            // TODO what XREFs do we want to allow and what prefixes?
            LOGGER.error("Set XREF");
        });

        orcidButton.setOnAction(actionEvent -> {
            Optional<String> opt = UserStringFetcher.fetchORCID();
            if (opt.isPresent()) {
                System.out.println("GOT ORCID " + opt.get());
                customOrcid = opt.get();
            } else {
                LOGGER.error("Could not retrieve custom ORCID");
            }

        });
    }
    private static final String NOT_INITIALIZED = "not initialized";

    public static String getStringFromUser(String windowTitle, String promptText, String labelText) {
        TextInputDialog dialog = new TextInputDialog(promptText);
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(NOT_INITIALIZED);

    }

    public List<String> getPmidList() {
        return pmidList;
    }


    public Optional<String> customOrcidOpt() {
        return Optional.ofNullable(customOrcid);
    }

    public void clearFields() {
        pmidList = new ArrayList<>();
        pmidLabel.setText("(0)");
    }

    /**
     * This is used to set an ActioHandler in {@link MainWindowController}
     * @param handler Action handler that opens a dialog to add a synonym
     */
    public void setAddSynonymAction(EventHandler<ActionEvent> handler) {
        synonymAdderButton.setOnAction(handler);
    }
}
