package org.monarchinitiative.hpo2robot.model;

import org.monarchinitiative.hpo2robot.controller.PopUps;
import org.monarchinitiative.hpo2robot.controller.services.HpoIdService;
import org.monarchinitiative.phenol.ontology.data.Term;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.*;


/**
 * This class keeps a record of items that we can use to make the next ROBOT item.
 * Note that for now we keep the list of approvaed ROBOT items in the MainController.
 * @author Peter N Robinson
 */
public class Model {

    private Options options = null;

    List<TermId> availableHpoIds = null;

    public Set<TermId> usedHpoTermIdSet;

    private String hpoTermLabel;

    private List<Term> parentTermList;

    private HpoIdService hpoIdService;

    private Set<Synonym> synonymSet;
    private String definition ;

    private String comment ;

    private String orcid;

    private List<String> pmidList;

    private String gitHubIssue;

    public Model(){
        reset();
    }


    public void reset() {
        definition = null;
        comment = null;
        orcid = null;
        gitHubIssue = null;
        usedHpoTermIdSet = new HashSet<>();
        synonymSet = new HashSet<>();
        pmidList = new ArrayList<>();
        parentTermList = new ArrayList<>();
    }


    public void setOptions(Options options) {
        this.options = options;
        String hpoEditOwl = options.getHpEditOwlFile();
        if (hpoEditOwl == null) {
            PopUps.alertDialog("Error", "Attempt to set up HPO ID service with invalid path");
        }
        HpoIdService hpoIdService = new HpoIdService(Path.of(hpoEditOwl));
        availableHpoIds = hpoIdService.getAvailableHpoIdList();
    }


    private TermId getNextAvailableHpoId() {
        TermId nextId = availableHpoIds.getFirst();
        availableHpoIds.removeFirst();
        if (usedHpoTermIdSet.contains(nextId)) {
            // SHOULD NEVER HAPPEN BUT IF IT DOES THERE IS NO TOMORROW
            System.err.println("[FATAL] Attempt to reuse HPO id " + nextId.getValue());
            System.exit(1);
        }
        usedHpoTermIdSet.add(nextId);
        return nextId;
    }


    public void setDefinition(String def) {
        this.definition = def;
    }

    public void setOrcid(String orc) {
        this.orcid = orc;
    }

    public void setGitHubIssue(String gitHubIssue) {
        this.gitHubIssue = gitHubIssue;
    }


    public void addSynonym(Synonym synonym) {
        synonymSet.add(synonym);
    }

    public void setPmidList(List<String> pmids) {
        this.pmidList.addAll(pmids);
    }

    public void setHpoTermLabel(String hpoTermLabel) {
        this.hpoTermLabel = hpoTermLabel;
    }

    public Optional<RobotItem> getRobotItemOpt() {
        RobotItem item;
        TermId hpoTermId = getNextAvailableHpoId();
        String orcidId = orcid == null ? options.getOrcid() : orcid;
        if (gitHubIssue != null) {
             item = new RobotItem(hpoTermId,
                    hpoTermLabel,
                    parentTermList,
                    synonymSet,
                    definition,
                     comment,
                    pmidList,
                     orcidId,
                     gitHubIssue);
        } else {
            item = new RobotItem(hpoTermId,
                    hpoTermLabel,
                    parentTermList,
                    synonymSet,
                    definition,
                    comment,
                    pmidList,
                    orcidId);
        }
        if (item.isValid()) {
            return Optional.of(item);
        } else {
            return Optional.empty();
        }
    }


    public void setparentTerms(List<Term> parentTerms) {
        this.parentTermList.clear();
        this.parentTermList.addAll(parentTerms);
    }


    public void setComment(String userText) {
        this.comment = userText;
    }
}
