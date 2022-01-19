package org.catalpacourt.openapi.schema;

import java.util.HashMap;
import java.util.Map;

public class Generate {
    private String github;
    private String tag;
    private Map<String, String> options = new HashMap<>();

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
