package org.monarchinitiative.hpo2robot.model;

import org.monarchinitiative.phenol.ontology.data.SynonymType;

public record Synonym(String label, SynonymType synonymType) {


    @Override
    public String toString() {
        return String.format("%s(%s)", label, synonymType.name());
    }

}
