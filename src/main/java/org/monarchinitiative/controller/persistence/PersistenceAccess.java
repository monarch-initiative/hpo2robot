package org.monarchinitiative.controller.persistence;

import org.monarchinitiative.model.Options;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class PersistenceAccess {

    private final String HPO2ROBOT_DIRNAME = ".hpo2robot";
    private final String HPO2ROBOT_DIRPATH = System.getProperty("user.home") + File.separator + HPO2ROBOT_DIRNAME;

    private final String HPO2ROBOT_LOCATION = HPO2ROBOT_DIRPATH + File.separator + "hpo2robot_options.ser";

    public Optional<Options> loadFromPersistence(){
        File f = new File(HPO2ROBOT_LOCATION);
        if (! f.isFile()) {
            // The options file has not been created yet or there is
            // some other problem. Then the user will have to
            // create new options
            return Optional.empty();
        }
        try {
            FileInputStream fileInputStream
                    = new FileInputStream(HPO2ROBOT_LOCATION);
            ObjectInputStream objectInputStream
                    = new ObjectInputStream(fileInputStream);
            Options o = (Options) objectInputStream.readObject();
            objectInputStream.close();
            return Optional.of(o);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    /**
     * Create the hpo2robot directory if it does not exist already and
     * save the Options object there as a serialized object
     * Note that Files.createDirectories() creates a new directory and
     * parent directories that do not exist. This method does not throw an
     * exception if the directory already exists.
     */
    public void saveToPersistence(Options options) {
        try {
            Files.createDirectories(Paths.get(HPO2ROBOT_DIRPATH));
            FileOutputStream fileOutputStream
                    = new FileOutputStream(HPO2ROBOT_LOCATION);
            ObjectOutputStream objectOutputStream
                    = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(options);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
