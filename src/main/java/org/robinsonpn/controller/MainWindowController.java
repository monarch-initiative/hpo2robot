package org.robinsonpn.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebView;
import org.robinsonpn.EmailManager;
import org.robinsonpn.view.ViewFactory;


public class MainWindowController extends BaseController {


    @FXML
    private TreeView<?> emailTreeview;

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

}
