package org.catalpacourt.openapi.schema;

import java.util.Map;

public class Extension {
    private Map<String, Generator> generators;
    private Info info;

    public Map<String, Generator> getGenerators() {
        return generators;
    }

    public void setGenerators(Map<String, Generator> generators) {
        this.generators = generators;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
