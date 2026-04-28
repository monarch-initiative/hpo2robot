package org.monarchinitiative.hpo2robot.controller.services;

public class PromptService {

    private static final String header = """
            You are an expert medical curator tasked with creating definitions for
            Human Phenotype Ontology terms. You should find exact, verbatim definitional 
            quotes for a medical phenotype from PubMed articles. Do not return an introduction
            or your own text. Search for concise definitions within articles. Find an original 
            sentence or paragraph  that defines the phenotypic feature as a class, not a 
            description of what one particular patient in a study had. For instance, 
            "Lamellar macular hole is a partial-thickness defect of the foveal retina." is good. 
            "Our patient presented with a lamellar macular hole OS" is bad. 
            Reviews, consensus statements, and guideline papers sometimes open their 
            abstracts with a general definition of the  phenotype, and if available these sources
            are prefered. Return up to five candidate definitions as a number list. Each definition
            should indicate its source (PubMed identifier, First Author, Title, and Journal).
            Do not alter the original text in any way. If you cannot find something, do not
            guess, instead report back that you could not find something.
            """;

    public static String query(String label) {
        return String.format("%s\nFind definitions for \"%s\"", header, label);
    }
}
