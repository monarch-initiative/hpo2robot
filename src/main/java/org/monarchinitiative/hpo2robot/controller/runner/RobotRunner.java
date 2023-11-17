package org.monarchinitiative.hpo2robot.controller.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class RobotRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RobotRunner.class);

    private static final String COMMAND_TEMPLATE = "cd %s; sh run.sh make MERGE_TEMPLATE_FILE=%s merge_template";

    private final String command;

    String gobbledText;

    int exitCode;

    public RobotRunner(String robotTemplateFilePath, File hpoSrcOntologyFolder) {
        command = String.format(COMMAND_TEMPLATE, hpoSrcOntologyFolder.getAbsolutePath(), robotTemplateFilePath);
    }


    public String getCommandString() {
        return command;
    }

    public void run() {
        Process process;
        ExecutorService executorService =
                new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>());
        try {
            process = Runtime.getRuntime()
                    .exec(getCommandString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            List<String> l = new ArrayList<>();
            Consumer<String> c1 = s -> l.add(s);
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), c1);
            Future<?> future = executorService.submit(streamGobbler);
            exitCode = process.waitFor();
            future.get(10, TimeUnit.SECONDS);
            gobbledText = String.join("\n", l);
        } catch (Exception e) {
            LOGGER.error("Could not run ROBOT Command {}", getCommandString());
            LOGGER.error("Because of error: {}", e.getMessage());
            exitCode =  -1;
        }
        LOGGER.info("ROBOT Command exit code = {}", exitCode);
    }


    /**
     *

     Value 127 is returned by /bin/sh when the given command is not found within your PATH system variable
     * @return
     */
    public String getGobbledText() {
        if (exitCode == 127) {
            return "ROBOT command not found in PATH";
        }
        if (exitCode == 0) {
            return "success";
        }
        return String.format("%d: %s", exitCode, gobbledText);
    }
}
