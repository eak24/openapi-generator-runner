package org.catalpacourt.openapi.commandlinerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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

    public List<ProcessBuilder> handleRedirections(ProcessBuilder processBuilder) {
        return Collections.singletonList(processBuilder);
//        List<ProcessBuilder> chain = new ArrayList<>();
//        int lastSplit = 0;
//        int n = processBuilder.command().size();
//        for (int i = 0; i < n; i++) {
//            String command = processBuilder.command().get(i);
//            if (Objects.equals(command, "|")) {
//                ProcessBuilder p = new ProcessBuilder();
//                p.directory(processBuilder.directory());
//                p.command(processBuilder.command().subList(lastSplit, i));
//                if (!chain.isEmpty()) {
//
//                    chain.get(chain.size() - 1).redirectOutput(p.redirectInput());
//                }
//                chain.add(p);
//                lastSplit = i + 1;
//            }
//        }
//        if (chain.isEmpty()) {
//            return Collections.singletonList(processBuilder);
//        } else if (lastSplit < n) {
//            ProcessBuilder p = new ProcessBuilder();
//            p.directory(processBuilder.directory());
//            p.command(processBuilder.command().subList(lastSplit, n));
//            chain.add(p);
//        }
//        return chain;
    }

    public Result run(ProcessBuilder processBuilder) {

        List<ProcessBuilder> processBuilders = handleRedirections(processBuilder);
        if (processBuilders.size() == 1) {
            print(processBuilder);
        } else {
            processBuilders.forEach(this::run);
        }

        if (isDryRun) {
            return new Result(0, "");
        }

        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            List<String> outputLines = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                outputLines.add(line);
            }
            output.append(String.join("\n", outputLines));
            int exitVal = process.waitFor();
            if (exitVal != 0) {
                BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    System.out.println(line);
                    outputLines.add(line);
                }
            }
            return new Result(exitVal, output.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Result(1, "");
    }

    private void print(ProcessBuilder processBuilder) {
        if (!isDryRun) {
            System.out.print("$");
        }
        System.out.println(String.join(" ", processBuilder.command()));
    }

    public Result run(Iterator<ProcessBuilder> processBuilders) {
        Result result = new Result(0, "");
        while (processBuilders.hasNext() && result.getExitCode() == 0) {
            Result newResult = run(processBuilders.next());
            result = new Result(newResult.getExitCode(), result.getOutput() + newResult.getOutput());
        }
        return result;
    }
}
