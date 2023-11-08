package org.monarchinitiative.hpo2robot.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotItem {
    private static Logger LOGGER = LoggerFactory.getLogger(RobotItem.class);

    private TermId newTermId;

    private final StringProperty newTermLabelProperty;

    private final StringProperty issueProperty;

    private final StringProperty parentTermLabelProperty;

    private final StringProperty newTermDefinitionProperty;

    private final StringProperty newTermCommentProperty;

    private final StringProperty pmidStringProperty;

    private final List<Term> parentTerms;

    private final List<String> pmids;

    private final List<Synonym> synonymList;

    private final Optional<String> gitHubIssueOpt;


    public RobotItem(TermId newHpoTermId,
                     String newTermLabel,
                     List<Term> parentTerms,
                     Set<Synonym> synonyms,
                     String definition,
                     String comment,
                     List<String> pmids,
                     String gitHubIssue) {
        this.newTermId = newHpoTermId;
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
        if (pmids.isEmpty()) {
            pmidStringProperty = new SimpleStringProperty("n/a");
        } else {
            pmidStringProperty = new SimpleStringProperty(String.join(";", pmids));
        }
        gitHubIssueOpt = Optional.ofNullable(gitHubIssue);
        synonymList = new ArrayList<>(synonyms);
    }

    /**
     * Use this constructor for terms that do not have an associated GitHub issue.
     * @param newHpoTermId The term ID for the new Term to be created
     * @param newTermLabel Name of the term
     * @param parentTerms one or more parent terms
     * @param definition The term definition
     * @param comment Comment - can be emptu
     * @param pmids Zero or multiple PubMed identifiers
     */
    public RobotItem(TermId newHpoTermId,
                     String newTermLabel,
                     List<Term> parentTerms,
                     Set<Synonym> synonyms,
                     String definition,
                     String comment,
                     List<String> pmids){
        this(newHpoTermId, newTermLabel, parentTerms, synonyms, definition, comment, pmids, null);
    }

    public static void exportRobotItems(ObservableList<RobotItem> items, File robotFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(robotFile))) {
            bw.write(header() + "\n");
            for (var item: items) {
                bw.write(item.getRow() + "\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        LOGGER.info("Wrote ROBOT file to {}", robotFile);
    }

    private String getRow() {
        List<String> rowItems = new ArrayList<>();
        rowItems.add(this.newTermId.getValue());
        rowItems.add(this.getNewTermLabel());
        String parents = parentTerms.stream()
                .map(Term::id)
                .map(TermId::getValue)
                .collect(Collectors.joining("|"));
        rowItems.add(parents);
        rowItems.add(getNewTermDefinition());
        String pmids = getPmids().stream().collect(Collectors.joining("|"));
        rowItems.add(pmids);
        rowItems.add(getNewTermComment());
        String synonyms = synonymList.stream().map(Synonym::toString).collect(Collectors.joining("|"));
        rowItems.add(synonyms);
        return String.join("\t", rowItems);
    }


    public static String header() {
        //HPO ID	Label	Superclass	Definition	Definition xref	Synonym	S xref	has_synonym_type	Narrow synonym	NS xref	has_synonym_type	Broad synonym	BS xref	has_synonym_type	Related synonym	RS xref	has_synonym_type	rdfs:comment	database_cross_reference	has_alternative_id
        List<String> headerItems = List.of("HPO_ID", "HPO_Label", "Superclass", "Definition", "Definition_PMID", "rdfs:comment", "Synonyms");
        return String.join("\t", headerItems);
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

    public String getParentTermDisplay() {
        String displayText = this.parentTerms
                .stream().map(Term::getName)
                .collect(Collectors.joining("; "));
        return displayText;

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

    public Optional<String> getGitHubIssueOpt() {
        return gitHubIssueOpt;
    }

    public boolean isValid() {
        return true; //TODO check
    }
}
