package org.monarchinitiative.hpo2robot.github;

import org.junit.jupiter.api.Test;
import org.monarchinitiative.hpo2robot.controller.services.HpoIdService;

import java.nio.file.Paths;

public class GitHubIssueRetrieverTest {

    @Test
    public void testit() {
        String path = "/Users/robinp/GIT/human-phenotype-ontology/src/ontology/hp-edit.owl";
        HpoIdService service = new HpoIdService(Paths.get(path));



    }

}
