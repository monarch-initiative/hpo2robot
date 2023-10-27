package org.monarchinitiative.model;

import java.io.File;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the current HPO file, ROBOT file, and ORCID identifier of the curator
 * between HPO2ROBOT sessions.
 */
public class Options implements Serializable {

    /** Regular expression to check whether an input string is a valid ORCID id. (just the 16 digits) */
    private static final String ORCID_REGEX =
            "^[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}$";

    /** Regular expression to check whether an input string is a valid ORCID id. */
    private static final Pattern ORCID_PATTERN = Pattern.compile(ORCID_REGEX);

    private static final String N_A = "";

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


    public boolean isValid() {
        if (hpJsonFile.equals(N_A)) {
            return false;
        }
        File f = new File(hpJsonFile);
        if (! f.isFile()) {
            return false;
        }
        if (robotFile.equals(N_A)) {
            return false;
        }
        /*f = new File(robotFile);
        if (! f.isFile()) {
            return false;
        }*/
        // the last thing to check is if the ORCID matches
        final Matcher matcher = ORCID_PATTERN.matcher(orcid);
        return  matcher.matches();
    }

    @Override
    public String toString() {
        return String.format("""
                                HPO: %s
                                ROBOT: %s
                                biocurator: %s
                                valid: %s""",
                this.hpJsonFile, this.robotFile, orcid, isValid());
    }

    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        File f = new File(hpJsonFile);
        if (f.isFile()) {
           // no op
        } else if (hpJsonFile.equals(N_A)) {
            sb.append("hp.json not set. ");
        } else {
            sb.append("could not find hp.json at ").append(hpJsonFile);
        }
        if (robotFile.equals(N_A)) {
            sb.append("ROBOT file not set. ");
        }
        final Matcher matcher = ORCID_PATTERN.matcher(orcid);
        boolean ORCID_OK = matcher.matches();
        if (! ORCID_OK) {
            sb.append(String.format("Malformed ORCID: \"%s\". ", orcid));
        }
        return sb.toString();
    }
}
