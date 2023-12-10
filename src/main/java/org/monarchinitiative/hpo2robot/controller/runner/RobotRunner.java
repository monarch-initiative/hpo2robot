package org.monarchinitiative.hpo2robot.controller.runner;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.monarchinitiative.hpo2robot.controller.widgets.ProcessRunnerWidget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;


public class RobotRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotRunner.class);

    private static final String COMMAND = "sh run.sh make MERGE_TEMPLATE_FILE=tmp/robot2hpo-merge.tsv merge_template";

    String gobbledText;

    private final File hpoFolder;

    Integer exitCode;

    public RobotRunner(File hpoSrcOntologyFolder) {
       hpoFolder = hpoSrcOntologyFolder;
       exitCode = null;
    }


    public String getCommandString() {
        return COMMAND;
    }

    public String runRobot() {
        String [] commandParts = {"sh", "run.sh", "make",
                "MERGE_TEMPLATE_FILE=tmp/hpo2robot.tsv", "merge_template"};
        String []  commandPartsTMP = {"touch", "TEST.txt"};
        return runProcess(commandParts);
    }

    public String runQc() {
        String [] commandParts = {"sh", "hp-qc-pipeline.sh"};
        String []  commandPartsTMP = {"touch", "TEST-QC.txt"};
        return runProcess(commandParts);
    }

    public Optional<Integer> getExitCode() {
        return Optional.ofNullable(exitCode);
    }


    private String runProcess(String [] commandParts) {
        final StringProperty myCommand = new SimpleStringProperty("");
        Service<String> service = new Service<>() {
            @Override
            protected Task<String> createTask() {
                    return new Task<>() {
                        @Override
                        protected String call() throws Exception {
                            StringBuilder sb = new StringBuilder();
                            ProcessBuilder pb = new ProcessBuilder(commandParts);
                            pb.directory(hpoFolder);
                            myCommand.set(String.join(" ",pb.command()));
                            LOGGER.info("Running ROBOT command {} in directory {}", myCommand.get(), pb.directory().getAbsolutePath());
                            try {
                                Process p = pb.start();
                                BufferedReader read = new BufferedReader(new InputStreamReader(
                                        p.getInputStream()));
                                try {
                                    p.waitFor();
                                } catch (InterruptedException e) {
                                    LOGGER.error(e.getMessage());
                                }
                                while (read.ready()) {
                                    sb.append(read.readLine());
                                }
                                exitCode = p.exitValue();
                                long processPid = p.pid();
                                String msg = String.format("ROBOT Command %s - PID: %d; exit code: %d.\n", myCommand.get(), processPid, exitCode);
                                LOGGER.info(msg);
                                return String.format("%s%s", msg, sb);
                            } catch (IOException e) {
                                LOGGER.error(e.getMessage());
                                exitCode = null;
                                return String.format("Could not run process with %s: %s.", myCommand.get(), e.getMessage());
                            }
                        }
                    };
                }
            };
        ProcessRunnerWidget widget = new ProcessRunnerWidget(service, myCommand.get());
        widget.showAndWait();
        return service.valueProperty().toString();
    }

    public void clearRobotFile() {
        String FILE_PATH = hpoFolder.getAbsolutePath() + File.separator + "tmp/hpo2robot.tsv";
        try {
            new PrintWriter(FILE_PATH).close();
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not delete contents of ROBOTG file: {}", e.getMessage());
        }
        LOGGER.info("Cleared ROBOT file at {}", FILE_PATH);
    }
}
