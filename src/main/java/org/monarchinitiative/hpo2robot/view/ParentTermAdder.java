package org.monarchinitiative.hpo2robot.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.controlsfx.control.textfield.TextFields;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.controller.ParentTermAdderController;
import org.monarchinitiative.hpo2robot.model.HpoRosettaStone;
import org.monarchinitiative.phenol.ontology.data.MinimalOntology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**]
 * Widget for adding parent terms to the current ROBOT entry
 * @author Peter N Robinson
 */
public class ParentTermAdder extends HBox {
    private final Logger LOGGER = LoggerFactory.getLogger(ParentTermAdder.class);
    private ParentTermAdderController controller;

    private StringProperty parentTermLabelStringProperty;


    private HpoRosettaStone rosettaStone = null;



    public ParentTermAdder() {
        super();
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("view/ParentTermAdder.fxml"));
            controller = new ParentTermAdderController();
            loader.setController(controller);
            Node node = loader.load();
            this.getChildren().add(node);
            parentTermLabelStringProperty = new SimpleStringProperty("");
            parentTermLabelStringProperty.bindBidirectional(controller.parentTermProperty());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOntology(MinimalOntology ontology) {
        if (ontology == null) {
            LOGGER.error("Attempt to set Ontology with null pointer.");
            return;
        }
        rosettaStone = new HpoRosettaStone(ontology);
        TextFields.bindAutoCompletion(controller.getTextField(), rosettaStone.allLabels());
    }


    public List<Term> getParentTermList() {
        Set<String> parentTermLabelSet = controller.getParentSet();
        List<Term> parentList = new ArrayList<>();
        for (String parentLabel : parentTermLabelSet) {
            Optional<Term> opt = rosettaStone.termFromPrimaryLabel(parentLabel);
            opt.ifPresent(parentList::add);
        }
        return parentList;
    }

    public void setParentTerm(String label) {
        this.controller.getTextField().setText(label);
    }

    public void clearFields() {
        this.controller.clearFields();
    }
}
