package org.monarchinitiative.hpo2robot.model;

import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.Set;

public class RobotItem {

    private TermId newTermId;

    private String newTermLabel;

    private String newTermDefinition;

    private String newTermComment;

    private final Set<Term> parentTerms;

    private final Set<String> pmids;


    public RobotItem() {
        parentTerms = new HashSet<>();
        pmids = new HashSet<>();

    }

    public TermId getNewTermId() {
        return newTermId;
    }

    public void setNewTermId(TermId newTermId) {
        this.newTermId = newTermId;
    }

    public String getNewTermLabel() {
        return newTermLabel;
    }

    public void setNewTermLabel(String newTermLabel) {
        this.newTermLabel = newTermLabel;
    }

    public String getNewTermDefinition() {
        return newTermDefinition;
    }

    public void setNewTermDefinition(String newTermDefinition) {
        this.newTermDefinition = newTermDefinition;
    }

    public String getNewTermComment() {
        return newTermComment;
    }

    public void setNewTermComment(String newTermComment) {
        this.newTermComment = newTermComment;
    }

    public Set<Term> getParentTerms() {
        return parentTerms;
    }


    public void addParentTerm(Term term) {
        this.parentTerms.add(term);
    }

    public Set<String> getPmids() {
        return pmids;
    }



    public void addPmid(String pmid) {
        this.pmids.add(pmid);
    }
}
