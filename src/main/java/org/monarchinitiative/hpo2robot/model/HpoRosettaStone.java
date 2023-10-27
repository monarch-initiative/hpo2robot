package org.monarchinitiative.hpo2robot.model;

import org.monarchinitiative.phenol.ontology.data.Ontology;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HpoRosettaStone {

    private final Ontology hpo;

    private final Map<String, Term> labelToTermMap;

    private final Map<TermId, String> idToLabelMap;

    public HpoRosettaStone(Ontology ontology) {
        this.hpo = ontology;
        Map<String, Term> labelMap = new HashMap<>();
        hpo.getTerms().forEach(term ->  labelMap.putIfAbsent(term.getName(), term));
        this.labelToTermMap = Map.copyOf(labelMap);
        Map<TermId, String> idMap = new HashMap<>();
        hpo.getTerms().forEach(term ->  idMap.putIfAbsent(term.id(), term.getName()));
        idToLabelMap = Map.copyOf(idMap);
    }

    public Optional<Term> termFromPrimaryLabel(String label) {
        return Optional.ofNullable(labelToTermMap.get(label));
    }

    public Optional<String> primaryLabelFromId(TermId tid) {
        return Optional.ofNullable(idToLabelMap.get(tid));
    }




}
