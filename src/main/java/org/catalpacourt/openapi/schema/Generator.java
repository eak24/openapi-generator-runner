package org.catalpacourt.openapi.schema;

import java.util.Arrays;
import java.util.List;

public class Generator {
    private String githubUrl;
    private Generate generate;
    private Action install;
    private Action test;
    private Action deploy;
    private String generatorName;

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public Generate getGenerate() {
        return generate;
    }

    public void setGenerate(Generate generate) {
        this.generate = generate;
    }

    public Action getInstall() {
        return install;
    }

    public void setInstall(Action install) {
        this.install = install;
    }

    public Action getTest() {
        return test;
    }

    public void setTest(Action test) {
        this.test = test;
    }

    public Action getDeploy() {
        return deploy;
    }

    public void setDeploy(Action deploy) {
        this.deploy = deploy;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }

    public List<Action> actions() {
        return Arrays.asList(install.name("install"), test.name("test"), deploy.name("deploy"));
    }
}
