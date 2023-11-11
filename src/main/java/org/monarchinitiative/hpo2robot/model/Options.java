package org.monarchinitiative.hpo2robot.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    private final ObjectProperty<File> hpJsonFile = new SimpleObjectProperty<>();

    private final ObjectProperty<File> hpEditOwlFile = new SimpleObjectProperty<>();

    private final StringProperty orcid = new SimpleStringProperty();

    private final BooleanBinding isReady;

    public Options(String hpJsonFile, String orcid, String hpEdit) {
        this();
        this.hpJsonFile.set(new File(hpJsonFile));
        this.hpEditOwlFile.set(new File(hpEdit));
        this.orcid.set(orcid);
    }

    public Options(){
        isReady = hpJsonFile.isNotNull().and(hpEditOwlFile.isNotNull()).and(orcid.isNotEmpty());
    }

    public File getHpJsonFile() {
        return hpJsonFile.get();
    }

    public void setHpJsonFile(File hpJsonFile) {
        this.hpJsonFile.set(hpJsonFile);
    }

    public ObjectProperty<File> hpJsonFileProperty() {
        return hpJsonFile;
    }

    public File getHpEditOwlFile() {
        return hpEditOwlFile.get();
    }

    public void setHpEditOwlFile(File hpEditOwlFile) {
        this.hpEditOwlFile.set(hpEditOwlFile);
    }

    public String getOrcid() {
        return orcid.get();
    }

    public void setOrcid(String orcid) {
        this.orcid.set(orcid);
    }

    public BooleanBinding isReadyProperty() {
        return isReady;
    }

    public boolean isValid() {
        if (hpJsonFile.get() == null) {
            return false;
        }
        if (! hpJsonFile.get().isFile()) {
            return false;
        }
        if (hpEditOwlFile.get() == null) {
            return false;
        }
        // the last thing to check is if the ORCID matches
        String o = orcid.get();
        if (o == null)
            return false;
        final Matcher matcher = ORCID_PATTERN.matcher(o);
        return  matcher.matches();
    }

    @Override
    public String toString() {
        return String.format("""
                                HPO: %s
                                hp-edit: %s
                                biocurator: %s
                                valid: %s""",
                hpJsonFile.get(), hpEditOwlFile.get(), orcid, isValid());
    }

    public String getErrorMessage() {
        StringBuilder sb = new StringBuilder();
        File hp = hpJsonFile.get();
        if (hp == null) {
            sb.append("hp.json not set. ");
        } else if (!hp.isFile()) {
            sb.append("could not find hp.json at ").append(hp);
        }
        File howl = hpEditOwlFile.get();
        if (howl == null) {
            sb.append("hp-edit.owl not set. ");
        }
        final Matcher matcher = ORCID_PATTERN.matcher(orcid.get());
        boolean ORCID_OK = matcher.matches();
        if (! ORCID_OK) {
            sb.append(String.format("Malformed ORCID: \"%s\". ", orcid.get()));
        }
        return sb.toString();
    }
}
