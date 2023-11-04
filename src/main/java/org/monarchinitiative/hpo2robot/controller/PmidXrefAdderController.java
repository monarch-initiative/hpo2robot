package org.monarchinitiative.hpo2robot.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PmidXrefAdderController implements Initializable  {
    Logger LOGGER = LoggerFactory.getLogger(PmidXrefAdderController.class);

    @FXML
    private Button pmidButton;

    @FXML
    private Label pmidLabel;

    @FXML
    private Button xrefButton;

    @FXML
    private Label xrefLabel;

    private List<String> pmidList;


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
                LOGGER.warn("Could not retried PMID");
            }
        });

        xrefButton.setOnAction(e -> {
            // TODO what XREFs do we want to allow and what prefixes?
            LOGGER.error("Set XREF");
        });

    }


    public List<String> getPmidList() {
        return pmidList;
    }
}