package org.robinsonpn;

import javafx.application.Application;
import javafx.stage.Stage;
import org.robinsonpn.view.ViewFactory;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        /*URL location = getClass().getResource("view/MainWindow.fxml");
        Parent parent = FXMLLoader.load(location);
        Scene scene = new Scene(parent, 510, 325);
        stage.setScene(scene);
        stage.show();*/

        ViewFactory viewFactory = new ViewFactory(new EmailManager());
        viewFactory.showLoginWindow();
        viewFactory.updateStyles();

    }


}