package org.catalpacourt.openapi;

import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.Extension;
import org.catalpacourt.openapi.schema.Generator;
import org.catalpacourt.openapi.schema.Info;
import org.catalpacourt.openapi.schema.OpenApiGeneratorInfo;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtensionToProcessGraphConverter extends ExtensionVisitor<ProcessNode> {

    private final Extension extension;
    private final String specLocation;
    private final ProcessNode root = new ProcessNode();

    public ExtensionToProcessGraphConverter(Extension extension, String specLocation) {
        this.extension = extension;
        this.specLocation = specLocation;
        visit(root, extension);
    }

    @Override
    protected void visit(ProcessNode parent, Extension extension) {
        visit(parent, extension.getInfo());
        extension.getGenerators().forEach((n, g) -> this.visit(parent.last(), n, g));
    }

    @Override
    protected void visit(ProcessNode parent, String name, Generator generator) {
        Map<String, String> options = generator.getGenerate().getOptions();
        options.putIfAbsent("-g", generator.getGeneratorName());
        options.putIfAbsent("-i", specLocation);
        options.putIfAbsent("-o", name);
        String optionsString = generator.getGenerate().getOptions().entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).collect(Collectors.joining(" "));
        ProcessNode node = new ProcessNode("npx @openapitools/openapi-generator-cli generate " + optionsString);
        node.getProcessBuilder().directory(new File(name));
        parent.next(node).next(new ProcessNode(generator.getInstall().getScript())).next(new ProcessNode(generator.getTest().getScript())).next(new ProcessNode(generator.getDeploy().getScript()));
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
