package org.monarchinitiative.hpo2robot.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.monarchinitiative.hpo2robot.model.Synonym;
import org.monarchinitiative.hpo2robot.view.ViewFactory;
import org.monarchinitiative.phenol.ontology.data.SynonymType;
//import org.monarchinitiative.hpo2robot.model.SynonymType;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class SynonymPaneController  extends BaseController implements Initializable {

    @FXML
    private TextField synonymTextField;

    @FXML
    private RadioButton abbreviationRadioButton;

    @FXML
    private RadioButton laypersonRadioButton;

    @FXML
    private RadioButton pluralRadioButton;

    @FXML
    private RadioButton ukRadioButton;

    @FXML
    private RadioButton noSynonymTypeRadioButton;

   private final ToggleGroup radioButtonToggleGroup = new ToggleGroup();

    private SynonymType synonymType = SynonymType.NONE;

    private String synonymLabel = null;

    public SynonymPaneController(ViewFactory viewFactory, String fxmlName) {
        super(viewFactory, fxmlName);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        noSynonymTypeRadioButton.setToggleGroup(radioButtonToggleGroup);
        noSynonymTypeRadioButton.setSelected(true);
        abbreviationRadioButton.setToggleGroup(radioButtonToggleGroup);
        abbreviationRadioButton.setSelected(false);
        laypersonRadioButton.setToggleGroup(radioButtonToggleGroup);
        laypersonRadioButton.setSelected(false);
        pluralRadioButton.setToggleGroup(radioButtonToggleGroup);
        pluralRadioButton.setSelected(false);
        ukRadioButton.setToggleGroup(radioButtonToggleGroup);
        ukRadioButton.setSelected(false);
        noSynonymTypeRadioButton.setUserData(SynonymType.NONE);
        abbreviationRadioButton.setUserData(SynonymType.ABBREVIATION);
        laypersonRadioButton.setUserData(SynonymType.LAYPERSON_TERM);
        pluralRadioButton.setUserData(SynonymType.PLURAL_FORM);
        ukRadioButton.setUserData(SynonymType.UK_SPELLING);
        radioButtonToggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (radioButtonToggleGroup.getSelectedToggle() != null) {
                final SynonymType userData = (SynonymType) radioButtonToggleGroup.getSelectedToggle().getUserData();
                setSelectedButton(userData);
            } else {
                setSelectedButton(SynonymType.NONE);
            }
        });
    }

    private void setSelectedButton(SynonymType udata) {
        this.synonymType = udata;
    }

    public Optional<Synonym> getSynonym() {
        if (synonymLabel == null) {
            return Optional.empty();
        } else {
            Synonym syn = new Synonym(synonymLabel, synonymType);
            return Optional.of(syn);
        }
    }



    @FXML
    public void cancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) this.synonymTextField.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    @FXML
    public void okAction(ActionEvent e) {
        Stage stage = (Stage) this.synonymTextField.getScene().getWindow();
        String syntext = synonymTextField.getText();
        this.synonymLabel =  syntext.replaceAll("  ", " ").trim();
        this.synonymType = (SynonymType) radioButtonToggleGroup.getSelectedToggle().getUserData();
        viewFactory.closeStage(stage);
    }
}
