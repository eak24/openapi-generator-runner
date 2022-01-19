package org.catalpacourt.openapi;

import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.*;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtensionToProcessGraphConverter extends ExtensionVisitor<ProcessNode> {

    private final static File GENERATED_DIRECTORY = new File("generated");
    private final static File CURRENT_DIRECTORY = new File(".").getAbsoluteFile();
    private final String specLocation;
    private final ProcessNode root = new ProcessNode();

    public ExtensionToProcessGraphConverter(Extension extension, String specLocation) {
        this.specLocation = specLocation;
        root.getProcessBuilder().command("mkdir", "generated");
        visit(root.next(new ProcessNode(GENERATED_DIRECTORY)), extension);
    }

    @Override
    protected void visit(ProcessNode parent, Extension extension) {
        visit(parent, extension.getInfo());
        extension.getGenerators().forEach((n, g) -> this.visit(parent.last(), n, g));
        ProcessNode cleanup = new ProcessNode("cd ..");
        cleanup.getProcessBuilder().directory(new File("."));
        cleanup.next(new ProcessNode("rm -rf generated"));
        parent.afterLast(cleanup);
    }

    @Override
    protected void visit(ProcessNode parent, String name, Generator generator) {
        ProcessNode gitClone = new ProcessNode("git clone " + generator.getGenerate().getGithub() + " " + name);
        ProcessNode chDir = new ProcessNode("cd " + name);
        ProcessNode gitCheckout = new ProcessNode("git checkout " + generator.getGenerate().getTag());
        gitCheckout.getProcessBuilder().directory(new File(GENERATED_DIRECTORY, name));
        Map<String, String> options = generator.getGenerate().getOptions();
        options.put("-g", generator.getGeneratorName());
        options.put("-i", new File(CURRENT_DIRECTORY, specLocation).toString());
        options.put("-o", name);
        ProcessNode generate = new ProcessNode("npx @openapitools/openapi-generator-cli generate " + optionsToString(options));
        parent.next(gitClone).next(chDir).next(gitCheckout).next(generate);
        visit(parent.last(), "install", generator.getInstall());
        visit(parent.last(), "test", generator.getTest());
        visit(parent.last(), "deploy", generator.getDeploy());
    }

    private String optionsToString(Map<String, String> options) {
        return options.entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).collect(Collectors.joining(" "));
    }

    @Override
    protected void visit(ProcessNode parent, String name, Action action) {
        parent.next(action.getScript().stream().map(ProcessNode::new).collect(Collectors.toList()));
    }

    @Override
    protected void visit(ProcessNode parent, Info info) {
        super.visit(parent.next(new ProcessNode("npm install @openapitools/openapi-generator-cli -g")), info);
    }

    @Override
    protected void visit(ProcessNode parent, OpenApiGeneratorInfo openApiGeneratorInfo) {
        super.visit(parent.next((new ProcessNode("openapi-generator-cli version-manager set " + openApiGeneratorInfo.getVersion()))), openApiGeneratorInfo);
    }

    public ProcessNode getRoot() {
        return root;
    }
}
