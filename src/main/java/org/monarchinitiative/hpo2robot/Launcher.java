package org.monarchinitiative.hpo2robot;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

import org.monarchinitiative.hpo2robot.controller.persistence.PersistenceAccess;
import org.monarchinitiative.hpo2robot.model.Options;
import org.monarchinitiative.hpo2robot.view.ViewFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JavaFX App for creating ROBOT templates to add or modify terms
 * of the Human Phenotype Ontology (HPO)
 * @author Peter Robinson
 */
public class Launcher extends Application {
    Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    ViewFactory viewFactory = null;

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        LOGGER.info("STARTING app");
        Options options = PersistenceAccess.loadFromPersistence();
        HostServices hostServices = getHostServices();
        viewFactory = new ViewFactory(options, hostServices);
        viewFactory.showMainWindow(stage);
       
        stage.setOnCloseRequest(e -> PersistenceAccess.saveToPersistence(viewFactory.getOptions()));
        stage.setTitle("HPO2Robot");

        InputStream iconStream = getClass().getResourceAsStream("/icons/robot-icon.png");
        if (iconStream != null) {
            stage.getIcons().add(new Image(iconStream));
            LOGGER.info("Loaded icon file!");
        } else {
            LOGGER.error("Could DID find icon file!");
        }
        // Native macOS Dock Icon implementation
        try {
            if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                java.net.URL iconUrl = getClass().getResource("/icons/robot-icon.png");
                if (iconUrl != null && java.awt.Taskbar.isTaskbarSupported()) {
                    java.awt.Image awtImage = java.awt.Toolkit.getDefaultToolkit().getImage(iconUrl);
                    java.awt.Taskbar.getTaskbar().setIconImage(awtImage);
                    LOGGER.info("Successfully forced macOS Dock icon!");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Could not set macOS Dock icon dynamically: {}", e.getMessage());
    }

    }

    @Override
    public void stop() {
        if (viewFactory != null) {
            PersistenceAccess.saveToPersistence(viewFactory.getOptions());
        }
    }
}