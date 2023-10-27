package org.monarchinitiative.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import org.monarchinitiative.Launcher;
import org.monarchinitiative.controller.ParentTermAdderController;
import org.monarchinitiative.model.HpoRosettaStone;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParentTermAdder extends HBox {

    private ParentTermAdderController controller;

    private StringProperty parentTermLabelStringProperty;


    private HpoRosettaStone rosettaStone = null;

    public void setOntology(Ontology ontology) {
        rosettaStone = new HpoRosettaStone(ontology);
    }

    ParentTermAdder() {
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


    List<Term> getParentTermList() {
        if (rosettaStone == null ) {
            System.err.println("TODO ERROR MESSAGE LOGGING");
            return List.of();
        }
        List<Term> parentTermList  = new ArrayList<>();;
        ObservableList<String> parentTermLabelss = controller.getParentList();
        for (String label : parentTermLabelss) {
            Optional<Term> termOpt = rosettaStone.termFromPrimaryLabel(label);
            termOpt.ifPresent(parentTermList::add);
        }
        return parentTermList;
    }



}
