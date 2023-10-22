package org.robinsonpn.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.robinsonpn.EmailManager;
import org.robinsonpn.view.ViewFactory;

public class LoginWindowController extends BaseController {

    @FXML
    private TextField emailAddressFied;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    public LoginWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
        //loginButton.setOnAction(e -> loginButtonAction(e));
    }

    @FXML
    void loginButtonAction(ActionEvent event) {
        System.out.println("KJHKJHKJHK");
        viewFactory.showMainWindow();
        Stage stage = (Stage) this.errorLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

}

