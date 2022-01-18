package org.catalpacourt.openapi.schema;

public class Info {
    private String openapiGeneratorVersion;
    private boolean deploy;

    public String getOpenapiGeneratorVersion() {
        return openapiGeneratorVersion;
    }

    public void setOpenapiGeneratorVersion(String openapiGeneratorVersion) {
        this.openapiGeneratorVersion = openapiGeneratorVersion;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }
}
