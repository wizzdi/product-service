package com.flexicore.product.iot.request;

public class ExternalServerCreate {
    private String url;
    private String name;
    private String description;

    public String getUrl() {
        return url;
    }

    public <T extends ExternalServerCreate> T setUrl(String url) {
        this.url = url;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public <T extends ExternalServerCreate> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends ExternalServerCreate> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }
}
