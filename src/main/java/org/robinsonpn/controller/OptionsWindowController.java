package org.robinsonpn.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import org.robinsonpn.EmailManager;
import org.robinsonpn.view.ViewFactory;

public class OptionsWindowController extends BaseController {


    @FXML
    private Slider fontSizePicker;

    @FXML
    private ChoiceBox<?> themePicker;



    @FXML
    void cancelButtonAction() {

    }

    @FXML
    void okButtonAction() {

    }

    public OptionsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

}
