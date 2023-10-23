package org.monarchinitiative;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.controller.persistence.PersistenceAccess;
import org.monarchinitiative.model.Options;
import org.monarchinitiative.view.ViewFactory;

import java.util.Optional;

/**
 * A JavaFX App for creating ROBOT templates to add or modify terms
 * of the Human Phenotype Ontology (HPO)
 * @author Peter Robinson
 */
public class Launcher extends Application {

    private final PersistenceAccess persistenceAccess = new PersistenceAccess();
    ViewFactory viewFactory = null;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        Optional<Options> opt = persistenceAccess.loadFromPersistence();
        Options options = opt.orElse(new Options()); // default to empty
        Hpo2RobotManager manager = new Hpo2RobotManager(options);
        viewFactory = new ViewFactory(manager);
        if (opt.isPresent()) {
            viewFactory.showMainWindow();
        } else {
            viewFactory.showOptionsWindow();
        }
    }

    @Override
    public void stop() throws Exception {
        if (viewFactory != null) {
            persistenceAccess.saveToPersistence(viewFactory.getOptions());
        }
    }
}