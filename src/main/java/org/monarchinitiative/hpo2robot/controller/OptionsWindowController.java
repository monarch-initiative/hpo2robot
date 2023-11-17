package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.monarchinitiative.hpo2robot.model.Options;
import org.monarchinitiative.hpo2robot.view.ViewFactory;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class OptionsWindowController extends BaseController implements Initializable {


    private static final String NOT_INITIALIZED = "not initialized";
    @FXML
    public Label hpSrcOntologyLabel;
    @FXML
    public Button hpJsonButton;
    @FXML
    public Button orcidButton;
    @FXML
    public Button hpSrcOntoButton;
    @FXML
    public Button okButton;
    @FXML
    public Button cancelButton;
    public VBox buttonBox;
    @FXML
    private Label hpJsonLabel;
    @FXML
    private Label orcidLabel;

    private StringProperty hpJsonProperty;

    private StringProperty hpSrcOntologyProperty;

    private StringProperty orcidProperty;

    private Options options;


    @FXML
    void okButtonAction() {
        String hp_json = this.hpJsonProperty.get();
        String orcid = orcidProperty.get();
        String hpSrcOntology = hpSrcOntologyProperty.get();
        if (!hp_json.equals(NOT_INITIALIZED)) {
            options.setHpJsonFile(new File(hp_json));
        }
        if (!orcid.equals(NOT_INITIALIZED)) {
            options.setOrcid(orcid);
        }
        if (! hpSrcOntology.equals(NOT_INITIALIZED)) {
            options.setHpSrcOntologyDir(new File(hpSrcOntology));
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
        hpSrcOntologyProperty = new SimpleStringProperty(NOT_INITIALIZED);
        hpSrcOntologyProperty.bindBidirectional(hpSrcOntologyLabel.textProperty());
        orcidProperty = new SimpleStringProperty(NOT_INITIALIZED);
        orcidProperty.bindBidirectional(orcidLabel.textProperty());
        buttonBox.setSpacing(10);
        setupCss();
    }

    private static final String BUTTON_CSS = """
            #bevel-grey {
                -fx-background-color:\s
                    linear-gradient(#f2f2f2, #d6d6d6),
                    linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%),
                    linear-gradient(#dddddd 0%, #f6f6f6 50%);
                -fx-background-radius: 8,7,6;
                -fx-background-insets: 0,1,2;
                -fx-text-fill: black;
                -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );
            }
            """;

    private void setupCss() {
        hpJsonButton.setStyle(BUTTON_CSS);
        hpSrcOntoButton.setStyle(BUTTON_CSS);
        orcidButton.setStyle(BUTTON_CSS);
        okButton.setStyle(BUTTON_CSS);
        cancelButton.setStyle(BUTTON_CSS);
    }

    /**
     * Choose the path to the hp.json file; this file is used for the ontology browser and autocomplete
     * for the parents and does not necessarily have to be the very latest version unless a new term
     * is needed as a parent term.
     */
    public void setHpJsonFile(ActionEvent e) {
        e.consume();
        Optional<File> opt = setFile("Select hp.json File", "JSON file", "*.json");
        if (opt.isPresent()) {
            File f = opt.get();
            this.options.setHpJsonFile(f);
            this.hpJsonProperty.set(f.getAbsolutePath());
        }
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
        if (!orcid.equals(NOT_INITIALIZED)) {
            this.options.setOrcid(orcid);
            this.orcidProperty.set(orcid);
        }
    }

    public Options getOptions() {
        return new Options(hpJsonProperty.get(), orcidProperty.get(),
                hpSrcOntologyProperty.get());
    }

    /**
     * Set the path to the cloned github repository human-phenotype-ontology/src/ontology
     * Note that the repository must be up to date (pull) to avoid BAD THINGS.
     */
    public void setHpSrcOntology(ActionEvent e) {
        e.consume();
        Optional<File> opt = setDirectory("Set hpo/src/ontology folder");
        if (opt.isPresent()) {
            File f = opt.get();
            this.options.setHpSrcOntologyDir(f);
            this.hpSrcOntologyProperty.set(f.getAbsolutePath());
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

    private Optional<File> setDirectory(String title) {
        DirectoryChooser dchooser = new DirectoryChooser();
        dchooser.setTitle(title);
        Stage stage = (Stage) this.hpJsonLabel.getScene().getWindow();
        File f = dchooser.showDialog(stage);
        return Optional.ofNullable(f);
    }


}
