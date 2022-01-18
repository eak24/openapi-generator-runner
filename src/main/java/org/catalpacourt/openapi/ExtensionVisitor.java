package org.catalpacourt.openapi;

import org.catalpacourt.openapi.schema.*;

public class ExtensionVisitor {
    protected void visit(Extension extension) {
        visit(extension.getInfo());
        extension.getGenerators().forEach(this::visit);
    }

    protected void visit(String name, Generator generator) {
        generator.actions().forEach(a -> visit(a.name(), a));
    }

    protected void visit(Generate generate) {
    }

    protected void visit(Info info) {
        visit(info.getOpenapiGenerator());
    }

    protected void visit(String name, Action action) {
    }

    protected void visit(OpenApiGeneratorInfo openApiGeneratorInfo) {
    }
}
