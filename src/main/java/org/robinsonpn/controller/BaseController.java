package org.robinsonpn.controller;

import org.robinsonpn.EmailManager;
import org.robinsonpn.view.ViewFactory;



public abstract class BaseController {

    protected final EmailManager emailManager;

    protected final ViewFactory viewFactory;

    private final String fxmlName;

    public BaseController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
        this.emailManager = emailManager;
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    /**
     * We store the FXML files in the resources/org/robinsonpn/view directory
     * @return name of the FXML file to be used to create the widget
     */
    public String getFxmlName() {
        return fxmlName;
    }




}
