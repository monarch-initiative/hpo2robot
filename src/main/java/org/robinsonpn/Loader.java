package org.robinsonpn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class Loader extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage stage) throws IOException {
        /*var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene); */
        System.out.println(Loader.class);
        //URL url = getClass().getResource("fxml/")
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("view/LoginWindow.fxml"));
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/LoginWindow.fxml"));

      // Parent parent = fxmlLoader.load();
        //System.err.println("parent " + root);
      Scene scene = new Scene(root, 300,200);
      stage.setScene(scene);
        stage.show();

        //System.err.println("url - " + parent);
       // URL url2 = Objects.requireNonNull(getClass().getResource("view/first.fxml"));

       /* Parent parent = FXMLLoader.load(url);


        */
        System.out.println("hello");
    }



}