package org.monarchinitiative.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.monarchinitiative.phenol.io.OntologyLoader;
import org.monarchinitiative.phenol.ontology.data.Ontology;

import java.io.File;

public class LoadHpoService extends Service<Ontology> {


    private String hpoJsonFilePath;
    private Ontology hpoOntology;
    public LoadHpoService(String hpJsonFile) {
        this.hpoJsonFilePath = hpJsonFile;
    }

    @Override
    protected Task<Ontology> createTask() {
        hpoOntology = OntologyLoader.loadOntology(new File(hpoJsonFilePath));
        return new Task<>() {
            @Override
            protected Ontology call() {
                return hpoOntology;
            }
        };
    }
}
