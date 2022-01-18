package org.catalpacourt.openapi.commandlinerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CommandLineRunner {
    private final boolean isDryRun;
    private final File location;
    private Map<String, String> environment = null;

    public CommandLineRunner(boolean isDryRun, File location) {
        this.isDryRun = isDryRun;
        if (!isDryRun) {
            environment = System.getenv();
        }
        this.location = location;
    }

    public CommandLineRunner(boolean isDryRun) {
        this(isDryRun, new File(System.getProperty("user.dir")));
    }

    public Result run(String command) {
        System.out.print("$");
        System.out.println(command);
        if (isDryRun) {
            return new Result(0, "");
        }
        ProcessBuilder processBuilder = new ProcessBuilder().directory(location);
        command = "/bin/bash -c " + command;
        processBuilder.command(command.split(" "));

        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            List<String> outputLines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                outputLines.add(line);
            }
            output.append(String.join("\n", outputLines));

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(output);
            } else {
                //abnormal...
            }

            return new Result(exitVal, output.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Result(1, "");
    }

    public Result run(Iterator<String> commands) {
        Result result = new Result(0, "");
        while (commands.hasNext() && result.getExitCode() == 0) {
            Result newResult = run(commands.next());
            result = new Result(newResult.getExitCode(), result.getOutput() + newResult.getOutput());
        }
        return result;
    }
}
