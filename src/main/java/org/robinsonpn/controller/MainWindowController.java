package org.robinsonpn.controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import org.robinsonpn.EmailManager;
import org.robinsonpn.view.ViewFactory;

import java.net.URL;
import java.util.ResourceBundle;


public class MainWindowController extends BaseController implements Initializable {


    @FXML
    private TreeView<String> emailTreeview;

    @FXML
    private TableView<?> emailsTableview;

    @FXML
    private WebView emailsWebview;

    public MainWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        super(emailManager, viewFactory, fxmlName);
    }

    @FXML
    void optionsAction() {
        this.viewFactory.showOptionsWindow();
    }

    public void addAccount() {
        viewFactory.showLoginWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUpEmailTreeview();
    }

    private void setUpEmailTreeview() {
        emailTreeview.setRoot(emailManager.getFoldersRoot());
        emailTreeview.setShowRoot(false);
    }
}
