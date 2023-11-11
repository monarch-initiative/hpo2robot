package org.monarchinitiative.hpo2robot;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;
import org.monarchinitiative.hpo2robot.controller.persistence.PersistenceAccess;
import org.monarchinitiative.hpo2robot.controller.services.GetOptionsService;
import org.monarchinitiative.hpo2robot.model.Options;
import org.monarchinitiative.hpo2robot.view.ViewFactory;

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
        Options options = PersistenceAccess.loadFromPersistence();
        HostServices hostServices = getHostServices();
        viewFactory = new ViewFactory(options, hostServices);
        viewFactory.showMainWindow();
    }

    private Options getOptions() {
        GetOptionsService service = new GetOptionsService();
        service.setOnSucceeded(e -> System.out.println("[INFO] Got Options"));
        service.setOnFailed(e -> System.err.println("[ERROR] Could not get valid options."));

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
    public void stop() {
        if (viewFactory != null) {
            PersistenceAccess.saveToPersistence(viewFactory.getOptions());
        }



    }
}