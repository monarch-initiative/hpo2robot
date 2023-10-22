package org.monarchinitiative;

import javafx.application.Application;
import javafx.stage.Stage;
import org.monarchinitiative.view.ViewFactory;

/**
 * JavaFX App
 */
public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        // todo persistence management
        // only open options if we need to add some data
        ViewFactory viewFactory = new ViewFactory(new EmailManager());
        viewFactory.showOptionsWindow();
    }


}