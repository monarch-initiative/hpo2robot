package org.monarchinitiative.hpo2robot.model;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvailableHpoIdsTest {


    private static final List<TermId> termIdList = List.of(TermId.of("HP:0000001"),
            TermId.of("HP:0000002"),
            TermId.of("HP:0000003"),
            TermId.of("HP:0000004"),
            TermId.of("HP:0000005"));

    private static final AvailableHpoIds availableHpoIds = new AvailableHpoIds(termIdList);


    @Test
    public void testAvailableHpoIdCount() {
        AvailableHpoIds availableHpoIds = new AvailableHpoIds(termIdList);
        assertEquals(5, availableHpoIds.availableHpoIdCount());
    }


    @Test
    public void testFirstAndSecond() {
        AvailableHpoIds availableHpoIds = new AvailableHpoIds(termIdList);
        assertEquals(5, availableHpoIds.availableHpoIdCount());
        Optional<TermId> opt = availableHpoIds.getNextAvailableId();
        assertTrue(opt.isPresent());
        TermId tid1 = opt.get();
        assertEquals("HP:0000001", tid1.getValue());
        assertEquals(4, availableHpoIds.availableHpoIdCount());
        Optional<TermId> opt2 = availableHpoIds.getNextAvailableId();
        assertTrue(opt2.isPresent());
        TermId tid2 = opt2.get();
        assertEquals("HP:0000002", tid2.getValue());
        assertEquals(3, availableHpoIds.availableHpoIdCount());

    }




}
