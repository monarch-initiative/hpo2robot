package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import org.monarchinitiative.hpo2robot.controller.widgets.UserStringFetcher;
import org.monarchinitiative.hpo2robot.model.Synonym;
import org.monarchinitiative.hpo2robot.view.ViewFactory;
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
    private Label orcidLabel;

    @FXML
    private Button orcidButton;

    private List<String> pmidList;

    private List<Synonym> synonymList;

    private StringProperty customOrcidProperty;

    private ViewFactory viewFactory = null;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pmidList = new ArrayList<>();
        synonymList = new ArrayList<>();
        pmidButton.setOnAction(e -> {
            Optional<String> opt = UserStringFetcher.fetchPubmed();
            if (opt.isPresent()) {
                pmidList.add(opt.get());
                LOGGER.trace("Adding {}", opt.get());
                pmidLabel.setText(String.format("(%d)", pmidList.size()));
            } else {
                LOGGER.warn("Could not retrieve PMID");
            }
        });

        xrefButton.setOnAction(e -> {
            PopUps.alertDialog("ERROR", "XREFs not implemented-todo");
            LOGGER.error("Set XREF not implemented yet");
        });

        orcidButton.setOnAction(actionEvent -> {
            Optional<String> opt = UserStringFetcher.fetchORCID();
            if (opt.isPresent()) {
                customOrcidProperty.set(opt.get());
            } else {
                customOrcidProperty.set("");
                LOGGER.error("Could not retrieve custom ORCID");
            }
        });

        synonymAdderButton.setOnAction(e -> {
            if (viewFactory != null) {
                Optional<Synonym> opt = this.viewFactory.showAddSynonymWindow();
                if (opt.isPresent()) {
                    LOGGER.trace("Adding synonym: {}",opt.get());
                    synonymList.add(opt.get());
                }
            }
            String synonymText = synonymList.isEmpty() ? "" : String.format("(%d)", synonymList.size());
            this.synonymAdderLabel.setText(synonymText);
        });
        customOrcidProperty = new SimpleStringProperty("");
        orcidLabel.textProperty().bind(customOrcidProperty);
        orcidLabel.setFont(new Font("Arial", 8));
    }
    private static final String NOT_INITIALIZED = "not initialized";


    public List<String> getPmidList() {
        return pmidList;
    }

    public List<Synonym> getSynonymList() { return synonymList; }


    public Optional<String> customOrcidOpt() {
        if (customOrcidProperty.get().isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(customOrcidProperty.get());
        }
    }

    public void clearFields() {
        pmidList.clear();
        pmidLabel.setText("(0)");
        synonymList.clear();
        synonymAdderLabel.setText("");
        customOrcidProperty.set("");
    }

    /**
     * This is used to set an ActioHandler in {@link MainWindowController}
     * @param handler Action handler that opens a dialog to add a synonym
     */
    public void setAddSynonymAction(EventHandler<ActionEvent> handler) {

        synonymAdderButton.setOnAction(handler);
    }

    public void setViewFactory(ViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }
}
