package org.catalpacourt.openapi;

import org.catalpacourt.openapi.schema.*;

public class ExtensionVisitor<T> {
    protected void visit(T in, Extension extension) {
        visit(in, extension.getInfo());
        extension.getGenerators().forEach((n, g) -> this.visit(in, n, g));
    }

    protected void visit(T in, String name, Generator generator) {
        generator.actions().forEach(a -> visit(in, a.name(), a));
    }

    protected void visit(T in, Generate generate) {
    }

    protected void visit(T in, Info info) {
        visit(in, info.getOpenapiGenerator());
    }

    protected void visit(T in, String name, Action action) {
    }

    protected void visit(T in, OpenApiGeneratorInfo openApiGeneratorInfo) {
    }
}
