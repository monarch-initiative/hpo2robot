package org.monarchinitiative.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.monarchinitiative.Launcher;
import org.monarchinitiative.controller.BaseController;
import org.monarchinitiative.controller.MainWindowController;
import org.monarchinitiative.controller.OptionsWindowController;
import org.monarchinitiative.model.Options;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViewFactory {


    private final List<Stage> activeStageList;

    private Options options;

    public ViewFactory(Options options) {
        this.options = options;
        activeStageList = new ArrayList<>();
    }

    public ViewFactory() {
        this.options = new Options(); // initialize to default (empty)
        activeStageList = new ArrayList<>();
    }


    private Optional<Parent>  initializeBaseStage(BaseController controller) {
        String fxmlDir = "view";
        URL location = getLocation(fxmlDir, controller.getFxmlName());
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
            }};

        loader.setControllerFactory(controllerFactory);
        // loader.setController(controller);
        Parent parent;
        try {
            parent = loader.load();
            return  Optional.of(parent);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private void initializeStage(BaseController controller) {
        Optional<Parent> opt = initializeBaseStage(controller);
        if (opt.isEmpty()) {
            System.err.println("[ERROR] could not initialize stage");
            return;
        }
        Parent parent = opt.get();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        activeStageList.add(stage);
    }



    private void initializeStageAndWait(BaseController controller) {
        Optional<Parent> opt = initializeBaseStage(controller);
        if (opt.isEmpty()) {
            System.err.println("[ERROR] could not initialize stage");
            return;
        }
        Parent parent = opt.get();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.showAndWait();
    }


    public void showMainWindow() {
        BaseController controller = new MainWindowController(this, "MainWindow.fxml");
        initializeStage(controller);
    }


    public void showOptionsWindow() {
        OptionsWindowController controller = new OptionsWindowController( this, "OptionsWindow.fxml");
        initializeStageAndWait(controller);
        this.options = controller.getOptions();
        System.out.println("ShowOptionsWindow: " + options);
    }

    public URL getLocation(String dir, String fxmlName) {
        String path = dir + File.separator + fxmlName;
        URL location = Launcher.class.getResource(path);
        return location;
    }

    public void closeStage(Stage stage) {
        stage.close();
        activeStageList.remove(stage);
    }



    public Options getOptions() {
        return this.options;
    }
}
