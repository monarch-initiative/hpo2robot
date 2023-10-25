package org.monarchinitiative;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.controller.persistence.PersistenceAccess;
import org.monarchinitiative.controller.services.GetOptionsService;
import org.monarchinitiative.controller.services.LoadHpoService;
import org.monarchinitiative.model.Options;
import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.view.ViewFactory;

import java.util.Optional;

/**
 * A JavaFX App for creating ROBOT templates to add or modify terms
 * of the Human Phenotype Ontology (HPO)
 * @author Peter Robinson
 */
public class Launcher extends Application {


    ViewFactory viewFactory = null;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        PersistenceAccess persistenceAccess = new PersistenceAccess();
        Optional<Options> opt = persistenceAccess.loadFromPersistence();
        Options options = opt.orElse(new Options());
        viewFactory = new ViewFactory(options);
        viewFactory.showMainWindow();
    }

    private Options getOptions() {
        GetOptionsService service = new GetOptionsService();
        service.setOnSucceeded(e -> {
            System.out.println("[INFO] Got Options");
        });
        service.setOnFailed(e -> {
            System.err.println("[ERROR] Could not get valid options.");
        });

        service.start();


        service.setOnSucceeded(e -> {
            Options options =  service.getValue();
            System.out.println("SUCCESS");
            System.out.println(options);
            System.exit(0);

        });


        return null;
    }

    @Override
    public void stop() throws Exception {
        if (viewFactory != null) {
            PersistenceAccess persistenceAccess = new PersistenceAccess();
            persistenceAccess.saveToPersistence(viewFactory.getOptions());
        }



    }
}