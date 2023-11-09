package org.monarchinitiative.hpo2robot.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.monarchinitiative.phenol.ontology.data.SynonymType;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotItem.class);


    private static List<String> ROBOT_HEADER_ITEMS = List.of("HPO_ID",
            "HPO_Label",
            "Superclass",
            "Definition",
            "Definition_PMID",
            "rdfs:comment",
            "creator orcid",
            "github",
            "Synonyms",
            "synonym orcid",
            "synonym pmid",
            "synonym types");

    private static String ROBOT_HEADER_LINE = String.join("\t", ROBOT_HEADER_ITEMS);

    private static List<String> ROBOT_TEMPLATE_ITEMS = List.of("ID",
            "LABEL",
            "SC % SPLIT=|",
            "A IAO:0000115",
            ">A oboInOwl:hasDbXref SPLIT=|",
            "A rdfs:comment",
            "AI dc:contributor SPLIT=|",
            "AI IAO:0000233",
            "A oboInOwl:hasExactSynonym SPLIT=|",
            ">AI oboInOwl:hasDbXref SPLIT=|",
            ">A oboInOwl:hasDbXref SPLIT=|",
            ">AI oboInOwl:hasSynonymType");

    private static String EMPTY_CELL = "";

    private static String ROBOT_TEMPLATE_LINE = String.join("\t", ROBOT_TEMPLATE_ITEMS);

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

    private final String orcidId;


    public RobotItem(TermId newHpoTermId,
                     String newTermLabel,
                     List<Term> parentTerms,
                     Set<Synonym> synonyms,
                     String definition,
                     String comment,
                     List<String> pmids,
                     String orcid,
                     String gitHubIssue) {
        this.newTermId = newHpoTermId;
        this.newTermLabelProperty = new SimpleStringProperty(newTermLabel);
        this.newTermDefinitionProperty = new SimpleStringProperty(definition);
        this.newTermCommentProperty = new SimpleStringProperty(comment);
        this.parentTerms = parentTerms;
        issueProperty = new SimpleStringProperty("42");
        this.orcidId = orcid;
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
                     List<String> pmids,
                     String orcid){
        this(newHpoTermId, newTermLabel, parentTerms, synonyms, definition, comment, pmids,orcid, null);
    }

    public static void exportRobotItems(ObservableList<RobotItem> items, File robotFile) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(robotFile))) {
            bw.write(header() + "\n");
            for (var item: items) {
                item.getRows().forEach(r -> {
                    try {
                        bw.write(r + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            }
        } catch (IOException ioe) {
            LOGGER.error("Could not write ROBOT row: {}", ioe.getMessage());
        }
        LOGGER.info("Wrote ROBOT file to {}", robotFile);
    }


    /**
     * "HPO_ID",
     *             "HPO_Label",
     *             "Superclass",
     *             "Definition",
     *             "Definition_PMID",
     *             "rdfs:comment",
     *             "creator orcid",
     *             "github",
     *             "Synonyms",
     *             "synonym orcid",
     *             "synonym pmid",
     *             "synonym types"
     * @return
     */
    private List<String> getRows() {
        List<String> rows = new ArrayList<>();
        if (synonymList.isEmpty()) {
            List<String> rowItems = new ArrayList<>();
            rowItems.add(this.newTermId.getValue());
            rowItems.add(this.getNewTermLabel());
            String parents = parentTerms.stream()
                    .map(Term::id)
                    .map(TermId::getValue)
                    .collect(Collectors.joining("|"));
            rowItems.add(parents);
            rowItems.add(getNewTermDefinition());
            String pmids = String.join("|", getPmids());
            rowItems.add(pmids);
            rowItems.add(getNewTermComment());
            rowItems.add(orcidId);
            rowItems.add(gitHubIssueOpt.orElse(EMPTY_CELL));
            rowItems.add(EMPTY_CELL); // synonym
            rowItems.add(EMPTY_CELL); // synonym type
            rowItems.add(EMPTY_CELL); // synonym PMID
            rowItems.add(EMPTY_CELL); // synonym orcid
            rows.add(String.join("\t", rowItems));
        } else  {
            Synonym synonym = synonymList.get(0);
            List<String> rows = new ArrayList<>();
            List<String> rowItems = new ArrayList<>();
            rowItems.add(this.newTermId.getValue());
            rowItems.add(this.getNewTermLabel());
            String parents = parentTerms.stream()
                    .map(Term::id)
                    .map(TermId::getValue)
                    .collect(Collectors.joining("|"));
            rowItems.add(parents);
            rowItems.add(getNewTermDefinition());
            String pmids = String.join("|", getPmids());
            rowItems.add(pmids);
            rowItems.add(getNewTermComment());
            rowItems.add(orcidId);
            rowItems.add(gitHubIssueOpt.orElse(EMPTY_CELL));
          //  String synonyms = synonymList.stream().map(Synonym::label).collect(Collectors.joining("|"));
            rowItems.add(synonym.label());
            if (synonym.synonymType() == SynonymType.NONE) {
                rowItems.add(synonym.synonymType().name());
            } else {
                rowItems.add(EMPTY_CELL);
            }
            rowItems.add(EMPTY_CELL); // synonym PMID
            rowItems.add(EMPTY_CELL); // synonym orcid
            rows.add(String.join("\t", rowItems));
            if  (synonymList.size() > 1) {

            }


        return rows;
    }




    public static String header() {
        //HPO ID	Label	Superclass	Definition	Definition xref	Synonym	S xref	has_synonym_type	Narrow synonym	NS xref	has_synonym_type	Broad synonym	BS xref	has_synonym_type	Related synonym	RS xref	has_synonym_type	rdfs:comment	database_cross_reference	has_alternative_id
        List<String> headerItems = List.of("HPO_ID", "HPO_Label", "Superclass", "Definition", "Definition_PMID", "rdfs:comment", "Synonyms", "ORCID");
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
        return this.parentTerms
                .stream().map(Term::getName)
                .collect(Collectors.joining("; "));

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

    public List<Synonym> getSynonyms() {
        return synonymList;
    }
}
