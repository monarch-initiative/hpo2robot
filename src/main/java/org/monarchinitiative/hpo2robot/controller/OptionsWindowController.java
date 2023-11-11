package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.monarchinitiative.hpo2robot.Launcher;
import org.monarchinitiative.hpo2robot.model.Options;
import org.monarchinitiative.hpo2robot.view.ViewFactory;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class OptionsWindowController extends BaseController implements Initializable {


    private static final String NOT_INITIALIZED = "not initialized";
    @FXML
    public Label hpEditOwlLabel;
    @FXML
    private Label hpJsonLabel;


    @FXML
    private Label orcidLabel;


    private StringProperty hpJsonProperty;

    private StringProperty hpEditOwlProperty;



    private StringProperty orcidProperty;


    private Options options;


    @FXML
    void okButtonAction() {
       String hp_json = this.hpJsonProperty.get();
        String orcid = orcidProperty.get();
        if (! hp_json.equals(NOT_INITIALIZED)) {
            options.setHpJsonFile(new File(hp_json));
        }
        if (! orcid.equals(NOT_INITIALIZED)) {
            options.setOrcid(orcid);
        }
        Stage stage = (Stage) this.orcidLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    @FXML
    void cancelButtonAction() {
        Stage stage = (Stage) this.orcidLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    public OptionsWindowController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.options = new Options();
        hpJsonProperty = new SimpleStringProperty(NOT_INITIALIZED);
        hpJsonProperty.bindBidirectional(hpJsonLabel.textProperty());
        hpEditOwlProperty = new SimpleStringProperty(NOT_INITIALIZED);
        hpEditOwlProperty.bindBidirectional(hpEditOwlLabel.textProperty());
        orcidProperty = new SimpleStringProperty(NOT_INITIALIZED);
        orcidProperty.bindBidirectional(orcidLabel.textProperty());
        //setupCss();
    }

    private void setupCss() {
        try {
            Scene scene = this.hpJsonLabel.getScene();
            scene.getStylesheets().add(Launcher.class.getResource("css/application.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("[ERROR] Could not set up CSS for options: " + e.getMessage());
        }
    }


    public void setHpJsonFile(ActionEvent e) {
        e.consume();
        Optional<File> opt = setFile("Select hp.json File", "JSON file", "*.json");
        if (opt.isPresent()) {
            File f = opt.get();
            this.options.setHpJsonFile(f);
            this.hpJsonProperty.set(f.getAbsolutePath());
        }
    }



    public Optional<File> createRobotFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Set location of new robot (.tsv) File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Robot files", "*.tsv"));
        Stage stage = (Stage) this.hpJsonLabel.getScene().getWindow();
        //fileChooser.
        File f = fileChooser.showSaveDialog(stage);
        return Optional.ofNullable(f);
    }



    /**
     * Request a String from user.
     *
     * @param windowTitle - Title of PopUp window
     * @param promptText  - Prompt of Text field (suggestion for user)
     * @param labelText   - Text of your request
     * @return String with user input
     */
    public static String getStringFromUser(String windowTitle, String promptText, String labelText) {
        TextInputDialog dialog = new TextInputDialog(promptText);
        dialog.setTitle(windowTitle);
        dialog.setHeaderText(null);
        dialog.setContentText(labelText);
        Optional<String> result = dialog.showAndWait();
        return result.orElse(NOT_INITIALIZED);

    }

    public void setORCID(ActionEvent actionEvent) {
        String orcid = getStringFromUser("Set Biocurator ORCID",
                "ORCID CURIE:", "orcid"
                );
        if (! orcid.equals(NOT_INITIALIZED)) {
            this.options.setOrcid(orcid);
            this.orcidProperty.set(orcid);
        }
    }

    public Options getOptions() {
        return new Options(hpJsonProperty.get(), orcidProperty.get(),
                hpEditOwlProperty.get());
    }

    public void hpJsonDownload(ActionEvent actionEvent) {
        System.err.println("TODO IMPLEMENT ME");
    }


    public void setHpEditOwl(ActionEvent e) {
        e.consume();
        Optional<File> opt = setFile("Choose hp-edit.owl file", "OWL file", "*.owl");
        if (opt.isPresent()) {
            File f = opt.get();
            this.options.setHpEditOwlFile(f);
            this.hpEditOwlProperty.set(f.getAbsolutePath());
        }
    }


    private Optional<File> setFile(String title, String extensionFileDisplay, String suffix) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(extensionFileDisplay, suffix));
        Stage stage = (Stage) this.hpJsonLabel.getScene().getWindow();
        File f = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(f);
    }



}
