package org.catalpacourt.openapi.schema;

import java.util.List;

public class Action {
    private List<String> script;
    private String github;
    private String name;

    public List<String> getScript() {
        return script;
    }

    public void setScript(List<String> script) {
        this.script = script;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public Action name(String name) {
        this.name = name;
        return this;
    }

    public String name() {
        return this.name;
    }
}
