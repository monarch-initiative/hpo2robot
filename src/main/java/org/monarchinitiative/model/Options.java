package org.monarchinitiative.model;

import java.io.Serializable;

/**
 * Stores the current HPO file, ROBOT file, and ORCID identifier of the curator
 * between HPO2ROBOT sessions.
 */
public class Options implements Serializable {

    private static String N_A = "";

    private String hpJsonFile;

    private String robotFile;

    private String orcid;

    public Options(String hpJsonFile, String robotFile, String orcid) {
        this.hpJsonFile = hpJsonFile;
        this.robotFile = robotFile;
        this.orcid = orcid;
    }

    public Options(){
        this.hpJsonFile = N_A;
        this.robotFile = N_A;
        this.orcid = N_A;
    }

    public String getHpJsonFile() {
        return hpJsonFile;
    }

    public void setHpJsonFile(String hpJsonFile) {
        this.hpJsonFile = hpJsonFile;
    }

    public String getRobotFile() {
        return robotFile;
    }

    public void setRobotFile(String robotFile) {
        this.robotFile = robotFile;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}
