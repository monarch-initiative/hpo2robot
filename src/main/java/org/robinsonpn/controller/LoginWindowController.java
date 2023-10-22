package org.robinsonpn.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.robinsonpn.EmailManager;
import org.robinsonpn.controller.services.LoginService;
import org.robinsonpn.model.EmailAccount;
import org.robinsonpn.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController extends BaseController implements Initializable {

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
    }

    @FXML
    void loginButtonAction() {
        System.out.println("loginButtonAction top - emailAdress: " + emailAddressFied.getText());
        System.out.println("loginButtonAction fieldsValid: " + fieldsAreValid());
        if (fieldsAreValid()) {

            EmailAccount account = new EmailAccount(emailAddressFied.getText(), passwordField.getText());
            LoginService service = new LoginService(account, emailManager);
            service.start();
            service.setOnSucceeded(event -> {
                EmailLoginResult result = service.getValue();
                System.out.println("result="+result);
                switch (result) {

                    case SUCCESS -> {
                        System.out.println("success with " + account);
                        viewFactory.showMainWindow();
                        Stage stage = (Stage) this.errorLabel.getScene().getWindow();
                        viewFactory.closeStage(stage);
                        return;}
                    case FAILED_BY_CREDENTIALS -> {
                        System.err.println("Invalid credentials");
                        return;
                    }
                    case FAILED_BY_NETWORK -> {
                        System.err.println("Network problem");
                        return;
                    }
                    case UNEXPECTED_ERROR -> {
                        System.err.println("Unexpected error");
                        return;
                    }
                }
            });
            service.setOnFailed(event -> {
                System.out.println("failed to get email");
                System.out.println(event.toString());
            });
        }

        viewFactory.showMainWindow();
        Stage stage = (Stage) this.errorLabel.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    private boolean fieldsAreValid() {
        if (emailAddressFied.getText().isEmpty()) {
            errorLabel.setText("Email field empty");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            errorLabel.setText("Password field empty");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

