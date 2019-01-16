package com.flexicore.product.iot.request;

public class ExternalServerUserCreate {
    private String name;
    private String description;
    private String username;
    private String password;

    public String getName() {
        return name;
    }

    public <T extends ExternalServerUserCreate> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends ExternalServerUserCreate> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public String getUsername() {
        return username;
    }

    public <T extends ExternalServerUserCreate> T setUsername(String username) {
        this.username = username;
        return (T) this;
    }

    public String getPassword() {
        return password;
    }

    public <T extends ExternalServerUserCreate> T setPassword(String password) {
        this.password = password;
        return (T) this;
    }
}
