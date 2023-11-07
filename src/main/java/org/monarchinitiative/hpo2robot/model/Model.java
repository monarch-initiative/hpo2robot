package org.monarchinitiative.hpo2robot.model;

import org.monarchinitiative.hpo2robot.controller.PopUps;
import org.monarchinitiative.hpo2robot.controller.services.HpoIdService;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Model {

    private Options options = null;

    List<TermId> availableHpoIds = null;

    public Set<TermId> usedHpoTermIdSet;

    private HpoIdService hpoIdService;

    public Model(){
        usedHpoTermIdSet = new HashSet<>();
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


    public TermId getNextHpoId() {
        TermId nextId = availableHpoIds.getFirst();
        availableHpoIds.removeFirst();
        usedHpoTermIdSet.add(nextId);
        return nextId;
    }





}
