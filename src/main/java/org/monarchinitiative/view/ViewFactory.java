package org.monarchinitiative.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.monarchinitiative.EmailManager;
import org.monarchinitiative.Launcher;
import org.monarchinitiative.controller.BaseController;
import org.monarchinitiative.controller.LoginWindowController;
import org.monarchinitiative.controller.MainWindowController;
import org.monarchinitiative.controller.OptionsWindowController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewFactory {
    private final EmailManager emailManager;

    private ColorTheme colorTheme = ColorTheme.DEFAULT;

    private FontSize fontSize = FontSize.MEDIUM;


    private List<Stage> activeStageList;

    public ViewFactory(EmailManager emailManager) {
        this.emailManager = emailManager;
        activeStageList = new ArrayList<>();
    }

    public void showLoginWindow()  {
        BaseController controller = new LoginWindowController(emailManager, this, "LoginWindow.fxml");
        initializeStage(controller);
    }

    private  void initializeStage(BaseController controller) {
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
        activeStageList.add(stage);
    }


    public void showMainWindow() {
        BaseController controller = new MainWindowController(emailManager, this, "MainWindow.fxml");
        initializeStage(controller);
    }


    public void showOptionsWindow() {
        BaseController controller = new OptionsWindowController(emailManager, this, "OptionsWindow.fxml");
        initializeStage(controller);
    }

    public URL getLocation(String dir, String fxmlName) {
        String path = dir + File.separator + fxmlName;
        URL location = Launcher.class.getResource(path);
//        System.out.printf("path = %s\n",path);
//        System.out.println("getLocation="+location);
        return location;
    }

    public void closeStage(Stage stage) {
        stage.close();
        activeStageList.remove(stage);
    }

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(ColorTheme colorTheme) {
        this.colorTheme = colorTheme;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }

    public void updateStyles() {
        for (Stage stage : activeStageList) {
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            String cssDir = "css";
            String cssName = ColorTheme.getCssFileName(getColorTheme());
            URL location =  getLocation(cssDir, cssName);
            scene.getStylesheets().add(location.toExternalForm());
            cssName = FontSize.getCssFileName(getFontSize());
            location =  getLocation(cssDir, cssName);
            scene.getStylesheets().add(location.toExternalForm());
        }
    }
}
