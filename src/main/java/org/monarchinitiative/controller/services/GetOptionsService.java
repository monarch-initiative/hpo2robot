package org.monarchinitiative.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.monarchinitiative.model.Options;
import org.monarchinitiative.view.ViewFactory;

public class GetOptionsService extends Service<Options> {
   // Logger LOGGER = null;


    public GetOptionsService( ) {
    }


    @Override
    protected Task<Options> createTask() {
        ViewFactory viewFactory = new ViewFactory();
        viewFactory.showOptionsWindow();
        Options options = viewFactory.getOptions();
        return new Task<>() {
            @Override
            protected Options call() {
                return options;
            }
        };
    }
}
