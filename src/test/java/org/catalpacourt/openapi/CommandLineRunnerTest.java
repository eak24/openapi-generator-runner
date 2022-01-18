package org.catalpacourt.openapi;

import org.catalpacourt.openapi.commandlinerunner.CommandLineRunner;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CommandLineRunnerTest {

    @Test
    public void shouldRunBasicCommands() {
        CommandLineRunner commandLineRunner = new CommandLineRunner(false);
        assertEquals("hi", commandLineRunner.run("echo hi").getOutput());
        List<String> outputPath = Arrays.asList(commandLineRunner.run("pwd").getOutput().split("/"));
        assertEquals("openapi-generator-runner", outputPath.get(outputPath.size() - 1));
    }

    @Test
    public void shouldEnsureSingleRunnerMaintainsState() {
        CommandLineRunner commandLineRunner = new CommandLineRunner(false);
        int exitCode = commandLineRunner.run(Arrays.asList("HI=what", "echo $HI").iterator()).getExitCode();
        assertEquals(0, exitCode);
    }

    @Test
    public void shouldCheckDependencies() {
        CommandLineRunner commandLineRunner = new CommandLineRunner(false);
        assertTrue(Integer.parseInt(commandLineRunner.run("git --version").getOutput().substring(12).split("\\.")[0]) > 1);
        assertEquals(0, commandLineRunner.run("npm install @openapitools/openapi-generator-cli -g").getExitCode());
    }
}
