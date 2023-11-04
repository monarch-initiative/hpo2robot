package org.monarchinitiative.hpo2robot.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;

public class RobotItem {

    private TermId newTermId;

    private final StringProperty newTermLabelProperty;

    private final StringProperty issueProperty;

    private final StringProperty parentTermLabelProperty;

    private final StringProperty newTermDefinitionProperty;

    private final StringProperty newTermCommentProperty;

    private final StringProperty pmidStringProperty;

    private final List<Term> parentTerms;

    private final List<String> pmids;


    public RobotItem(String newTermLabel, List<Term> parentTerms, String definition, String comment, List<String> pmids) {
        this.newTermLabelProperty = new SimpleStringProperty(newTermLabel);
        this.newTermDefinitionProperty = new SimpleStringProperty(definition);
        this.newTermCommentProperty = new SimpleStringProperty(comment);
        this.parentTerms = parentTerms;
        issueProperty = new SimpleStringProperty("42");
        if (parentTerms.isEmpty()) {
            parentTermLabelProperty = new SimpleStringProperty("Error - no term found");
        } else {
            String firstParentTermLabel = parentTerms.get(0).getName();
            firstParentTermLabel = firstParentTermLabel.length() < 45
                    ? firstParentTermLabel
                    : firstParentTermLabel.substring(0,45) + "...";
            parentTermLabelProperty = new SimpleStringProperty(firstParentTermLabel);
        }
        this.pmids = pmids;
        pmidStringProperty = new SimpleStringProperty("n/a");

    }

    public StringProperty parentTermLabelPropertyProperty() {
        return parentTermLabelProperty;
    }
    public String parentTermLabel() {
        return parentTermLabelProperty.get();
    }

    public TermId getNewTermId() {
        return newTermId;
    }

    public void setNewTermId(TermId newTermId) {
        this.newTermId = newTermId;
    }

    public StringProperty getNewTermLabelProperty() {
        return newTermLabelProperty;
    }

    public String getNewTermLabel() {
        return newTermLabelProperty.get();
    }



    public StringProperty getNewTermDefinitionProperty() {
        return newTermDefinitionProperty;
    }

    public String getNewTermDefinition() {
        return newTermDefinitionProperty.get();
    }

    public StringProperty getNewTermCommentProperty() {
        return newTermCommentProperty;
    }

    public String getNewTermComment() {
        return newTermCommentProperty.get();
    }

    public List<Term> getParentTerms() {
        return parentTerms;
    }

    public List<String> getPmids() {
        return pmids;
    }


    public StringProperty getIssueProperty() {
        return issueProperty;
    }

    public String getIssue() {
        return issueProperty.get();
    }


    public void addPmid(String pmid) {
        this.pmids.add(pmid);
    }

    public StringProperty getPmidStringProperty() {
        return this.pmidStringProperty;
    }

    public String getPmidString() {
        return this.pmidStringProperty.get();
    }
}
