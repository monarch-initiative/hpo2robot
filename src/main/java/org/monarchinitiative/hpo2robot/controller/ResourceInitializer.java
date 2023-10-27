package org.monarchinitiative.hpo2robot.controller;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermSynonym;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create a label to prefered label map for use in Automcompletion
 * Should be used within a Task in the Main controller
 */
@Deprecated(forRemoval = true)
public class ResourceInitializer {
    private final Logger LOGGER = LoggerFactory.getLogger(ResourceInitializer.class);


    public static Map<String, String> initializeLabelMap(Ontology hpoOntology) {
        Map<String, String> labelMap = new HashMap<>();

        for (Term hterm : hpoOntology.getTerms()) {
            if (hterm.isObsolete()) {
                continue;
            }
            String label = hterm.getName();
            String id = hterm.id().getValue();
            labelMap.put(label, label);
            labelMap.put(id, label);
            List<TermSynonym> syns = hterm.getSynonyms();
            if (syns != null) {
                for (TermSynonym syn : syns) {
                    String synlabel = syn.getValue();
                    labelMap.put(synlabel, label);
                }
            }
        }
        return Map.copyOf(labelMap);
    }
}
