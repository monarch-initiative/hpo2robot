package org.monarchinitiative.hpo2robot.model;

import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class AvailableHpoIds {

    private final List<TermId> availableHpoIds;

    private final Set<TermId> assignedHpoIds;

    public AvailableHpoIds(List<TermId> availableHpoIdList) {
        this.availableHpoIds = List.copyOf(availableHpoIdList);
        this.assignedHpoIds = new HashSet<>();
    }

    public Optional<TermId> getNextAvailableId() {
        Optional<TermId> opt = availableHpoIds.stream().filter(Predicate.not(assignedHpoIds::contains)).findFirst();
        opt.ifPresent(assignedHpoIds::add);
        return opt;
    }


    public Object availableHpoIdCount() {
        return availableHpoIds.size() - assignedHpoIds.size();
    }
}
