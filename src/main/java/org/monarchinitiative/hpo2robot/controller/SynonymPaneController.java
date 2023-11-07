package org.monarchinitiative.hpo2robot.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import org.monarchinitiative.phenol.ontology.data.SynonymType;
//import org.monarchinitiative.hpo2robot.model.SynonymType;

import java.net.URL;
import java.util.ResourceBundle;

public class SynonymPaneController implements Initializable {

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

    private SynonymType synonymType = SynonymType.NONE;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final ToggleGroup group = new ToggleGroup();
        noSynonymTypeRadioButton.setToggleGroup(group);
        noSynonymTypeRadioButton.setSelected(true);
        abbreviationRadioButton.setToggleGroup(group);
        abbreviationRadioButton.setSelected(false);
        laypersonRadioButton.setToggleGroup(group);
        laypersonRadioButton.setSelected(false);
        pluralRadioButton.setToggleGroup(group);
        pluralRadioButton.setSelected(false);
        ukRadioButton.setToggleGroup(group);
        ukRadioButton.setSelected(false);
        noSynonymTypeRadioButton.setUserData(SynonymType.NONE);
        abbreviationRadioButton.setUserData(SynonymType.ABBREVIATION);
        laypersonRadioButton.setUserData(SynonymType.LAYPERSON_TERM);
        pluralRadioButton.setUserData(SynonymType.PLURAL_FORM);
        ukRadioButton.setUserData(SynonymType.UK_SPELLING);
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (group.getSelectedToggle() != null) {
                    final SynonymType userData = (SynonymType) group.getSelectedToggle().getUserData();
                    setSelectedButton(userData);
                } else {
                    setSelectedButton(SynonymType.NONE);
                }
            }
        });
    }

    private void setSelectedButton(SynonymType udata) {
        this.synonymType = udata;
    }
}
