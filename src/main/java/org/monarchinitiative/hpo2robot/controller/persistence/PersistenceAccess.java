package org.monarchinitiative.hpo2robot.controller.persistence;

import org.monarchinitiative.hpo2robot.model.Options;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PersistenceAccess {

    private static final String HPO2ROBOT_DIRNAME = ".hpo2robot";
    private static final String HPO2ROBOT_DIRPATH = System.getProperty("user.home") + File.separator + HPO2ROBOT_DIRNAME;
    private static final String HP_JSON_FILE = "hp.json.file";
    private static final String USER_ORCID = "user.orcid";

    private static final String HPO_SRC_FOLDER = "hpo_src_ontology";

    private static final String HPO2ROBOT_LOCATION = HPO2ROBOT_DIRPATH + File.separator + "hpo2robot_options.properties";

    private PersistenceAccess() {
    }

    public static Options loadFromPersistence(){
        Options options = new Options();
        File f = new File(HPO2ROBOT_LOCATION);
        if (! f.isFile()) {
            // The options file has not been created yet or there is
            // some other problem. Then the user will have to
            // create new options
            return options;
        }
        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(HPO2ROBOT_LOCATION)) {
            properties.load(is);
            if (properties.containsKey(HP_JSON_FILE))
                options.setHpJsonFile(new File(properties.getProperty(HP_JSON_FILE)));
            if (properties.containsKey(HPO_SRC_FOLDER))
                options.setHpSrcOntologyDir(new File(properties.getProperty(HPO_SRC_FOLDER)));
            if (properties.containsKey(USER_ORCID))
                options.setOrcid(properties.getProperty(USER_ORCID));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return options;
    }


    /**
     * Create the hpo2robot directory if it does not exist already and
     * save the Options object there as a serialized object
     * Note that Files.createDirectories() creates a new directory and
     * parent directories that do not exist. This method does not throw an
     * exception if the directory already exists.
     */
    public static void saveToPersistence(Options options) {
        Properties properties = new Properties();

        File hpJsonFile = options.getHpJsonFile();
        if (hpJsonFile != null)
            properties.setProperty(HP_JSON_FILE, hpJsonFile.getAbsolutePath());

        File hpSrcOntologyDirectory = options.getHpSrcOntologyDir();
        if (hpSrcOntologyDirectory != null)
            properties.setProperty(HPO_SRC_FOLDER, hpSrcOntologyDirectory.getAbsolutePath());

        properties.setProperty(USER_ORCID, options.getOrcid());

        try {
            Files.createDirectories(Paths.get(HPO2ROBOT_DIRPATH));
            try (FileOutputStream os = new FileOutputStream(HPO2ROBOT_LOCATION)) {
                properties.store(os, "hpo2robot");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
