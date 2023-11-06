package org.monarchinitiative.hpo2robot.controller.services;

import org.monarchinitiative.phenol.ontology.data.TermId;

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
    /** List of identifiers that we can use for new terms */
    private final List<TermId> availableHpoIdList;
    private final Pattern hpIdPattern = Pattern.compile("http://purl.obolibrary.org/obo/HP_(\\d{7,7})");


    private final Integer lowValue = 5_000_000;
    private final Integer highValue = 5_500_000;

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

            for (int i = this.lowValue; i < highValue; ++i) {
                if (hpoIdSet.contains(i)) {
                    continue;
                } else {
                    TermId tid = integerToHpoIt(i);
                    availableHpoIdList.add(tid);
                }
            }
            availableHpoIdList.sort(TermId::compareTo);
            for (var i : availableHpoIdList) {
                System.out.println(i);
            }
            System.out.printf("Existing HPO ids: n=%d.\n", hpoIdSet.size());
            System.out.printf("Available HPO ids: n=%d.\n", availableHpoIdList.size());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public TermId integerToHpoIt(int identifier) {
        String s =  String.format("HP:%07d", identifier);
        return TermId.of(s);
    }


}
