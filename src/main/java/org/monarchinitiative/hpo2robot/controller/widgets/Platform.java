package org.monarchinitiative.hpo2robot.controller.widgets;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.monarchinitiative.hpo2robot.controller.PopUps;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;

public class Platform {

    /**
     * Get path to directory where HRMD-gui stores global settings.
     * The path depends on underlying operating system. Linux, Windows & OSX
     * currently supported.
     * @return File to directory
     */
    public static File getHpo2RobotDir() {
        CurrentPlatform platform = figureOutPlatform();
        File linuxPath = new File(System.getProperty("user.home") + File.separator + ".hpo2robot");
        File windowsPath = new File(System.getProperty("user.home") + File.separator + "hpo2robot");
        File osxPath = new File(System.getProperty("user.home") + File.separator + ".hpo2robot");

        switch (platform) {
            case LINUX -> {
                createDirIfNotExists(linuxPath);
                return linuxPath;
            }
            case WINDOWS -> {
                createDirIfNotExists(windowsPath);
                return windowsPath;
            }
            case OSX -> {
                createDirIfNotExists(osxPath);
                return osxPath;
            }
            case UNKNOWN -> {
                return null;
            }
            default -> {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("Find GUI config dir");
                a.setHeaderText(null);
                a.setContentText(String.format("Unrecognized platform: \"%s\"", platform));
                a.showAndWait();
                return null;
            }
        }
    }


    public static File getHpo2RobotOptionsFile() {
        File dir = getHpo2RobotDir();
        File file = new File(dir + File.separator + "hpo2robot.txt");
        if (! file.isFile()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("! hpo2robot.txt options file\n");
            } catch (IOException e) {
                PopUps.showErrorMessage("Could not initialized hpo2robot.txt file");
            }
        }
        return file;
    }

    public static File getSkippedIssueFile() {
        File dir = getHpo2RobotDir();
        File file = new File(dir + File.separator + "hpo2robot-skipped-issues.txt");
        if (! file.isFile()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("! hpo2robot.txt skipped issue file\n");
            } catch (IOException e) {
                PopUps.showErrorMessage("Could not initialized skipped issues file");
            }
        }
        return file;
    }

    private static void createDirIfNotExists(File dirPath) {

        try {
            if (!dirPath.isDirectory()) {
                Files.createDirectories(Paths.get(dirPath.toURI()));
            }
        } catch (IOException e) {
            Alert a = new Alert(AlertType.ERROR);
            a.setTitle("Error creating hpo2robot directory");
            a.setHeaderText(null);
            a.setContentText(String.format("Could not create directory: \"%s\"", e.getMessage()));
            a.showAndWait();
        }
    }

    /**
     * Get the absolute path to the log file.
     * @return the absolute path,e.g., /home/user/.hpo2robot/hpo2robot.log
     */
    public static String getAbsoluteLogPath() {
        File dir = getHpo2RobotDir();
        return dir + File.separator +  "hpo2robot.log";
    }


    /* Based on this post: http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/ */
    private static CurrentPlatform figureOutPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return CurrentPlatform.LINUX;
        } else if (osName.contains("win")) {
            return CurrentPlatform.WINDOWS;
        } else if (osName.contains("mac")) {
            return CurrentPlatform.OSX;
        } else {
            return CurrentPlatform.UNKNOWN;
        }
    }


    private enum CurrentPlatform {
        LINUX("Linux"),
        WINDOWS("Windows"),
        OSX("Mac OSX"),
        UNKNOWN("Unknown");
        private final String name;

        CurrentPlatform(String n) {this.name = n; }

        @Override
        public String toString() { return this.name; }
    }



}
