package org.catalpacourt.openapi;

import org.catalpacourt.openapi.schema.Action;
import org.catalpacourt.openapi.schema.Extension;

import java.util.Collections;
import java.util.Objects;

public class ExtensionDefaultsProcessor extends ExtensionVisitor<Extension> {
    public void addDefaults(Extension extension) {
        visit(extension, extension);
    }

    @Override
    protected void visit(Extension in, String name, Action action) {
        if (action.getGithub() != null) {
            if (Objects.equals(name, "deploy")) {
                action.setScript(Collections.singletonList("git status"));
            }
        }
        super.visit(in, name, action);
    }
}
