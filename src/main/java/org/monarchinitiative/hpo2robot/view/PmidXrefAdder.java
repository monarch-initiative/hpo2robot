package org.monarchinitiative.hpo2robot.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.PmidXrefAdderController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PmidXrefAdder extends AnchorPane {
    Logger LOGGER = LoggerFactory.getLogger(PmidXrefAdder.class);

    PmidXrefAdderController controller = null;

    public PmidXrefAdder() {
        super();
        LOGGER.error("PmdXrefAdder");
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
        int x = 42;
        System.out.println(x);
        if (this.controller == null ) {
            LOGGER.error("Controller is null");
            return List.of();
        } else {
            return controller.getPmidList();
        }
    }
}
