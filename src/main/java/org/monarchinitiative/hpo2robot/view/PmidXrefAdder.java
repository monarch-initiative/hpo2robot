package org.monarchinitiative.hpo2robot.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.PmidXrefAdderController;
import org.monarchinitiative.hpo2robot.model.Synonym;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * This control let's us add PMID and XREFs.
 */
public class PmidXrefAdder extends AnchorPane {
    private final Logger LOGGER = LoggerFactory.getLogger(PmidXrefAdder.class);

    PmidXrefAdderController controller = null;


    public PmidXrefAdder() {
        super();

        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/PmidXrefAdder.fxml"));
            this.controller = new PmidXrefAdderController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
        } catch (Exception e) {
            LOGGER.error("Error loading PmidXrefAdderController: {}", e.getMessage());

        }
    }



    public List<String> getPmidList() {
        if (this.controller == null ) {
            LOGGER.error("Attempt to get PMID list but Controller is null");
            return List.of();
        } else {
            return controller.getPmidList();
        }
    }

    public List<Synonym> getSynonymList() {
        if (this.controller == null ) {
            LOGGER.error("Attempt to get PMID list but Controller is null");
            return List.of();
        } else {
            return controller.getSynonymList();
        }
    }

    public Optional<String> getCustomOrcidOpt() {
        return controller.customOrcidOpt();
    }

    public void clearFields() {
        controller.clearFields();
    }

    public void setViewFactory(ViewFactory viewFactory) {
        this.controller.setViewFactory(viewFactory);
    }
}
