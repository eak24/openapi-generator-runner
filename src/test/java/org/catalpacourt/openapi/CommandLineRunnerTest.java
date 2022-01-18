package org.catalpacourt.openapi;

import org.catalpacourt.openapi.commandlinerunner.CommandLineRunner;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandLineRunnerTest {

    @Test
    public void shouldRunBasicCommands() {
        CommandLineRunner commandLineRunner = new CommandLineRunner(false);
        assertEquals("hi", commandLineRunner.run(new ProcessBuilder().command("echo", "hi")).getOutput());
        List<String> outputPath = Arrays.asList(commandLineRunner.run(new ProcessBuilder().command("pwd")).getOutput().split("/"));
        assertEquals("openapi-generator-runner", outputPath.get(outputPath.size() - 1));
    }

    @Test
    public void shouldEnsureSingleRunnerMaintainsState() {
        CommandLineRunner commandLineRunner = new CommandLineRunner(false);
        int exitCode = commandLineRunner.run(Arrays.asList(new ProcessBuilder().command("echo", "hi"), new ProcessBuilder().command("echo", "hi")).iterator()).getExitCode();
        assertEquals(0, exitCode);
    }

    @Test
    public void shouldCheckDependencies() {
        CommandLineRunner commandLineRunner = new CommandLineRunner(false);
        assertTrue(Integer.parseInt(commandLineRunner.run(new ProcessBuilder().command("git", "--version")).getOutput().substring(12).split("\\.")[0]) > 1);
        assertEquals(0, commandLineRunner.run(new ProcessBuilder().command("npm", "install", "@openapitools/openapi-generator-cli", "-g")).getExitCode());
    }
}
