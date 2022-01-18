package org.catalpacourt.openapi;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.catalpacourt.openapi.commandlinerunner.CommandLineRunner;
import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.Extension;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
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

    public OpenApiGeneratorRunner() {
        this.commandLineRunner = new CommandLineRunner(isDryRun);
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new OpenApiGeneratorRunner()).execute(args);
        System.exit(exitCode);
    }

    public ProcessNode createProcessGraph() {
        return new ExtensionVisitor(extension, spec.getAbsolutePath()).createProcessGraph();
    }

    @Override
    public Integer call() throws Exception {
        extension = new ObjectMapper().readValue(spec, Extension.class);
        return null;
    }

    public CommandLineRunner getCommandLineRunner() {
        return commandLineRunner;
    }

    public File getSpec() {
        return spec;
    }

    public void setSpec(File spec) throws IOException {
        ObjectMapper mapper = MapperWrapper.getMapper(spec.getAbsolutePath());
        TreeNode node = mapper.readValue(spec, JsonNode.class);
        extension = mapper.treeToValue(node.get("info").get("x-openapi-generator-runner"), Extension.class);
        this.spec = spec;
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
