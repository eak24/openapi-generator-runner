package org.catalpacourt.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.catalpacourt.openapi.commandlinerunner.CommandLineRunner;
import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.Extension;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "generate",
        description = "Generate, install, test and deploy the specified OpenApi spec."
)
public class OpenApiGeneratorRunner implements Callable<Integer> {

    private final CommandLineRunner commandLineRunner;
    @CommandLine.Parameters(index = "0", description = "OpenApi Spec.")
    private File spec;
    private Extension extension;
    private boolean isDryRun;

    public OpenApiGeneratorRunner(boolean isDryRun) {
        this.commandLineRunner = new CommandLineRunner(isDryRun);
    }

    public OpenApiGeneratorRunner() {
        this(false);
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new OpenApiGeneratorRunner()).execute(args);
        System.exit(exitCode);
    }

    public void addDefaults() {
        new ExtensionDefaultsProcessor().addDefaults(extension);
    }

    public ProcessNode createProcessGraph() {
        return new ExtensionToProcessGraphConverter(extension, spec.getPath()).getRoot();
    }

    @Override
    public Integer call() {
        List<ProcessBuilder> processes = new SequentialProcessor().linearizeAndConvertToProcessBuilders(createProcessGraph());
        return commandLineRunner.run(processes.iterator()).getExitCode();
    }

    public CommandLineRunner getCommandLineRunner() {
        return commandLineRunner;
    }

    public File getSpec() {
        return spec;
    }

    public void setSpec(File spec) throws IOException {
        ObjectMapper mapper = MapperWrapper.getMapper(spec.getAbsolutePath());
        JsonNode node = mapper.readValue(spec, JsonNode.class);
        extension = mapper.treeToValue(node.findValue(Extension.X_OPENAPI_GENERATOR_RUNNER), Extension.class);
        this.spec = spec;
        addDefaults();
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public boolean isDryRun() {
        return isDryRun;
    }

    public void setDryRun(boolean dryRun) {
        isDryRun = dryRun;
    }
}
