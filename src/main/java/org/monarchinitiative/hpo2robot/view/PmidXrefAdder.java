package org.monarchinitiative.hpo2robot.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.PmidXrefAdderController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
            e.printStackTrace();
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

    public void clearFields() {
        controller.clearFields();
    }

    public void setAction(EventHandler<ActionEvent> handler) {
        controller.setAddSynoynmAction(handler);
    }
}
