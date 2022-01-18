package org.catalpacourt.openapi.commandlinerunner;

public class Result {
    private final Integer exitCode;
    private final String output;

    public Result(Integer exitCode, String output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutput() {
        return output;
    }
}
