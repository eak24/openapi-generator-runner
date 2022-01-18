package org.catalpacourt.openapi.schema;

public class Info {
    private OpenApiGeneratorInfo openapiGenerator;
    private boolean deploy;

    public OpenApiGeneratorInfo getOpenapiGenerator() {
        return openapiGenerator;
    }

    public void setOpenapiGenerator(OpenApiGeneratorInfo openapiGeneratorVersion) {
        this.openapiGenerator = openapiGeneratorVersion;
    }

    public boolean isDeploy() {
        return deploy;
    }

    public void setDeploy(boolean deploy) {
        this.deploy = deploy;
    }
}
