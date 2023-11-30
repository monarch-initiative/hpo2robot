package org.monarchinitiative.hpo2robot.controller.services;

import org.monarchinitiative.phenol.ontology.data.TermId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class investigates the current hp-edit.owl file to extract all current HPO ids
 * using a regular expression (we are not using OWL-API to actually open the file as an ontology).
 * It then provides a list of identifiers that can be used to get the "next" HPO term when
 * we are creating the ROBOT template file.
 * @author Peter N Robinson
 */
public class HpoIdService {
    private final static Logger LOGGER = LoggerFactory.getLogger(HpoIdService.class);
    /** List of identifiers that we can use for new terms */
    private final List<TermId> availableHpoIdList;
    private final static Pattern hpIdPattern = Pattern.compile("http://purl.obolibrary.org/obo/HP_(\\d{7,7})");


    private final static Integer lowValue = 6_000_000;
    private final static Integer highValue = 6_500_000;

    public HpoIdService(Path hpoEditOwl) {
        Set<Integer> hpoIdSet = new HashSet<>();
        availableHpoIdList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(hpoEditOwl.toFile()))){
            String line;
            while ((line = br.readLine()) != null) {
               // System.out.println(line);
                Matcher matcher = hpIdPattern.matcher(line);
                if (matcher.find()) {
                    String hpoIdMatch = matcher.group(1);
                    Integer hpoId = Integer.parseInt(hpoIdMatch);
                    hpoIdSet.add(hpoId);
                }
            }

            for (int i = lowValue; i < highValue; ++i) {
                if (! hpoIdSet.contains(i)) {
                    TermId tid = integerToHpoId(i);
                    availableHpoIdList.add(tid);
                }
            }
            availableHpoIdList.sort(TermId::compareTo);
            LOGGER.info("Existing HPO ids: n={}.", hpoIdSet.size());
            LOGGER.info("Available HPO ids: n={}.", availableHpoIdList.size());
        } catch (IOException e) {
            LOGGER.error("Could not open hp-edit.owl: {}", e.getMessage());
        }
    }



    public TermId integerToHpoId(int identifier) {
        String s =  String.format("HP:%07d", identifier);
        return TermId.of(s);
    }

    public List<TermId> getAvailableHpoIdList() {
        return availableHpoIdList;
    }
}
