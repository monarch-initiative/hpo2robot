package org.robinsonpn.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.robinsonpn.EmailManager;
import org.robinsonpn.Launcher;
import org.robinsonpn.controller.BaseController;
import org.robinsonpn.controller.LoginWindowController;
import org.robinsonpn.controller.MainWindowController;
import org.robinsonpn.controller.OptionsWindowController;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ViewFactory {
    private final EmailManager emailManager;

    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    public void showLoginWindow()  {
        BaseController controller = new LoginWindowController(emailManager, this, "LoginWindow.fxml");
        initializeStage(controller);
    }

    private  void initializeStage(BaseController controller) {
        URL location = getLocation(controller.getFxmlName());
        FXMLLoader loader = new FXMLLoader(location);

        // This is used to customize the creation of controller injected by javaFX when defining them with fx:controller attribute inside FXML files
        // Using the controller factory instead of setting the controller directly with fxmlLoader.setController() allows us to keep the fx:controller
        // attribute inside FXML files. This makes it easier for IDE to link fxml with controllers and check for errors
        Callback<Class<?>, Object> controllerFactory = type -> {
            // Any controller that needs custom constructor behavior needs to be defined above this check
            if (BaseController.class.isAssignableFrom(type)) {
                // A default behavior for controllerFactory for all classes extends from base controller.
                return controller ;
            } else {
                // default behavior for controllerFactory:
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception exc) {
                    exc.printStackTrace();
                    throw new RuntimeException(exc); // fatal, just bail...
                }
            }
        };

        loader.setControllerFactory(controllerFactory);
       // loader.setController(controller);
        Parent parent;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.getStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    public void showMainWindow() {
        BaseController controller = new MainWindowController(emailManager, this, "MainWindow.fxml");
        initializeStage(controller);
    }


    public void showOptionsWindow() {
        BaseController controller = new OptionsWindowController(emailManager, this, "OptionsWindow.fxml");
        initializeStage(controller);
    }

    public URL getLocation(String fxmlName) {
        String path = "view" + File.separator + fxmlName;
        System.out.printf("path = %s\n",path);
        URL location = Launcher.class.getResource(path);
        System.out.println("getLocation="+location);
        return location;
    }

    public void closeStage(Stage stage) {
        stage.close();
    }


}
