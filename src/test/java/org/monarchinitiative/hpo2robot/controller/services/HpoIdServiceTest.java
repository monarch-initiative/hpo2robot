package org.monarchinitiative.hpo2robot.controller.services;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.phenol.ontology.data.TermId;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Testing a private function from the HpoIdService class for convenience here.
 */
public class HpoIdServiceTest {


    public TermId integerToHpoIt(int identifier) {
        String s =  String.format("HP:%07d", identifier);
        return TermId.of(s);
    }


    @Test
    public void testValid() {
        int id = 42;
        TermId tid = integerToHpoIt(id);
        assertEquals("HP", tid.getPrefix());
        assertEquals("0000042", tid.getId());
        assertEquals("HP:0000042", tid.getValue());
    }

    @Test
    public void testRange() {
        HpoIdService service = new HpoIdService(Path.of("J"));
    }



}
