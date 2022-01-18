package org.catalpacourt.openapi;

import org.catalpacourt.openapi.commandlinerunner.ProcessNode;
import org.catalpacourt.openapi.schema.Extension;
import org.catalpacourt.openapi.schema.Generator;
import org.catalpacourt.openapi.schema.Info;
import org.catalpacourt.openapi.schema.Script;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

public class ExtensionVisitor {

    private final Extension extension;
    private final String specLocation;

    public ExtensionVisitor(Extension extension, String specLocation) {
        this.extension = extension;
        this.specLocation = specLocation;
    }

    public ProcessNode createProcessGraph() {
        ProcessNode node = createProcessGraph(extension.getInfo());
        for (Map.Entry<String, Generator> generator : extension.getGenerators().entrySet()) {
            String folderName = generator.getKey();
            ProcessNode processNode = generator(generator.getValue(), folderName);
            processNode.getProcessBuilder().directory(new File(folderName));
            node.addNext(processNode);
        }
        return node;
    }

    private ProcessNode createProcessGraph(Info info) {
        ProcessNode processNode = new ProcessNode("npm install @openapitools/openapi-generator-cli -g");
        if (info.getOpenapiGeneratorVersion() != null) {
            processNode.afterLast(new ProcessNode("openapi-generator-cli version-manager set " + info.getOpenapiGeneratorVersion()));
        }
        return processNode;
    }

    private ProcessNode generator(Generator generator, String outputLocation) {
        Map<String, String> options = generator.getGenerate().getOptions();
        options.putIfAbsent("-g", generator.getGeneratorName());
        options.putIfAbsent("-i", specLocation);
        options.putIfAbsent("-o", outputLocation);
        String optionsString = generator.getGenerate().getOptions().entrySet().stream().map(e -> e.getKey() + " " + e.getValue()).collect(Collectors.joining(" "));
        ProcessNode node = new ProcessNode("npx @openapitools/openapi-generator-cli generate " + optionsString);
        ProcessNode test = node.next(script(generator.getInstall())).next(script(generator.getTest()));
        if (extension.getInfo().isDeploy()) {
            ProcessNode deploy = script(generator.getDeploy());
            test.addNext(deploy);
        }
        return node;
    }

    private ProcessNode script(Script script) {
        return new ProcessNode(script.getScript());
    }
}
