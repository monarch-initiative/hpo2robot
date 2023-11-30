package org.monarchinitiative.hpo2robot.controller.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Optional;


public class RobotRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotRunner.class);

    private static final String COMMAND = "sh run.sh make MERGE_TEMPLATE_FILE=tmp/hpo2robot.tsv";

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

    public void run() {
        StringBuilder sb = new StringBuilder();
        ProcessBuilder pb = new ProcessBuilder("sh", "run.sh", "make",
                "MERGE_TEMPLATE_FILE=tmp/hpo2robot.tsv", "merge_template");
        pb = new ProcessBuilder("touch", "TEST.txt");
        pb.directory(hpoFolder);
        String myCommand = String.join(" ",pb.command());
        LOGGER.info("Running ROBOT command {} in directory {}", myCommand, pb.directory().getAbsolutePath());
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

            String msg = String.format("ROBOT Command %s - PID: %d; exit code: %d.\n", myCommand, processPid, exitCode);
            LOGGER.info(msg);
            gobbledText = String.format("%s%s", msg, sb.toString());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            exitCode = null;
        }
    }

    public Optional<Integer> getExitCode() {
        return Optional.ofNullable(exitCode);
    }


    /**
     *

     Value 127 is returned by /bin/sh when the given command is not found within your PATH system variable
     * @return the text returned by the process running.
     */
    public String getGobbledText() {
        if (exitCode == 127) {
            return "ROBOT command not found in PATH";
        }
        return gobbledText;
    }
}
