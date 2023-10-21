package org.robinsonpn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class Loader extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws IOException {
        URL location = getClass().getResource("view/LoginWindow.fxml");
        Parent parent = FXMLLoader.load(location);
        Scene scene = new Scene(parent, 510, 325);
        stage.setScene(scene);
        stage.show();

        System.out.println("hello");
    }


}