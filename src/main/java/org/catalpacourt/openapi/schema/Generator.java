package org.catalpacourt.openapi.schema;

public class Generator {
    private String githubUrl;
    private Generate generate;
    private Script install;
    private Script test;
    private Script deploy;
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

    public Script getInstall() {
        return install;
    }

    public void setInstall(Script install) {
        this.install = install;
    }

    public Script getTest() {
        return test;
    }

    public void setTest(Script test) {
        this.test = test;
    }

    public Script getDeploy() {
        return deploy;
    }

    public void setDeploy(Script deploy) {
        this.deploy = deploy;
    }

    public String getGeneratorName() {
        return generatorName;
    }

    public void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }
}
