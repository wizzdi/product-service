package com.flexicore.product.request;

import com.flexicore.security.SecurityContext;

public class ConnectionHolder<ExternalServer extends com.flexicore.iot.ExternalServer> {

    private ExternalServer externalServer;
    private SecurityContext securityContext;

    public ConnectionHolder(ExternalServer externalServer) {
        this.externalServer = externalServer;
    }

    public ExternalServer getExternalServer() {
        return externalServer;
    }

    public <T extends ConnectionHolder<ExternalServer>> T setExternalServer(ExternalServer externalServer) {
        this.externalServer = externalServer;
        return (T) this;
    }

    public SecurityContext getSecurityContext() {
        return securityContext;
    }

    public <T extends ConnectionHolder<ExternalServer>> T setSecurityContext(SecurityContext securityContext) {
        this.securityContext = securityContext;
        return (T) this;
    }
}
